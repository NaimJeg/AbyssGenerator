param(
    [int]$Port = 25586,
    [ValidateSet('server', 'client')][string]$Side = 'server',
    [string]$Script,
    [string]$Code,
    [string]$OutputFile,
    [int]$TimeoutMs = 15000
)
$ErrorActionPreference = 'Stop'
if ($Script) { $Code = Get-Content -LiteralPath $Script -Raw }
if (-not $Code) { throw 'Provide -Script or -Code' }
$request = @{
    jsonrpc = '2.0'; id = 1; method = 'tools/call'
    params = @{ name = 'eval_js'; arguments = @{ side = $Side; code = $Code; timeout_ms = $TimeoutMs } }
} | ConvertTo-Json -Depth 10 -Compress
$response = Invoke-RestMethod -Uri "http://127.0.0.1:$Port/mcp" -Method Post -ContentType 'application/json' -Body ([System.Text.Encoding]::UTF8.GetBytes($request)) -TimeoutSec ([Math]::Ceiling($TimeoutMs / 1000) + 5)
if ($response.error) { throw ($response.error | ConvertTo-Json -Compress) }
$result = $response.result.content[0].text | ConvertFrom-Json
if ($OutputFile) {
    $parent = Split-Path -Parent $OutputFile
    if ($parent) { New-Item -ItemType Directory -Force $parent | Out-Null }
    $result | ConvertTo-Json -Depth 30 | Set-Content -LiteralPath $OutputFile
}
if ($response.result.isError -or -not $result.ok) { throw ($result | ConvertTo-Json -Depth 20) }
if ($result.diagnostics) { throw "AutoJS diagnostics require review: $($result.diagnostics | ConvertTo-Json -Compress)" }
$assertion = $null
try { $assertion = $result.value | ConvertFrom-Json -ErrorAction Stop } catch { }
if ($null -ne $assertion -and $assertion.passed -eq $false) { throw "Acceptance gate failed: $($result.value)" }
$result.value

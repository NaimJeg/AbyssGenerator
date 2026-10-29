param([int]$StartupTimeoutSeconds = 90)
$ErrorActionPreference = 'Stop'
$workspace = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$invoke = Join-Path $PSScriptRoot 'Invoke-AutoJS.ps1'
$runDirectory = Join-Path $workspace 'build/acceptance/server'
$resultDirectory = Join-Path $workspace 'build/acceptance/results/baseline-run'
$serverProcess = $null
Set-Location $workspace
New-Item -ItemType Directory -Force $runDirectory, $resultDirectory | Out-Null

# Never take over an already running game instance.
$connection = [System.Net.Sockets.TcpClient]::new()
try {
    try { $connection.Connect('127.0.0.1', 25586) } catch { }
    if ($connection.Connected) { throw 'Port 25586 is occupied; this runner requires its own isolated server.' }
} finally { $connection.Dispose() }

& ./gradlew.bat compileJava compileTestJava test build --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Build failed' }
$worldName = 'baseline-' + [DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss-fff')
@"
level-name=$worldName
level-seed=104729
server-ip=127.0.0.1
server-port=25566
online-mode=false
view-distance=4
simulation-distance=2
spawn-protection=0
allow-flight=true
pause-when-empty-seconds=0
"@ | Set-Content (Join-Path $runDirectory 'server.properties')

function Start-TestServer([string]$Phase) {
    Write-Host "Starting isolated server: $Phase"
    $script:serverProcess = Start-Process -FilePath $env:ComSpec -ArgumentList '/d', '/c', 'gradlew.bat runAcceptanceServer --console=plain' -WorkingDirectory $workspace -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $resultDirectory "$Phase-launch.log") -RedirectStandardError (Join-Path $resultDirectory "$Phase-stderr.log")
    # Cache the native handle before a short-lived process can exit. Without it,
    # PowerShell's Start-Process wrapper can report a null ExitCode on Windows.
    $null = $script:serverProcess.Handle
    $deadline = [DateTime]::UtcNow.AddSeconds($StartupTimeoutSeconds)
    do {
        if ($script:serverProcess.HasExited) { throw "Server exited during $Phase startup; inspect launch log" }
        try {
            $ready = & $invoke -Code 'AutoJS.server() !== null' -TimeoutMs 1000
            if ($ready -eq 'true') { return }
        } catch { }
        Start-Sleep -Milliseconds 250
    } while ([DateTime]::UtcNow -lt $deadline)
    throw "Server readiness timeout during $Phase"
}
function Stop-TestServer {
    if ($null -eq $script:serverProcess) { return }
    $script:serverProcess.Refresh()
    if ($script:serverProcess.HasExited) {
        $exitCode = $script:serverProcess.ExitCode
        $script:serverProcess = $null
        if ($null -eq $exitCode -or $exitCode -ne 0) { throw "Server exited unexpectedly (code '$exitCode')" }
        return
    }
    Write-Host "Requesting normal server stop (PID $($script:serverProcess.Id))"
    try {
        & $invoke -Code "AutoJS.command('stop'); 'stop requested'" | Out-Null
    } catch {
        # stop can close AutoJS before the HTTP response is delivered. It is only
        # accepted as a clean shutdown if the launcher subsequently exits with 0.
        Write-Host "Shutdown endpoint closed; validating process exit: $_"
    }
    Write-Host 'Waiting for launcher exit'
    if (-not $script:serverProcess.WaitForExit(45000)) { throw 'Server did not finish saving and stop within 45 seconds' }
    $exitCode = $script:serverProcess.ExitCode
    $script:serverProcess = $null
    if ($null -eq $exitCode -or $exitCode -ne 0) { throw "Server did not report successful exit (code '$exitCode')" }
}

try {
    Start-TestServer 'write'
    & $invoke -Script (Join-Path $PSScriptRoot 'baseline.js') -OutputFile (Join-Path $resultDirectory 'write.json')
    Stop-TestServer
    Start-TestServer 'verify-clear'
    & $invoke -Script (Join-Path $PSScriptRoot 'persistence.js') -OutputFile (Join-Path $resultDirectory 'restart-markers.json')
    $clear = "var acceptancePhase = 'clear';`n" + (Get-Content (Join-Path $PSScriptRoot 'persistence.js') -Raw)
    & $invoke -Code $clear -OutputFile (Join-Path $resultDirectory 'clear.json')
    Stop-TestServer
    Start-TestServer 'verify-air'
    $air = "var acceptancePhase = 'air';`n" + (Get-Content (Join-Path $PSScriptRoot 'persistence.js') -Raw)
    & $invoke -Code $air -OutputFile (Join-Path $resultDirectory 'restart-air.json')
    Stop-TestServer
    @{ passed = $true; world = $worldName; seed = 104729; stages = @('write', 'restart-markers', 'clear', 'restart-air') } | ConvertTo-Json | Set-Content (Join-Path $resultDirectory 'summary.json')
} catch {
    Write-Host "Acceptance runner failed: $_"
    throw
} finally {
    try { Stop-TestServer } catch { Write-Warning "Cleanup failed: $_" }
}

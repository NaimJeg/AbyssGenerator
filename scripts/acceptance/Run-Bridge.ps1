param([int]$StartupTimeoutSeconds = 90)
$ErrorActionPreference = 'Stop'
$workspace = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$invoke = Join-Path $PSScriptRoot 'Invoke-AutoJS.ps1'
$runDirectory = Join-Path $workspace 'build/acceptance/bridge-server'
$resultDirectory = Join-Path $workspace ('build/acceptance/results/bridge-' + [DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss-fff'))
$properties = Join-Path $runDirectory 'server.properties'
$originalProperties = if (Test-Path -LiteralPath $properties) { [IO.File]::ReadAllBytes($properties) } else { $null }
$serverProcess = $null
Set-Location $workspace
foreach ($port in @(25568, 25588)) {
    $connection = [Net.Sockets.TcpClient]::new()
    try {
        try { $connection.Connect('127.0.0.1', $port) } catch { }
        if ($connection.Connected) { throw "Port $port is occupied; refusing to use another server." }
    } finally { $connection.Dispose() }
}
& ./gradlew.bat test build --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Build failed' }
New-Item -ItemType Directory -Force $runDirectory, $resultDirectory | Out-Null
$worldName = 'bridge-' + [DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss-fff')
try {
    @"
level-name=$worldName
level-seed=104729
server-ip=127.0.0.1
server-port=25568
online-mode=false
view-distance=4
simulation-distance=2
spawn-protection=0
pause-when-empty-seconds=0
"@ | Set-Content -LiteralPath $properties
    $serverProcess = Start-Process -FilePath $env:ComSpec -ArgumentList '/d', '/c', 'gradlew.bat runBridgeServer --console=plain' -WorkingDirectory $workspace -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $resultDirectory 'launch.log') -RedirectStandardError (Join-Path $resultDirectory 'stderr.log')
    $null = $serverProcess.Handle
    $deadline = [DateTime]::UtcNow.AddSeconds($StartupTimeoutSeconds)
    $ready = $false
    do {
        if ($serverProcess.HasExited) { throw 'Bridge server exited during startup; inspect launch.log' }
        try { $ready = (& $invoke -Port 25588 -Code 'AutoJS.server() !== null' -TimeoutMs 1000) -eq 'true' } catch { }
        if (-not $ready) { Start-Sleep -Milliseconds 250 }
    } while (-not $ready -and [DateTime]::UtcNow -lt $deadline)
    if (-not $ready) { throw 'Bridge server readiness timeout' }
    & $invoke -Port 25588 -Script (Join-Path $PSScriptRoot 'bridge.js') -OutputFile (Join-Path $resultDirectory 'bridge.json')
    $startPrepare = "var acceptancePhase = 'start';`n" + (Get-Content (Join-Path $PSScriptRoot 'bridge-prepare.js') -Raw)
    & $invoke -Port 25588 -Code $startPrepare -OutputFile (Join-Path $resultDirectory 'prepare-start.json')
    $verify = "var acceptancePhase = 'verify';`n" + (Get-Content (Join-Path $PSScriptRoot 'bridge-prepare.js') -Raw)
    $deadline = [DateTime]::UtcNow.AddSeconds(40)
    do {
        $result = & $invoke -Port 25588 -Code $verify -OutputFile (Join-Path $resultDirectory 'prepare.json')
        $report = $result | ConvertFrom-Json
        if (-not $report.ready) { Start-Sleep -Milliseconds 250 }
    } while (-not $report.ready -and [DateTime]::UtcNow -lt $deadline)
    if (-not $report.ready) { throw 'Administrative preparation did not complete before deadline' }
    Write-Output $result
    $startGeneration = "var acceptancePhase = 'start';`n" + (Get-Content (Join-Path $PSScriptRoot 'generation-probe.js') -Raw)
    & $invoke -Port 25588 -Code $startGeneration
    $verifyGeneration = "var acceptancePhase = 'verify';`n" + (Get-Content (Join-Path $PSScriptRoot 'generation-probe.js') -Raw)
    $deadline = [DateTime]::UtcNow.AddSeconds(90)
    do {
        $result = & $invoke -Port 25588 -Code $verifyGeneration -OutputFile (Join-Path $resultDirectory 'generation.json')
        $report = $result | ConvertFrom-Json
        if (-not $report.ready) { Start-Sleep -Milliseconds 250 }
    } while (-not $report.ready -and [DateTime]::UtcNow -lt $deadline)
    if (-not $report.ready) { throw 'Generation experiments timed out' }
    Write-Output $result
    # Leave the gate failure visible. Passing the administrative probe does not pass P1.
    & $invoke -Port 25588 -Script (Join-Path $PSScriptRoot 'bridge-gate.js') -OutputFile (Join-Path $resultDirectory 'gate.json')
} finally {
    $exitFailure = $null
    if ($null -ne $serverProcess) {
        if (-not $serverProcess.HasExited) {
            try { & $invoke -Port 25588 -Code "AutoJS.command('stop'); 'stop requested'" | Out-Null }
            catch { Write-Host 'Shutdown endpoint closed; checking normal process exit.' }
            if (-not $serverProcess.WaitForExit(45000)) { throw 'Bridge server did not stop within 45 seconds; configuration preserved for inspection.' }
        }
        if ($serverProcess.ExitCode -ne 0) { $exitFailure = "Bridge server exited with code $($serverProcess.ExitCode)." }
    }
    if ($null -ne $originalProperties) { [IO.File]::WriteAllBytes($properties, $originalProperties) }
    else { Remove-Item -LiteralPath $properties -ErrorAction SilentlyContinue }
    Write-Host "Bridge reports: $resultDirectory"
    if ($null -ne $exitFailure) { throw $exitFailure }
}

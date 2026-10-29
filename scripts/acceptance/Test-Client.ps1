param([int]$ServerPort = 25586, [int]$ClientPort = 25587)
$ErrorActionPreference = 'Stop'
$invoke = Join-Path $PSScriptRoot 'Invoke-AutoJS.ps1'
$results = @()
foreach ($targetY in @(-1024, 0, 1024)) {
    $prepare = "AutoJS.command('execute in abyssgenerator:abyss run wideytest write $targetY'); AutoJS.command('execute as @a at @s run wideytest tp $targetY'); 'requested'"
    & $invoke -Port $ServerPort -Code $prepare | Out-Null
    $deadline = [DateTime]::UtcNow.AddSeconds(30)
    $sample = $null
    do {
        $readback = @"
(function () {
 var mc = Java.loadClass('net.minecraft.client.Minecraft').getInstance();
 var Pos = Java.loadClass('net.minecraft.core.BlockPos');
 var Blocks = Java.loadClass('net.minecraft.world.level.block.Blocks');
 if (mc.level === null || mc.player === null) return JSON.stringify({ready:false});
 var y = mc.player.getY();
 var marker = mc.level.getBlockState(new Pos(8, $targetY, 8)).is(Blocks.DIAMOND_BLOCK);
 return JSON.stringify({ready:Math.abs(y - $targetY) < 0.01 && marker,
   y:y, marker:marker, dimension:String(mc.level.dimension().identifier()),
   rendering:String(mc.levelRenderer.getSectionStatistics())});
})();
"@
        $sample = (& $invoke -Port $ClientPort -Side client -Code $readback) | ConvertFrom-Json
        if ($sample.ready) { break }
        Start-Sleep -Milliseconds 200
    } while ([DateTime]::UtcNow -lt $deadline)
    if (-not $sample.ready -or $sample.dimension -ne 'abyssgenerator:abyss') { throw "Client readback failed at $targetY : $($sample | ConvertTo-Json -Compress)" }
    $results += $sample
}
New-Item -ItemType Directory -Force build/acceptance/results | Out-Null
$results | ConvertTo-Json -Depth 10 | Set-Content build/acceptance/results/p0-client.json
$results | ConvertTo-Json -Depth 10

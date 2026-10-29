(function () {
    var mc = Java.loadClass('net.minecraft.client.Minecraft').getInstance();
    var Pos = Java.loadClass('net.minecraft.core.BlockPos');
    var Blocks = Java.loadClass('net.minecraft.world.level.block.Blocks');
    var Geometry = Java.loadClass('cc.sighs.abyssgenerator.world.generation.MengerSponge');
    function check(value, message) { if (!value) throw new Error(message); }
    check(mc.player !== null && mc.level !== null, 'client not ready');
    check(String(mc.level.dimension().identifier()) === 'minecraft:overworld', 'wrong dimension');
    check(Math.abs(mc.player.getX() - 24.5) < .01 && Math.abs(mc.player.getY() - 16) < .01
        && Math.abs(mc.player.getZ() - .5) < .01, 'fresh player did not spawn on the natural floor');
    check(mc.level.getBlockState(new Pos(24,15,0)).is(Blocks.STONE), 'client spawn floor missing');
    check(mc.level.getBlockState(new Pos(24,16,0)).isAir() && mc.level.getBlockState(new Pos(24,17,0)).isAir(), 'client spawn obstructed');
    var ys = [-2032,-1024,-17,-1,0,15,16,31,32,47,48,1024,2031];
    for (var i = 0; i < ys.length; i++) {
        var state = mc.level.getBlockState(new Pos(24,ys[i],0));
        check(Geometry.isSolid(24,ys[i],0) ? state.is(Blocks.STONE) : state.isAir(), 'client data mismatch at ' + ys[i]);
    }
    return JSON.stringify({passed:true, player:String(mc.player.getName().getString()), x:mc.player.getX(),
        y:mc.player.getY(), z:mc.player.getZ(), samples:ys.length, rendering:String(mc.levelRenderer.getSectionStatistics())});
})();

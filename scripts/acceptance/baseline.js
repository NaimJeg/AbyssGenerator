(function () {
    var server = AutoJS.server();
    var ResourceKey = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Identifier = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var BlockPos = Java.loadClass('net.minecraft.core.BlockPos');
    var Blocks = Java.loadClass('net.minecraft.world.level.block.Blocks');
    var Harness = Java.loadClass('cc.sighs.abyssgenerator.WideYBaselineTest');
    var level = server.getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse('abyssgenerator:abyss')));
    function check(condition, description) { if (!condition) throw new Error(description); }
    check(level !== null, 'missing abyss dimension');
    check(level.getMinY() === -2032 && level.getHeight() === 4064, 'dimension bounds');
    check(!level.dimensionType().hasSkyLight(), 'skylight must be disabled');
    var source = server.createCommandSourceStack().withLevel(level);
    var dispatcher = server.getCommands().getDispatcher();
    var PermissionSet = Java.loadClass('net.minecraft.server.permissions.PermissionSet');
    check(!dispatcher.getRoot().getChild('wideytest').canUse(source.withPermission(PermissionSet.NO_PERMISSIONS)), 'unprivileged command access');
    function command(text) { return dispatcher.execute(text, source); }
    check(command('wideytest selfcheck') === 1, 'selfcheck');
    var before = level.getChunkSource().getLoadedChunksCount();
    check(command('wideytest probe 0') === 1, 'probe');
    check(level.getChunkSource().getLoadedChunksCount() === before, 'probe loaded a column');
    check(command('wideytest write -2033') === 0, 'lower out-of-range write accepted');
    check(command('wideytest write 2032') === 0, 'upper out-of-range write accepted');
    check(level.getChunkSource().getLoadedChunksCount() === before, 'rejected write loaded a column');
    var ys = [-2032,-2031,-1024,-257,-256,-255,-17,-16,-1,0,15,16,255,256,257,1024,2031];
    for (var i = 0; i < ys.length; i++) check(command('wideytest write ' + ys[i]) === 1, 'write ' + ys[i]);
    for (var j = 0; j < ys.length; j++) {
        check(command('wideytest verify ' + ys[j]) === 1, 'verify ' + ys[j]);
        check(level.getBlockState(new BlockPos(8, ys[j], 8)).is(Blocks.DIAMOND_BLOCK), 'independent readback ' + ys[j]);
    }
    check(server.overworld().getMinY() === -64 && server.overworld().getHeight() === 384, 'overworld bounds changed');
    check(command('wideytest save') === 1, 'flush');
    return JSON.stringify({phase: 'P0-baseline', passed: true, samples: ys.length, stats: String(Harness.stats(level))});
})();

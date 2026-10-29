(function () {
    var server = AutoJS.server();
    var Key = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Id = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var Pos = Java.loadClass('net.minecraft.core.BlockPos');
    var Blocks = Java.loadClass('net.minecraft.world.level.block.Blocks');
    var Manager = Java.loadClass('cc.sighs.abyssgenerator.world.section.SectionManager');
    var SectionKey = Java.loadClass('cc.sighs.abyssgenerator.world.coord.SectionKey');
    var Harness = Java.loadClass('cc.sighs.abyssgenerator.WideYBaselineTest');
    var level = server.getLevel(Key.create(Registries.DIMENSION, Id.parse('abyssgenerator:bridge_fixture')));
    var source = server.createCommandSourceStack().withLevel(level);
    var dispatcher = server.getCommands().getDispatcher();
    function check(condition, description) { if (!condition) throw new Error(description); }
    var chunk = level.getChunk(0, 0);
    var Unknown = Java.loadClass('cc.sighs.abyssgenerator.world.bridge.UnknownSection');
    var unknownCount = 0;
    for (var s = 0; s < chunk.getSections().length; s++) if (chunk.getSection(s) instanceof Unknown) unknownCount++;
    check(unknownCount >= 251, 'eager section allocation: only ' + unknownCount + ' unknown slots');
    var empty = chunk.getSection(level.getSectionIndex(512));
    check(empty instanceof Unknown, 'expected unknown section at 512');
    var writeRejected = false;
    try { empty.setBlockState(0, 0, 0, Blocks.GOLD_BLOCK.defaultBlockState()); } catch (e) { writeRejected = true; }
    check(writeRejected, 'shared empty view accepted mutation');
    empty.getStates().getAndSet(0, 0, 0, Blocks.GOLD_BLOCK.defaultBlockState());
    check(empty.getBlockState(0, 0, 0).isAir(), 'palette accessor leaked mutable shared data');
    check(dispatcher.execute('wideytest write 512', source) === 0, 'unknown write was not rejected');
    var ys = [-1024, 0, 1024];
    for (var i = 0; i < ys.length; i++) {
        check(dispatcher.execute('wideytest ensure ' + ys[i], source) === 1, 'ensure ' + ys[i]);
        var record = Manager.of(level).ensureSection(SectionKey.fromBlock(8, ys[i], 8));
        check(record.data() === chunk.getSection(level.getSectionIndex(ys[i])), 'manager/vanilla identity mismatch');
        check(dispatcher.execute('wideytest write ' + ys[i], source) === 1, 'write ' + ys[i]);
    }
    for (var j = 0; j < ys.length; j++) check(level.getBlockState(new Pos(8, ys[j], 8)).is(Blocks.DIAMOND_BLOCK), 'marker alias at ' + ys[j]);
    check(chunk.getSection(level.getSectionIndex(512)).getBlockState(8, 0, 8).isAir(), 'write polluted unknown section');
    check(dispatcher.execute('wideytest save', source) === 1, 'save');
    return JSON.stringify({phase: 'P1-sparse-server', passed: true, unknownSlotsBefore: unknownCount, stats: String(Harness.stats(level))});
})();

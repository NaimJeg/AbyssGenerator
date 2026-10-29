(function () {
    var phase = typeof acceptancePhase === 'undefined' ? 'verify' : acceptancePhase;
    if (['verify', 'clear', 'air'].indexOf(phase) < 0) throw new Error('unknown phase');
    var server = AutoJS.server();
    var Key = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Id = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var Unknown = Java.loadClass('cc.sighs.abyssgenerator.world.bridge.UnknownSection');
    var level = server.getLevel(Key.create(Registries.DIMENSION, Id.parse('abyssgenerator:bridge_fixture')));
    var source = server.createCommandSourceStack().withLevel(level);
    var dispatcher = server.getCommands().getDispatcher();
    var chunk = level.getChunk(0, 0);
    function command(text) { if (dispatcher.execute(text, source) !== 1) throw new Error(text); }
    var ys = [-1024, 0, 1024];
    for (var i = 0; i < ys.length; i++) {
        if (chunk.getSection(level.getSectionIndex(ys[i])) instanceof Unknown) throw new Error('saved section lost: ' + ys[i]);
        if (phase === 'clear' && ys[i] === 0) command('wideytest clear 0');
        command('wideytest ' + (ys[i] === 0 && phase !== 'verify' ? 'verifyAir' : 'verify') + ' ' + ys[i]);
    }
    if (!(chunk.getSection(level.getSectionIndex(512)) instanceof Unknown)) throw new Error('unknown section materialized after save');
    command('wideytest save');
    return JSON.stringify({phase: 'bridge-' + phase, passed: true, knownSections: 3, unknownSlots: 251});
})();

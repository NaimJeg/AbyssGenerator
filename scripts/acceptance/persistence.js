// Run once after baseline + restart (verify), then with acceptancePhase='clear';
// restart again and run with acceptancePhase='air'. Each phase reads actual blocks.
(function () {
    var phase = typeof acceptancePhase === 'undefined' ? 'verify' : acceptancePhase;
    if (['verify', 'clear', 'air'].indexOf(phase) < 0) throw new Error('unknown phase');
    var server = AutoJS.server();
    var Key = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Id = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var level = server.getLevel(Key.create(Registries.DIMENSION, Id.parse('abyssgenerator:abyss')));
    var source = server.createCommandSourceStack().withLevel(level);
    var dispatcher = server.getCommands().getDispatcher();
    function command(text) { if (dispatcher.execute(text, source) !== 1) throw new Error(text); }
    var ys = [-2032,-2031,-1024,-257,-256,-255,-17,-16,-1,0,15,16,255,256,257,1024,2031];
    for (var i = 0; i < ys.length; i++) {
        if (phase === 'clear') command('wideytest clear ' + ys[i]);
        command('wideytest ' + (phase === 'verify' ? 'verify' : 'verifyAir') + ' ' + ys[i]);
    }
    command('wideytest save');
    return JSON.stringify({phase: phase, passed: true, samples: ys.length});
})();

// Check on completion before normal unloading, then poll the detached report.
(function () {
    var Probe = Java.loadClass('cc.sighs.abyssgenerator.SectionPreparationProbe');
    if (typeof acceptancePhase !== 'undefined' && acceptancePhase === 'verify') return String(Probe.report());
    var Key = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Id = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var level = AutoJS.server().getLevel(Key.create(Registries.DIMENSION, Id.parse('abyssgenerator:bridge_fixture')));
    return String(Probe.start(level, typeof acceptanceChunkX === 'undefined' ? 64 : acceptanceChunkX));
})();

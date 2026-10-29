(function () {
    var Probe = Java.loadClass('cc.sighs.abyssgenerator.SectionGenerationProbe');
    if (typeof acceptancePhase !== 'undefined' && acceptancePhase === 'verify') return String(Probe.report());
    Probe.start(AutoJS.server());
    return JSON.stringify({phase: 'P1-generation-experiments-start', ready: false});
})();

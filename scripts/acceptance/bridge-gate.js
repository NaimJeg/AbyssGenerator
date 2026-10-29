(function () {
    var server = AutoJS.server();
    var Key = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Id = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var Pos = Java.loadClass('net.minecraft.core.BlockPos');
    var Packet = Java.loadClass('net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData');
    var Section = Java.loadClass('net.minecraft.world.level.chunk.LevelChunkSection');
    var Unknown = Java.loadClass('cc.sighs.abyssgenerator.world.bridge.UnknownSection');
    var level = server.getLevel(Key.create(Registries.DIMENSION, Id.parse('abyssgenerator:bridge_fixture')));
    var chunk = level.getChunk(0, 0);
    var packet = new Packet(chunk);
    var buffer = packet.getReadBuffer();
    var bytes = buffer.readableBytes();
    var section = new Section(level.palettedContainerFactory());
    var serialized = 0, nonAir = 0;
    try {
        while (buffer.isReadable()) {
            section.read(buffer);
            serialized++;
            if (!section.hasOnlyAir()) nonAir++;
        }
    } finally { buffer.release(); }
    var unknownY = 512;
    if (!(chunk.getSection(level.getSectionIndex(unknownY)) instanceof Unknown)) throw new Error('gate requires an unknown section');
    var unknownReadsAir = level.getBlockState(new Pos(8, unknownY, 8)).isAir();
    var blockers = [];
    // Call the Java record accessors directly: Rhino exposes records to Gson as
    // member descriptions rather than their field values when passed from JS.
    var noise = level.abyssgenerator$generationAudit().snapshot().get('minecraft:noise');
    var generationAudited = noise !== null;
    var wholeHeightInput = generationAudited && noise.maxInputSectionSlots() > 1;
    if (!generationAudited) blockers.push('Generation dependency scope was not measured; run the cold preparation probe first.');
    else if (wholeHeightInput) blockers.push('The preparation still schedules vanilla stages with whole-height column inputs; no section generation takeover.');
    if (nonAir > 0) blockers.push('Vanilla column packets still carry ready block data; section snapshot ownership is not implemented.');
    if (unknownReadsAir) blockers.push('Vanilla read paths cannot distinguish unknown from air; the spike must remain closed to gameplay.');
    var report = {
        phase: 'P1-gate-audit',
        passed: blockers.length === 0,
        columnPacketSections: serialized,
        columnPacketNonAirSections: nonAir,
        columnPacketBytes: bytes,
        unknownVanillaReadIsAir: unknownReadsAir,
        generationAudited: generationAudited,
        generationUsesWholeHeightInput: wholeHeightInput,
        generationNoiseMaxInputSections: generationAudited ? noise.maxInputSectionSlots() : null,
        blockers: blockers
    };
    return JSON.stringify(report);
})();

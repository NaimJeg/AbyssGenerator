(function () {
    var server = AutoJS.server();
    var Key = Java.loadClass('net.minecraft.resources.ResourceKey');
    var Id = Java.loadClass('net.minecraft.resources.Identifier');
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries');
    var Pos = Java.loadClass('net.minecraft.core.BlockPos');
    var Blocks = Java.loadClass('net.minecraft.world.level.block.Blocks');
    var Geometry = Java.loadClass('cc.sighs.abyssgenerator.world.generation.MengerSponge');
    var Generator = Java.loadClass('cc.sighs.abyssgenerator.world.generation.FractalChunkGenerator');
    var Bridge = Java.loadClass('cc.sighs.abyssgenerator.world.bridge.VanillaColumnBridge');
    var Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types');
    function check(value, message) { if (!value) throw new Error(message); }
    var overworld = server.overworld();
    var abyss = server.getLevel(Key.create(Registries.DIMENSION, Id.parse('abyssgenerator:abyss')));
    check(overworld.getChunkSource().getGenerator() instanceof Generator, 'world preset generator not selected');
    check(abyss.getChunkSource().getGenerator() instanceof Generator, 'abyss generator not selected');
    check(!Bridge.enabled(overworld) && !Bridge.enabled(abyss), 'unfinished sparse bridge enabled for terrain');
    check(overworld.getMinY() === -2032 && overworld.getHeight() === 4064, 'preset bounds');
    var samples = 0;
    var ys = [-2032,-2031,-1024,-49,-48,-17,-16,-1,0,15,16,17,31,32,47,48,49,1024,2031];
    var xs = [-17,-16,-1,0,15,16,24,31,32,47,48];
    for (var i = 0; i < xs.length; i++) for (var j = 0; j < ys.length; j++) {
        var x = xs[i], y = ys[j], z = xs[(i * 3 + j) % xs.length];
        var actual = overworld.getBlockState(new Pos(x,y,z));
        check(Geometry.isSolid(x,y,z) ? actual.is(Blocks.STONE) : actual.isAir(), 'terrain mismatch at ' + [x,y,z]);
        samples++;
    }
    var generator = overworld.getChunkSource().getGenerator();
    var random = overworld.getChunkSource().randomState();
    for (var k = 0; k < xs.length; k++) {
        var cx = xs[k], cz = 0;
        var chunk = overworld.getChunk(Math.floor(cx / 16), 0);
        var expectedHeight = generator.getBaseHeight(cx, cz, Heightmap.WORLD_SURFACE, overworld, random);
        check(expectedHeight === chunk.getHeight(Heightmap.WORLD_SURFACE, cx & 15, cz) + 1, 'heightmap mismatch');
        var column = generator.getBaseColumn(cx, cz, overworld, random);
        for (var n = 0; n < ys.length; n++) check(column.getBlock(ys[n]).equals(overworld.getBlockState(new Pos(cx,ys[n],cz))), 'base column mismatch');
    }
    check(overworld.getRespawnData().pos().equals(Generator.SPAWN), 'unsafe preset spawn selection');
    var spawn = Generator.SPAWN;
    check(overworld.getBlockState(spawn.below()).is(Blocks.STONE), 'spawn floor missing');
    check(overworld.getBlockState(spawn).isAir() && overworld.getBlockState(spawn.above()).isAir(), 'spawn obstructed');
    check(overworld.getBlockState(new Pos(0,-2033,0)).isAir(), 'below world bounds');
    check(overworld.getBlockState(new Pos(0,2032,0)).isAir(), 'above world bounds');
    // Exercise a second dimension and verify that cached palette templates remain immutable.
    var edit = new Pos(0,0,0);
    check(abyss.getBlockState(edit).is(Blocks.STONE), 'abyss terrain absent');
    overworld.setBlock(edit, Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
    check(overworld.getBlockState(new Pos(48,0,0)).is(Blocks.STONE), 'neighbor palette alias');
    check(abyss.getBlockState(edit).is(Blocks.STONE), 'dimension palette alias');
    overworld.setBlock(edit, Blocks.STONE.defaultBlockState(), 2);
    server.saveEverything(false, true, true);
    return JSON.stringify({passed:true, generator:'abyssgenerator:fractal', edge:48, depth:3,
        samples:samples, spawn:String(spawn), dimensions:2, height:overworld.getHeight()});
})();

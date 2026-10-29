package cc.sighs.abyssgenerator.world.generation;

import cc.sighs.abyssgenerator.world.coord.VerticalRange;
import cc.sighs.abyssgenerator.world.coord.SectionKey;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Util;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/** Vanilla generator integration, independent of the unfinished sparse bridge. */
public final class FractalChunkGenerator extends ChunkGenerator implements SectionTerrainGenerator {
    public static final MapCodec<FractalChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.retrieveElement(Biomes.THE_VOID))
                    .apply(instance, FractalChunkGenerator::new));
    public static final BlockPos SPAWN = new BlockPos(24, 16, 0);
    private final ConcurrentHashMap<Integer, PalettedContainer<BlockState>> templates = new ConcurrentHashMap<>();
    private final Holder<Biome> biome;

    public FractalChunkGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
    }
    @Override protected MapCodec<? extends ChunkGenerator> codec() { return CODEC; }

    @Override public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> sets, RandomState random, long seed) {
        return ChunkGeneratorStructureState.createForFlat(random, seed, biomeSource, Stream.empty());
    }

    @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random,
            StructureManager structures, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            var range = new VerticalRange(chunk.getMinY(), chunk.getHeight());
            for (int index = 0; index < chunk.getSections().length; index++) {
                int sectionY = chunk.getSectionYFromSectionIndex(index);
                var original = chunk.getSection(index);
                chunk.getSections()[index] = generateSection(
                        new SectionKey(chunk.getPos().x(), sectionY, chunk.getPos().z()), range,
                        new LevelChunkSection(original.getStates().recreate(), original.getBiomes().copy()));
            }
            Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG));
            return chunk;
        }, Util.backgroundExecutor().forName("abyss-fractal"));
    }

    @Override public LevelChunkSection generateSection(SectionKey key, VerticalRange range,
            PalettedContainerFactory factory) {
        return generateSection(key, range, new LevelChunkSection(factory));
    }

    private LevelChunkSection generateSection(SectionKey key, VerticalRange range, LevelChunkSection empty) {
        if (!range.containsSection(key.y()))
            throw new IllegalArgumentException("Section outside dimension: " + key);
        // This generator has one fixed biome; no noise sampler or world access is needed.
        empty.fillBiomesFromNoise((x, y, z, sampler) -> biome, null, 0, 0, 0);
        if (!VerticalRange.ABYSS.containsSection(key.y())) return empty;
        int phaseX = Math.floorMod(key.x(), 3);
        int phaseY = Math.floorMod(key.y(), 3);
        int phaseZ = Math.floorMod(key.z(), 3);
        int phase = (phaseX * 3 + phaseY) * 3 + phaseZ;
        var template = templates.computeIfAbsent(phase, ignored -> {
            var states = empty.getStates().recreate();
            for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++) {
                if (MengerSponge.isSolid(phaseX * 16 + x, phaseY * 16 + y, phaseZ * 16 + z))
                    states.getAndSetUnchecked(x, y, z, Blocks.STONE.defaultBlockState());
            }
            return states;
        });
        return new LevelChunkSection(template.copy(), empty.getBiomes().copy());
    }

    public BlockState sample(int x, int y, int z) {
        return VerticalRange.ABYSS.containsBlock(y) && MengerSponge.isSolid(x, y, z)
                ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }
    @Override public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor bounds, RandomState random) {
        for (int y = Math.min(bounds.getMaxY(), VerticalRange.ABYSS.maxYExclusive() - 1); y >= Math.max(bounds.getMinY(), getMinY()); y--)
            if (type.isOpaque().test(sample(x, y, z))) return y + 1;
        return bounds.getMinY();
    }
    @Override public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor bounds, RandomState random) {
        BlockState[] states = new BlockState[bounds.getHeight()];
        for (int i = 0; i < states.length; i++) states[i] = sample(x, bounds.getMinY() + i, z);
        return new NoiseColumn(bounds.getMinY(), states);
    }
    @Override public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState random, ChunkAccess chunk) {}
    @Override public void applyCarvers(WorldGenRegion region, long seed, RandomState random, BiomeManager biomes, StructureManager structures, ChunkAccess chunk) {}
    @Override public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structures) {}
    @Override public void spawnOriginalMobs(WorldGenRegion region) {}
    @Override public int getGenDepth() { return VerticalRange.ABYSS.height(); }
    @Override public int getMinY() { return VerticalRange.ABYSS.minY(); }
    @Override public int getSeaLevel() { return getMinY(); }
    @Override public int getSpawnHeight(LevelHeightAccessor bounds) { return SPAWN.getY(); }
    @Override public void addDebugScreenInfo(List<String> lines, RandomState random, BlockPos pos) {
        lines.add("Abyss Menger sponge: 48 blocks, depth 3");
    }
}

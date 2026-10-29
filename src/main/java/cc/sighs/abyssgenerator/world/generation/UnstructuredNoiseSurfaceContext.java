package cc.sighs.abyssgenerator.world.generation;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import java.util.*;

/**
 * Independent, task-local noise metadata for structure-free generation experiments.
 * Samples one X/Z column and one noise cell in Y per cache miss, never full chunks.
 * Dependencies may still span the full height; metrics expose that cost. This is
 * not an approved vanilla adapter: structure adaptation and work bounds remain open.
 */
public final class UnstructuredNoiseSurfaceContext implements SectionSurfaceContext {
    private record Cell(int x, int minY, int z) {}
    private final NoiseBasedChunkGenerator generator;
    private final RandomState random;
    private final LevelHeightAccessor bounds;
    private final ChunkPos position;
    private final Map<Cell, NoiseColumn> cells = new HashMap<>();
    private final SurfaceDepthProfile[] profiles = new SurfaceDepthProfile[256];
    private final int[] heights = new int[256];
    private long sampledBlocks;
    public UnstructuredNoiseSurfaceContext(NoiseBasedChunkGenerator generator, RandomState random,
            LevelHeightAccessor bounds, ChunkPos position) {
        this.generator = generator; this.random = random; this.bounds = bounds; this.position = position;
        Arrays.fill(heights, Integer.MIN_VALUE);
    }
    @Override public SurfaceDepthProfile column(int x, int z) {
        int index = index(x, z);
        if (profiles[index] == null) profiles[index] = new SampledSurfaceProfile(bounds.getMinY(), bounds.getMaxY() + 1, y -> {
            var state = noiseBlockState(x, y, z);
            return state.isAir() ? SurfaceColumnProfile.Material.AIR : state.getFluidState().isEmpty()
                    ? SurfaceColumnProfile.Material.SOLID : SurfaceColumnProfile.Material.FLUID;
        });
        return profiles[index];
    }
    @Override public int surfaceHeight(int x, int z) {
        int index = index(x, z);
        if (heights[index] == Integer.MIN_VALUE) {
            int y = Math.min(bounds.getMaxY(), generator.getMinY() + generator.getGenDepth() - 1);
            while (y >= bounds.getMinY() && noiseBlockState(x, y, z).isAir()) y--;
            heights[index] = y;
        }
        return heights[index];
    }
    @Override public boolean supportsBiomeExtensions() { return true; }
    @Override public BlockState noiseBlockState(int x, int y, int z) {
        index(x, z);
        if (bounds.isOutsideBuildHeight(y) || y < generator.getMinY() || y >= generator.getMinY() + generator.getGenDepth())
            return Blocks.AIR.defaultBlockState();
        int cellHeight = generator.generatorSettings().value().noiseSettings().getCellHeight();
        int cellY = Math.floorDiv(y, cellHeight) * cellHeight;
        var column = cells.computeIfAbsent(new Cell(x, cellY, z), key -> {
            sampledBlocks += cellHeight;
            return generator.getBaseColumn(position.getMinBlockX() + x, position.getMinBlockZ() + z,
                    LevelHeightAccessor.create(cellY, cellHeight), random);
        });
        return column.getBlock(y);
    }
    public long sampledBlocks() { return sampledBlocks; }
    public int sampledCellColumns() { return cells.size(); }
    /** Full-domain preliminary-surface metadata without filling any voxel column. */
    public net.minecraft.world.level.levelgen.NoiseChunk createSurfaceNoise(net.minecraft.world.level.chunk.PalettedContainerFactory factory) {
        var slots = new net.minecraft.world.level.chunk.LevelChunkSection[bounds.getSectionsCount()];
        Arrays.fill(slots, new net.minecraft.world.level.chunk.LevelChunkSection(factory));
        var view = new net.minecraft.world.level.chunk.ProtoChunk(position, net.minecraft.world.level.chunk.UpgradeData.EMPTY,
                slots, new net.minecraft.world.ticks.ProtoChunkTicks<>(), new net.minecraft.world.ticks.ProtoChunkTicks<>(), bounds, factory, null);
        var structures = new net.minecraft.world.level.StructureManager(null, null, null) {
            @Override public java.util.List<net.minecraft.world.level.levelgen.structure.StructureStart> startsForStructure(
                    ChunkPos pos, java.util.function.Predicate<net.minecraft.world.level.levelgen.structure.Structure> matcher) { return List.of(); }
        };
        return generator.createNoiseChunk(view, structures, net.minecraft.world.level.levelgen.blending.Blender.empty(), random);
    }
    private static int index(int x, int z) {
        if (x < 0 || x >= 16 || z < 0 || z >= 16) throw new IllegalArgumentException("Column outside chunk");
        return x * 16 + z;
    }
}

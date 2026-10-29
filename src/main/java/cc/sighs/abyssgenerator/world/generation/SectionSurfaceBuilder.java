package cc.sighs.abyssgenerator.world.generation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.worldgen.SurfaceRuleData;

/**
 * Evaluates vanilla surface rules only for the target section. Profiles and
 * heights must describe the original noise terrain, including unloaded regions.
 * Biome extensions require an explicit boundary sampler, never live-world reads.
 */
public final class SectionSurfaceBuilder {
    private SectionSurfaceBuilder() {}
    public record Metrics(int ruleEvaluations, int replacements, int extensionTargetWrites, int extensionBoundaryWrites) {}

    public static Metrics build(NoiseBasedChunkGenerator generator, RandomState random, BiomeManager biomes,
            Registry<Biome> biomeRegistry, LevelHeightAccessor worldBounds, ProtoChunk target,
            NoiseChunk noise, PalettedContainerFactory factory, SectionSurfaceContext metadata) {
        if (target.getSectionsCount() != 1) throw new IllegalArgumentException("Expected exactly one target section");
        var settings = generator.generatorSettings().value();
        // Exact structural checks, not generator IDs: datapacks may replace a
        // preset's rules. An unchanged default-material rule needs no metadata.
        if (settings.surfaceRule().equals(SurfaceRules.state(settings.defaultBlock()))
                && generator.getBiomeSource().possibleBiomes().stream().noneMatch(biome -> biome.is(Biomes.ERODED_BADLANDS)
                    || biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN))) return new Metrics(0, 0, 0, 0);
        boolean stockOverworld = settings.surfaceRule().equals(SurfaceRuleData.overworld());
        int startX = target.getPos().getMinBlockX(), startZ = target.getPos().getMinBlockZ();
        var pos = new BlockPos.MutableBlockPos();
        int[] heights = new int[256];
        java.util.Arrays.fill(heights, Integer.MIN_VALUE);
        // Preflight all required metadata and unsupported extensions before mutating output.
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
            if (metadata.column(x, z) == null) throw new IllegalStateException("Missing surface context");
            if (!metadata.supportsBiomeExtensions()) {
                int height = metadata.surfaceHeight(x, z) + 1;
                heights[x * 16 + z] = height - 1;
                var biome = biomes.getBiome(pos.set(startX + x, settings.useLegacyRandomSource() ? 0 : height, startZ + z));
                if (biome.is(Biomes.ERODED_BADLANDS) || biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN))
                    throw new UnsupportedOperationException("Surface biome extension requires boundary samples: " + biome);
            }
        }
        // SurfaceRules' steepness condition reads neighboring column heights.
        // Only reference slots: no extra voxel containers are generated. This
        // internal view is used for world bounds and height queries, never blocks.
        var slots = new LevelChunkSection[worldBounds.getSectionsCount()];
        java.util.Arrays.fill(slots, target.getSection(0));
        var heightView = new ProtoChunk(target.getPos(), UpgradeData.EMPTY, slots,
                new net.minecraft.world.ticks.ProtoChunkTicks<>(), new net.minecraft.world.ticks.ProtoChunkTicks<>(), worldBounds, factory, null) {
            @Override public int getHeight(Heightmap.Types type, int x, int z) {
                if (type != Heightmap.Types.WORLD_SURFACE_WG) throw new UnsupportedOperationException("Unprovided surface heightmap: " + type);
                int index = (x & 15) * 16 + (z & 15);
                if (heights[index] == Integer.MIN_VALUE) heights[index] = metadata.surfaceHeight(x & 15, z & 15);
                return heights[index];
            }
            @Override public net.minecraft.world.level.block.state.BlockState getBlockState(BlockPos pos) {
                throw new UnsupportedOperationException("Surface height view has no block data");
            }
            @Override public net.minecraft.world.level.block.state.BlockState setBlockState(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
                throw new UnsupportedOperationException("Surface height view is read-only");
            }
        };
        var context = new SurfaceRules.Context(random.surfaceSystem(), random, heightView, noise,
                biomes::getBiome, biomeRegistry, new WorldGenerationContext(generator, worldBounds));
        var rule = settings.surfaceRule().apply(context);
        var deepRule = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())),
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState()))).apply(context);
        int evaluations = 0, replacements = 0, extensionTargetWrites = 0, extensionBoundaryWrites = 0;
        boolean entirelyDeep = stockOverworld;
        for (int x = 0; x < 16 && entirelyDeep; x++) for (int z = 0; z < 16 && entirelyDeep; z++) {
            context.updateXZ(startX + x, startZ + z);
            entirelyDeep = target.getMaxY() < context.getMinSurfaceLevel()
                    && hasNonAirCeiling(target, metadata, worldBounds, x, z, pos);
        }
        // Use the shortcut only for the entire target. Mixing skipped columns
        // with surface columns would omit height changes observed by steepness.
        if (entirelyDeep) {
            for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
                int worldX = startX + x, worldZ = startZ + z;
                context.updateXZ(worldX, worldZ);
                for (int y = target.getMaxY(); y >= target.getMinY(); y--) {
                    if (target.getBlockState(pos.set(worldX, y, worldZ)) != settings.defaultBlock()) continue;
                    context.updateY(0, 0, Integer.MIN_VALUE, worldX, y, worldZ);
                    evaluations++;
                    var replacement = deepRule.tryApply(worldX, y, worldZ);
                    if (replacement != null) { replacements++; target.setBlockState(pos, replacement); }
                }
            }
            return new Metrics(evaluations, replacements, 0, 0);
        }
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
            int worldX = startX + x, worldZ = startZ + z;
            context.updateXZ(worldX, worldZ);
            if (heights[x * 16 + z] == Integer.MIN_VALUE) heights[x * 16 + z] = metadata.surfaceHeight(x, z);
            int startingHeight = heights[x * 16 + z] + 1;
            var biome = biomes.getBiome(pos.set(worldX, settings.useLegacyRandomSource() ? 0 : startingHeight, worldZ));
            var workspace = new ExtensionColumn(x, z, target, metadata, worldBounds, heights);
            if (biome.is(Biomes.ERODED_BADLANDS))
                random.surfaceSystem().erodedBadlandsExtension(workspace, worldX, worldZ, startingHeight, heightView);
            var column = metadata.column(x, z);
            if (workspace.minWritten <= workspace.maxWritten) column = column.withSolidFill(workspace.minWritten, workspace.maxWritten + 1);
            var originalColumn = column;
            var surfaceResults = new java.util.HashMap<Integer, net.minecraft.world.level.block.state.BlockState>();
            int[] localEvaluations = {0};
            java.util.function.IntFunction<net.minecraft.world.level.block.state.BlockState> surfaceAt = y -> surfaceResults.computeIfAbsent(y, sampleY -> {
                var state = workspace.preSurfaceBlock(sampleY);
                if (state != settings.defaultBlock()) return state;
                var depths = originalColumn.depthsAt(sampleY);
                context.updateY(depths.above(), depths.below(), depths.waterHeight(), worldX, sampleY, worldZ);
                localEvaluations[0]++;
                var replacement = rule.tryApply(worldX, sampleY, worldZ);
                return replacement != null ? replacement : state;
            });
            // Preserve height changes from surface holes above the target. Query
            // top-down until a retained block is found, as the vanilla heightmap
            // does after removing its highest block. Later columns see this height.
            if (metadata.supportsBiomeExtensions()) {
                int top = heights[x * 16 + z];
                while (top >= worldBounds.getMinY() && surfaceAt.apply(top).isAir()) heights[x * 16 + z] = --top;
            }
            for (int y = target.getMaxY(); y >= target.getMinY(); y--) {
                var state = target.getBlockState(pos.set(worldX, y, worldZ));
                if (state != settings.defaultBlock()) continue;
                var replacement = surfaceAt.apply(y);
                if (replacement != state) {
                    replacements++;
                    target.setBlockState(pos, replacement);
                    if (!replacement.getFluidState().isEmpty()) target.markPosForPostprocessing(pos);
                }
            }
            if (biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN)) {
                // Iceberg RNG consumption depends on surface-rule output even
                // outside the target (holes can replace stone with air/water).
                workspace.surfaceBoundary = surfaceAt;
                random.surfaceSystem().frozenOceanExtension(context.getMinSurfaceLevel(), biome.value(), workspace,
                        pos, worldX, worldZ, startingHeight);
            }
            extensionTargetWrites += workspace.targetWrites;
            extensionBoundaryWrites += workspace.writes.size() - workspace.targetWrites;
            evaluations += localEvaluations[0];
        }
        return new Metrics(evaluations, replacements, extensionTargetWrites, extensionBoundaryWrites);
    }

    private static boolean hasNonAirCeiling(ProtoChunk target, SectionSurfaceContext metadata, LevelHeightAccessor bounds,
            int x, int z, BlockPos.MutableBlockPos pos) {
        if (!target.getBlockState(pos.set(target.getPos().getMinBlockX() + x, target.getMaxY(), target.getPos().getMinBlockZ() + z)).isAir()) return true;
        if (!metadata.supportsBiomeExtensions()) return false;
        for (int y = target.getMaxY() + 1; y <= Math.min(bounds.getMaxY(), target.getMaxY() + 16); y++)
            if (!metadata.noiseBlockState(x, y, z).isAir()) return true;
        return false;
    }

    private static final class ExtensionColumn implements BlockColumn {
        private final int x, z;
        private final ProtoChunk target;
        private final SectionSurfaceContext source;
        private final LevelHeightAccessor bounds;
        private final int[] heights;
        private final java.util.Map<Integer, net.minecraft.world.level.block.state.BlockState> writes = new java.util.HashMap<>();
        private int minWritten = Integer.MAX_VALUE, maxWritten = Integer.MIN_VALUE;
        private int targetWrites;
        private java.util.function.IntFunction<net.minecraft.world.level.block.state.BlockState> surfaceBoundary;
        private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        ExtensionColumn(int x, int z, ProtoChunk target, SectionSurfaceContext source, LevelHeightAccessor bounds, int[] heights) {
            this.x = x; this.z = z; this.target = target; this.source = source; this.bounds = bounds; this.heights = heights;
        }
        @Override public net.minecraft.world.level.block.state.BlockState getBlock(int y) {
            if (bounds.isOutsideBuildHeight(y)) return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            if (target.isInsideBuildHeight(y)) return target.getBlockState(position(y));
            var written = writes.get(y);
            return written != null ? written : surfaceBoundary != null ? surfaceBoundary.apply(y) : source.noiseBlockState(x, y, z);
        }
        private net.minecraft.world.level.block.state.BlockState preSurfaceBlock(int y) {
            if (bounds.isOutsideBuildHeight(y)) return Blocks.AIR.defaultBlockState();
            var written = writes.get(y);
            if (written != null) return written;
            return target.isInsideBuildHeight(y) ? target.getBlockState(position(y)) : source.noiseBlockState(x, y, z);
        }
        @Override public void setBlock(int y, net.minecraft.world.level.block.state.BlockState state) {
            if (bounds.isOutsideBuildHeight(y)) return;
            writes.put(y, state);
            minWritten = Math.min(minWritten, y); maxWritten = Math.max(maxWritten, y);
            if (!state.isAir()) heights[x * 16 + z] = Math.max(heights[x * 16 + z], y);
            if (target.isInsideBuildHeight(y)) {
                targetWrites++;
                target.setBlockState(position(y), state);
                if (!state.getFluidState().isEmpty()) target.markPosForPostprocessing(position(y));
            }
        }
        private BlockPos position(int y) { return pos.set(target.getPos().getMinBlockX() + x, y, target.getPos().getMinBlockZ() + z); }
    }
}

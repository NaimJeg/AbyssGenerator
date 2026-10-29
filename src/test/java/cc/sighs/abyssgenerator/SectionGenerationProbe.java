package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.coord.SectionKey;
import cc.sighs.abyssgenerator.world.coord.VerticalRange;
import cc.sighs.abyssgenerator.world.generation.FractalChunkGenerator;
import cc.sighs.abyssgenerator.world.generation.SectionSurfaceBuilder;
import cc.sighs.abyssgenerator.world.generation.SectionSurfaceContext;
import cc.sighs.abyssgenerator.world.generation.SurfaceColumnProfile;
import cc.sighs.abyssgenerator.world.generation.UnstructuredNoiseSurfaceContext;
import com.google.gson.Gson;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Detached P1 differential experiments. Full columns are test oracles only.
 * Noise/surface tests intentionally omit structures, carving and decoration;
 * their results cannot establish complete vanilla generator compatibility.
 */
public final class SectionGenerationProbe {
    private static volatile String report = "{\"ready\":false}";
    private SectionGenerationProbe() {}
    public static String report() { return report; }

    public static void start(MinecraftServer server) {
        if (!server.isSameThread()) throw new IllegalStateException("Start on server thread");
        report = "{\"ready\":false}";
        var experiments = new ArrayList<CompletableFuture<Map<String, Object>>>();
        for (var level : server.getAllLevels()) {
            if (level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator generator) {
                for (var pos : List.of(new ChunkPos(-8, 5), new ChunkPos(0, 0), new ChunkPos(7, -11)))
                    experiments.add(noiseExperiment(level, generator, pos, "original", level.getSeed()));
                experiments.add(noiseExperiment(level, generator, new ChunkPos(0, 0), "original", 0));
                if (level.dimension() == Level.OVERWORLD) {
                    var biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
                    for (var biome : List.of(Biomes.ERODED_BADLANDS, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)) {
                        var fixed = new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(biome)), generator.generatorSettings());
                        for (var pos : List.of(new ChunkPos(-8, 5), new ChunkPos(0, 0), new ChunkPos(7, -11)))
                            experiments.add(noiseExperiment(level, fixed, pos, biome.identifier().toString(), level.getSeed()));
                    }
                    experiments.add(badlandsExtensionExperiment(level, generator));
                }
            }
            else if (level.getChunkSource().getGenerator() instanceof FractalChunkGenerator generator)
                experiments.add(nativeExperiment(level, generator));
        }
        CompletableFuture.allOf(experiments.toArray(CompletableFuture[]::new)).thenRun(() -> {
            var results = experiments.stream().map(CompletableFuture::join).toList();
            // An observed adapter mismatch is a successful experiment, NOT a passed P1 gate.
            boolean nativePassed = results.stream().anyMatch(r -> r.get("kind").equals("native")) && results.stream().filter(r -> r.get("kind").equals("native"))
                    .allMatch(r -> ((Number) r.get("mismatches")).intValue() == 0);
            boolean surfacePassed = results.stream().filter(r -> r.get("kind").equals("vanilla-clipping"))
                    .allMatch(r -> ((Number) r.get("contextSurfaceMismatches")).intValue() == 0
                            && ((Number) r.get("independentSurfaceMismatches")).intValue() == 0
                            && ((Number) r.get("singleSectionMismatches")).intValue() == 0);
            boolean extensionPassed = results.stream().filter(r -> r.get("kind").equals("badlands-extension-fixture"))
                    .allMatch(r -> ((Number) r.get("mismatches")).longValue() == 0 && ((Number) r.get("targetWrites")).longValue() > 0);
            report = new Gson().toJson(Map.of("phase", "P1-generation-experiments", "ready", true,
                    "passed", nativePassed && surfacePassed && extensionPassed, "results", results,
                    "scope", "Native and section surface checks with oracle and independent structure-free metadata. Complete vanilla pipeline and dependency work bounds remain unverified."));
        }).exceptionally(error -> {
            report = new Gson().toJson(Map.of("ready", true, "passed", false, "error", error.toString()));
            return null;
        });
    }

    private static CompletableFuture<Map<String, Object>> nativeExperiment(ServerLevel level, FractalChunkGenerator generator) {
        var bounds = LevelHeightAccessor.create(level.getMinY(), level.getHeight());
        var range = new VerticalRange(bounds.getMinY(), bounds.getHeight());
        var factory = level.palettedContainerFactory();
        String dimension = level.dimension().identifier().toString();
        var full = new ProtoChunk(new ChunkPos(-1, 1), UpgradeData.EMPTY, bounds, factory, null);
        return generator.fillFromNoise(Blender.empty(), null, null, full).thenApply(ignored -> {
            long mismatches = 0;
            for (int sy = bounds.getMinSectionY(); sy <= bounds.getMaxSectionY(); sy++) {
                var key = new SectionKey(-1, sy, 1);
                var section = generator.generateSection(key, range, factory);
                var repeated = generator.generateSection(key, range, factory);
                if (section == repeated || section.getStates() == repeated.getStates())
                    throw new IllegalStateException("Native generator shared a mutable section");
                var expected = full.getSection(full.getSectionIndexFromSectionY(sy));
                mismatches += differences(section, expected);
                for (int y = 0; y < 4; y++) for (int z = 0; z < 4; z++) for (int x = 0; x < 4; x++)
                    if (!section.getNoiseBiome(x, y, z).equals(expected.getNoiseBiome(x, y, z)))
                        throw new IllegalStateException("Native section biome differs from full-column reference");
                var old = repeated.getBlockState(0, 0, 0);
                section.setBlockState(0, 0, 0, net.minecraft.world.level.block.Blocks.GOLD_BLOCK.defaultBlockState());
                if (repeated.getBlockState(0, 0, 0) != old) throw new IllegalStateException("Native template mutation leaked");
            }
            return Map.<String, Object>of("kind", "native", "dimension", dimension,
                    "sectionsCompared", bounds.getSectionsCount(), "mismatches", mismatches);
        });
    }

    private static CompletableFuture<Map<String, Object>> noiseExperiment(ServerLevel level, NoiseBasedChunkGenerator generator, ChunkPos pos, String fixture, long seed) {
        var bounds = LevelHeightAccessor.create(level.getMinY(), level.getHeight());
        var factory = level.palettedContainerFactory();
        var random = RandomState.create(generator.generatorSettings().value(), level.registryAccess().lookupOrThrow(Registries.NOISE), seed);
        var biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
        String dimension = level.dimension().identifier().toString();
        // Beardifier is the only structure query in these two stages. An empty
        // answer isolates noise/surface without loading neighboring real columns.
        var structures = new StructureManager(null, null, null) {
            @Override public List<StructureStart> startsForStructure(ChunkPos pos, Predicate<Structure> matcher) { return List.of(); }
        };
        var full = new ProtoChunk(pos, UpgradeData.EMPTY, bounds, factory, null);
        var independent = new UnstructuredNoiseSurfaceContext(generator, random, bounds, pos);
        var independentNoise = independent.createSurfaceNoise(factory);
        return generator.fillFromNoise(Blender.empty(), random, structures, full).thenCompose(ignored -> {
            var original = Arrays.stream(full.getSections()).map(LevelChunkSection::copy).toList();
            var profiles = new SurfaceColumnProfile[256];
            var heights = new int[256];
            // Test-only extraction from the full-column oracle. Production must
            // supply independent generation metadata, never call this extraction.
            var samplePos = new net.minecraft.core.BlockPos.MutableBlockPos();
            for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
                var runs = new ArrayList<SurfaceColumnProfile.Run>();
                int start = bounds.getMinY();
                SurfaceColumnProfile.Material previous = null;
                for (int y = bounds.getMinY(); y <= bounds.getMaxY(); y++) {
                    var state = full.getBlockState(samplePos.set(pos.getMinBlockX() + x, y, pos.getMinBlockZ() + z));
                    var material = state.isAir() ? SurfaceColumnProfile.Material.AIR : state.getFluidState().isEmpty()
                            ? SurfaceColumnProfile.Material.SOLID : SurfaceColumnProfile.Material.FLUID;
                    if (previous != null && previous != material) { runs.add(new SurfaceColumnProfile.Run(start, y, previous)); start = y; }
                    previous = material;
                }
                runs.add(new SurfaceColumnProfile.Run(start, bounds.getMaxY() + 1, previous));
                profiles[x * 16 + z] = new SurfaceColumnProfile(bounds.getMinY(), bounds.getMaxY() + 1, runs);
                heights[x * 16 + z] = full.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            }
            var metadata = new SectionSurfaceContext() {
                @Override public SurfaceColumnProfile column(int x, int z) { return profiles[x * 16 + z]; }
                @Override public int surfaceHeight(int x, int z) { return heights[x * 16 + z]; }
                @Override public boolean supportsBiomeExtensions() { return true; }
                @Override public net.minecraft.world.level.block.state.BlockState noiseBlockState(int x, int y, int z) {
                    if (bounds.isOutsideBuildHeight(y)) return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
                    return original.get(bounds.getSectionIndex(y)).getBlockState(x, Math.floorMod(y, 16), z);
                }
            };
            // Use the noise source directly, avoiding live-world biome lookups and clipped palette clamping.
            var biomeManager = new BiomeManager((x, y, z) -> generator.getBiomeSource().getNoiseBiome(x, y, z, random.sampler()), BiomeManager.obfuscateSeed(seed));
            generator.buildSurface(full, new WorldGenerationContext(generator, bounds), random, structures, biomeManager, biomes, Blender.empty());
            var slices = new ArrayList<CompletableFuture<long[]>>();
            // Nether's dimension is taller than its noise settings. Do not invoke
            // surface/noise internals on sections outside the generator domain.
            int firstSection = Math.max(bounds.getMinSectionY(), Math.floorDiv(generator.getMinY(), 16));
            int lastSection = Math.min(bounds.getMaxSectionY(), Math.floorDiv(generator.getMinY() + generator.getGenDepth() - 1, 16));
            for (int sy = firstSection; sy <= lastSection; sy++) {
                int sectionY = sy;
                var sliceBounds = LevelHeightAccessor.create(sy * 16, 16);
                var slice = new ProtoChunk(pos, UpgradeData.EMPTY, sliceBounds, factory, null);
                slices.add(generator.fillFromNoise(Blender.empty(), random, structures, slice).thenApply(generated -> {
                    long noiseMismatch = differences(slice.getSection(0), original.get(bounds.getSectionIndexFromSectionY(sectionY)));
                    var corrected = new ProtoChunk(pos, UpgradeData.EMPTY, new LevelChunkSection[]{slice.getSection(0).copy()},
                            new net.minecraft.world.ticks.ProtoChunkTicks<>(), new net.minecraft.world.ticks.ProtoChunkTicks<>(), sliceBounds, factory, null);
                    var sampled = new ProtoChunk(pos, UpgradeData.EMPTY, new LevelChunkSection[]{slice.getSection(0).copy()},
                            new net.minecraft.world.ticks.ProtoChunkTicks<>(), new net.minecraft.world.ticks.ProtoChunkTicks<>(), sliceBounds, factory, null);
                    long singleSamples = 0, singleMismatch = 0;
                    if (sectionY == 0) {
                        var single = new ProtoChunk(pos, UpgradeData.EMPTY, new LevelChunkSection[]{slice.getSection(0).copy()},
                                new net.minecraft.world.ticks.ProtoChunkTicks<>(), new net.minecraft.world.ticks.ProtoChunkTicks<>(), sliceBounds, factory, null);
                        var singleContext = new UnstructuredNoiseSurfaceContext(generator, random, bounds, pos);
                        SectionSurfaceBuilder.build(generator, random, biomeManager, biomes, bounds, single,
                                singleContext.createSurfaceNoise(factory), factory, singleContext);
                        singleSamples = singleContext.sampledBlocks();
                        singleMismatch = differences(single.getSection(0), full.getSection(bounds.getSectionIndexFromSectionY(sectionY)));
                    }
                    // Preserve the original world bounds in rule context. Only the
                    // storage view is clipped, giving naive clipping its best chance.
                    generator.buildSurface(slice, new WorldGenerationContext(generator, bounds), random, structures, biomeManager, biomes, Blender.empty());
                    long surfaceMismatch = differences(slice.getSection(0), full.getSection(bounds.getSectionIndexFromSectionY(sectionY)));
                    // The full-domain preliminary-surface cache is part of the context,
                    // not the clipped target's noise bounds. Serialize access to this
                    // shared test oracle's cache; runtime callers must own their cache.
                    var surfaceNoise = full.getOrCreateNoiseChunk(chunk -> { throw new IllegalStateException("Missing oracle noise"); });
                    synchronized (surfaceNoise) {
                        SectionSurfaceBuilder.build(generator, random, biomeManager, biomes, bounds, corrected, surfaceNoise, factory, metadata);
                    }
                    long correctedMismatch = differences(corrected.getSection(0), full.getSection(bounds.getSectionIndexFromSectionY(sectionY)));
                    SectionSurfaceBuilder.Metrics metrics;
                    synchronized (independent) {
                        metrics = SectionSurfaceBuilder.build(generator, random, biomeManager, biomes, bounds, sampled, independentNoise, factory, independent);
                    }
                    long independentMismatch = differences(sampled.getSection(0), full.getSection(bounds.getSectionIndexFromSectionY(sectionY)));
                    return new long[]{noiseMismatch, surfaceMismatch, correctedMismatch, independentMismatch, singleSamples, singleMismatch, metrics.extensionTargetWrites()};
                }));
            }
            return CompletableFuture.allOf(slices.toArray(CompletableFuture[]::new)).thenApply(done -> {
                long noiseMismatch = 0, surfaceMismatch = 0, correctedMismatch = 0, independentMismatch = 0, singleSamples = 0, singleMismatch = 0, extensions = 0;
                for (var slice : slices) { var counts = slice.join(); noiseMismatch += counts[0]; surfaceMismatch += counts[1]; correctedMismatch += counts[2]; independentMismatch += counts[3]; singleSamples += counts[4]; singleMismatch += counts[5]; extensions += counts[6]; }
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("kind", "vanilla-clipping"); result.put("dimension", dimension); result.put("fixture", fixture);
                result.put("chunkX", pos.x()); result.put("chunkZ", pos.z()); result.put("sectionsCompared", slices.size());
                result.put("seed", seed);
                result.put("noiseMismatches", noiseMismatch); result.put("afterSurfaceMismatches", surfaceMismatch);
                result.put("contextSurfaceMismatches", correctedMismatch); result.put("independentSurfaceMismatches", independentMismatch);
                result.put("metadataSampledBlocks", independent.sampledBlocks()); result.put("metadataCellColumns", independent.sampledCellColumns());
                result.put("singleSectionMetadataSamples", singleSamples); result.put("singleSectionMismatches", singleMismatch);
                result.put("surfaceExtensionTargetWrites", extensions);
                result.put("clippingEquivalent", noiseMismatch == 0 && surfaceMismatch == 0);
                return result;
            });
        });
    }

    private static long differences(LevelChunkSection actual, LevelChunkSection expected) {
        long different = 0;
        for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++)
            if (actual.getBlockState(x, y, z) != expected.getBlockState(x, y, z)) different++;
        return different;
    }

    private static CompletableFuture<Map<String, Object>> badlandsExtensionExperiment(ServerLevel level, NoiseBasedChunkGenerator originalGenerator) {
        var bounds = LevelHeightAccessor.create(level.getMinY(), level.getHeight());
        var factory = level.palettedContainerFactory();
        var biomeRegistry = level.registryAccess().lookupOrThrow(Registries.BIOME);
        var badlands = biomeRegistry.getOrThrow(Biomes.ERODED_BADLANDS);
        var generator = new NoiseBasedChunkGenerator(new FixedBiomeSource(badlands), originalGenerator.generatorSettings());
        var random = RandomState.create(generator.generatorSettings().value(), level.registryAccess().lookupOrThrow(Registries.NOISE), 104729);
        var biomeManager = new BiomeManager((x, y, z) -> badlands, BiomeManager.obfuscateSeed(104729));
        var stone = generator.generatorSettings().value().defaultBlock();
        var air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        var profile = new SurfaceColumnProfile(bounds.getMinY(), bounds.getMaxY() + 1, List.of(
                new SurfaceColumnProfile.Run(bounds.getMinY(), 33, SurfaceColumnProfile.Material.SOLID),
                new SurfaceColumnProfile.Run(33, bounds.getMaxY() + 1, SurfaceColumnProfile.Material.AIR)));
        var metadata = new SectionSurfaceContext() {
            @Override public SurfaceColumnProfile column(int x, int z) { return profile; }
            @Override public int surfaceHeight(int x, int z) { return 32; }
            @Override public boolean supportsBiomeExtensions() { return true; }
            @Override public net.minecraft.world.level.block.state.BlockState noiseBlockState(int x, int y, int z) {
                return bounds.isInsideBuildHeight(y) && y <= 32 ? stone : air;
            }
        };
        return CompletableFuture.supplyAsync(() -> {
            long mismatches = 0, writes = 0;
            for (var position : List.of(new ChunkPos(-8, 5), new ChunkPos(0, 0), new ChunkPos(7, -11))) {
                var full = new ProtoChunk(position, UpgradeData.EMPTY, bounds, factory, null);
                for (int sy = bounds.getMinSectionY(); sy <= bounds.getMaxSectionY(); sy++) {
                    var section = full.getSection(full.getSectionIndexFromSectionY(sy));
                    for (int y = 0; y < 16; y++) if (sy * 16 + y <= 32)
                        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) section.setBlockState(x, y, z, stone);
                }
                var raw = Arrays.stream(full.getSections()).map(LevelChunkSection::copy).toList();
                var noise = new UnstructuredNoiseSurfaceContext(generator, random, bounds, position).createSurfaceNoise(factory);
                full.getOrCreateNoiseChunk(ignored -> noise);
                var structures = new StructureManager(null, null, null) {
                    @Override public List<StructureStart> startsForStructure(ChunkPos pos, Predicate<Structure> matcher) { return List.of(); }
                };
                generator.buildSurface(full, new WorldGenerationContext(generator, bounds), random, structures, biomeManager, biomeRegistry, Blender.empty());
                for (int sy = bounds.getMinSectionY(); sy <= bounds.getMaxSectionY(); sy++) {
                    int index = bounds.getSectionIndexFromSectionY(sy);
                    var target = new ProtoChunk(position, UpgradeData.EMPTY, new LevelChunkSection[]{raw.get(index)},
                            new net.minecraft.world.ticks.ProtoChunkTicks<>(), new net.minecraft.world.ticks.ProtoChunkTicks<>(),
                            LevelHeightAccessor.create(sy * 16, 16), factory, null);
                    var metrics = SectionSurfaceBuilder.build(generator, random, biomeManager, biomeRegistry, bounds, target, noise, factory, metadata);
                    writes += metrics.extensionTargetWrites();
                    mismatches += differences(target.getSection(0), full.getSection(index));
                }
            }
            return Map.<String, Object>of("kind", "badlands-extension-fixture", "mismatches", mismatches, "targetWrites", writes);
        }, net.minecraft.util.Util.backgroundExecutor());
    }
}

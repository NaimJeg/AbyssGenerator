/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.WorldGenRegion
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.Util
 *  net.minecraft.util.random.WeightedList
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.NoiseColumn
 *  net.minecraft.world.level.StructureManager
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.BiomeGenerationSettings
 *  net.minecraft.world.level.biome.BiomeManager
 *  net.minecraft.world.level.biome.BiomeResolver
 *  net.minecraft.world.level.biome.BiomeSource
 *  net.minecraft.world.level.biome.FeatureSorter$StepFeatureData
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.ChunkGeneratorStructureState
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.StructureAccess
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.LegacyRandomSource
 *  net.minecraft.world.level.levelgen.RandomState
 *  net.minecraft.world.level.levelgen.RandomSupport
 *  net.minecraft.world.level.levelgen.WorldgenRandom
 *  net.minecraft.world.level.levelgen.XoroshiroRandomSource
 *  net.minecraft.world.level.levelgen.blending.Blender
 *  net.minecraft.world.level.levelgen.feature.FeatureCountTracker
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureCheckResult
 *  net.minecraft.world.level.levelgen.structure.StructureSet
 *  net.minecraft.world.level.levelgen.structure.StructureSet$StructureSelectionEntry
 *  net.minecraft.world.level.levelgen.structure.StructureSpawnOverride
 *  net.minecraft.world.level.levelgen.structure.StructureSpawnOverride$BoundingBoxType
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement
 *  net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
 *  net.minecraft.world.level.levelgen.structure.placement.StructurePlacement
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.FeatureCountTracker;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkGenerator.class})
public abstract class ChunkGeneratorMixin {
    @Shadow
    public static Codec<ChunkGenerator> CODEC;
    @Shadow
    protected BiomeSource biomeSource;
    @Shadow
    private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;
    @Shadow
    private Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    @Overwrite
    public void validate() {
        ((ChunkGenerator)this).featuresPerStep.get();
    }

    @Shadow
    public abstract MapCodec<? extends ChunkGenerator> codec();

    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> structureSets, RandomState randomState, long legacyLevelSeed) {
        return ChunkGeneratorStructureState.createForNormal((RandomState)randomState, (long)legacyLevelSeed, (BiomeSource)((ChunkGenerator)this).biomeSource, structureSets);
    }

    @Overwrite
    public Optional<Identifier> getTypeNameForDataFixer() {
        return BuiltInRegistries.CHUNK_GENERATOR.getResourceKey((Object)((ChunkGenerator)this).codec()).map(ResourceKey::identifier);
    }

    @Overwrite
    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess protoChunk) {
        return CompletableFuture.supplyAsync(() -> {
            protoChunk.fillBiomesFromNoise((BiomeResolver)((ChunkGenerator)this).biomeSource, randomState.sampler());
            return protoChunk;
        }, Util.backgroundExecutor().forName("init_biomes"));
    }

    @Shadow
    public abstract void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7);

    private static double chunkcutter$squareDistance(Vec3i a, Vec3i b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        int dz = Math.abs(a.getZ() - b.getZ());
        int max = Math.max(dx, Math.max(dy, dz));
        return (double)max * (double)max;
    }

    public @Nullable Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel level, HolderSet<Structure> wantedStructures, BlockPos pos, int maxSearchRadius, boolean createReference) {
        if (SharedConstants.DEBUG_DISABLE_FEATURES) {
            return null;
        }
        if (!level.getServer().getWorldGenSettings().options().generateStructures()) {
            return null;
        }
        ChunkGeneratorStructureState generatorState = level.getChunkSource().getGeneratorState();
        Object2ObjectArrayMap placementScans = new Object2ObjectArrayMap();
        for (Holder structure : wantedStructures) {
            for (StructurePlacement placement : generatorState.getPlacementsForStructure(structure)) {
                placementScans.computeIfAbsent(placement, p -> new ObjectArraySet()).add(structure);
            }
        }
        if (placementScans.isEmpty()) {
            return null;
        }
        Pair<BlockPos, Holder<Structure>> nearest = null;
        double distanceSqr = Double.MAX_VALUE;
        StructureManager structureManager = level.structureManager();
        ArrayList randomSpreadEntries = new ArrayList(placementScans.size());
        for (Map.Entry entry : placementScans.entrySet()) {
            StructurePlacement placement = (StructurePlacement)entry.getKey();
            if (placement instanceof ConcentricRingsStructurePlacement) {
                BlockPos structurePos;
                double newDistanceSqr;
                ConcentricRingsStructurePlacement rings = (ConcentricRingsStructurePlacement)placement;
                Pair<BlockPos, Holder<Structure>> generating = ((ChunkGenerator)this).getNearestGeneratedStructure((Set)entry.getValue(), level, structureManager, pos, createReference, rings);
                if (generating == null || !((newDistanceSqr = ChunkGeneratorMixin.chunkcutter$squareDistance((Vec3i)pos, (Vec3i)(structurePos = (BlockPos)generating.getFirst()))) < distanceSqr)) continue;
                distanceSqr = newDistanceSqr;
                nearest = generating;
                continue;
            }
            if (!(placement instanceof RandomSpreadStructurePlacement)) continue;
            randomSpreadEntries.add(entry);
        }
        if (!randomSpreadEntries.isEmpty()) {
            int chunkOriginX = SectionPos.blockToSectionCoord((int)pos.getX());
            int chunkOriginZ = SectionPos.blockToSectionCoord((int)pos.getZ());
            for (int radius = 0; radius <= maxSearchRadius; ++radius) {
                boolean foundSomething = false;
                for (Map.Entry entry : randomSpreadEntries) {
                    RandomSpreadStructurePlacement randomPlacement = (RandomSpreadStructurePlacement)entry.getKey();
                    Pair<BlockPos, Holder<Structure>> structurePos = ChunkGeneratorMixin.getNearestGeneratedStructure((Set)entry.getValue(), (LevelReader)level, structureManager, chunkOriginX, chunkOriginZ, radius, createReference, generatorState.getLevelSeed(), randomPlacement);
                    if (structurePos == null) continue;
                    foundSomething = true;
                    double newDistanceSqr = ChunkGeneratorMixin.chunkcutter$squareDistance((Vec3i)pos, (Vec3i)structurePos.getFirst());
                    if (!(newDistanceSqr < distanceSqr)) continue;
                    distanceSqr = newDistanceSqr;
                    nearest = structurePos;
                }
                if (!foundSomething) continue;
                return nearest;
            }
        }
        return nearest;
    }

    @Overwrite
    public @Nullable Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> structures, ServerLevel level, StructureManager structureManager, BlockPos pos, boolean createReference, ConcentricRingsStructurePlacement rings) {
        List positions = level.getChunkSource().getGeneratorState().getRingPositionsFor(rings);
        if (positions == null) {
            throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
        }
        Pair<BlockPos, Holder<Structure>> closestPos = null;
        double closest = Double.MAX_VALUE;
        BlockPos.MutableBlockPos structurePos = new BlockPos.MutableBlockPos();
        for (ChunkPos chunkPos : positions) {
            Pair<BlockPos, Holder<Structure>> generating;
            structurePos.set(SectionPos.sectionToBlockCoord((int)chunkPos.x(), (int)8), 32, SectionPos.sectionToBlockCoord((int)chunkPos.z(), (int)8));
            double distSqr = ChunkGeneratorMixin.chunkcutter$squareDistance((Vec3i)structurePos, (Vec3i)pos);
            boolean isClosest = closestPos == null || distSqr < closest;
            if (!isClosest || (generating = ChunkGeneratorMixin.getStructureGeneratingAt(structures, (LevelReader)level, structureManager, createReference, (StructurePlacement)rings, chunkPos)) == null) continue;
            closestPos = generating;
            closest = distSqr;
        }
        return closestPos;
    }

    @Overwrite
    public static @Nullable Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> structures, LevelReader level, StructureManager structureManager, int chunkOriginX, int chunkOriginZ, int radius, boolean createReference, long seed, RandomSpreadStructurePlacement config) {
        int spacing = config.spacing();
        for (int x = -radius; x <= radius; ++x) {
            boolean xEdge = x == -radius || x == radius;
            for (int z = -radius; z <= radius; ++z) {
                int sectorZ;
                int sectorX;
                ChunkPos chunkTarget;
                Pair<BlockPos, Holder<Structure>> generating;
                boolean zEdge;
                boolean bl = zEdge = z == -radius || z == radius;
                if (!xEdge && !zEdge || (generating = ChunkGeneratorMixin.getStructureGeneratingAt(structures, level, structureManager, createReference, (StructurePlacement)config, chunkTarget = config.getPotentialStructureChunk(seed, sectorX = chunkOriginX + spacing * x, sectorZ = chunkOriginZ + spacing * z))) == null) continue;
                return generating;
            }
        }
        return null;
    }

    @Overwrite
    public static @Nullable Pair<BlockPos, Holder<Structure>> getStructureGeneratingAt(Set<Holder<Structure>> structures, LevelReader level, StructureManager structureManager, boolean createReference, StructurePlacement config, ChunkPos chunkTarget) {
        for (Holder<Structure> structure : structures) {
            StructureCheckResult fastCheckResult = structureManager.checkStructurePresence(chunkTarget, (Structure)structure.value(), config, createReference);
            if (fastCheckResult == StructureCheckResult.START_NOT_PRESENT) continue;
            if (!createReference && fastCheckResult == StructureCheckResult.START_PRESENT) {
                return Pair.of((Object)config.getLocatePos(chunkTarget), structure);
            }
            ChunkAccess chunk = level.getChunk(chunkTarget.x(), chunkTarget.z(), ChunkStatus.STRUCTURE_STARTS);
            StructureStart start = structureManager.getStartForStructure(SectionPos.bottomOf((ChunkAccess)chunk), (Structure)structure.value(), (StructureAccess)chunk);
            if (start == null || !start.isValid() || createReference && !ChunkGeneratorMixin.tryAddReference(structureManager, start)) continue;
            return Pair.of((Object)config.getLocatePos(start.getChunkPos()), structure);
        }
        return null;
    }

    @Overwrite
    public static boolean tryAddReference(StructureManager manager, StructureStart start) {
        if (start.canBeReferenced()) {
            manager.addReference(start);
            return true;
        }
        return false;
    }

    @Overwrite
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        ChunkPos centerPos = chunk.getPos();
        if (!SharedConstants.debugVoidTerrain((ChunkPos)centerPos)) {
            SectionPos sectionPos = SectionPos.of((ChunkPos)centerPos, (int)level.getMinSectionY());
            BlockPos origin = sectionPos.origin();
            Registry structuresRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            Map<Integer, List<Structure>> structuresByStep = structuresRegistry.stream().collect(Collectors.groupingBy(structure -> structure.step().ordinal()));
            List featureList = (List)((ChunkGenerator)this).featuresPerStep.get();
            WorldgenRandom random = new WorldgenRandom((RandomSource)new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long decorationSeed = random.setDecorationSeed(level.getSeed(), origin.getX(), origin.getZ());
            ObjectArraySet possibleBiomes = new ObjectArraySet();
            ChunkPos.rangeClosed((ChunkPos)sectionPos.chunk(), (int)1).forEach(arg_0 -> ChunkGeneratorMixin.lambda$applyBiomeDecoration$1(level, (Set)possibleBiomes, arg_0));
            possibleBiomes.retainAll(((ChunkGenerator)this).biomeSource.possibleBiomes());
            int featureStepCount = featureList.size();
            try {
                Registry featureRegistry = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
                int generationSteps = Math.max(GenerationStep.Decoration.values().length, featureStepCount);
                for (int stepIndex = 0; stepIndex < generationSteps; ++stepIndex) {
                    int index = 0;
                    if (structureManager.shouldGenerateStructures()) {
                        for (Structure structure2 : structuresByStep.getOrDefault(stepIndex, Collections.emptyList())) {
                            random.setFeatureSeed(decorationSeed, index, stepIndex);
                            Supplier<String> currentlyGenerating = () -> structuresRegistry.getResourceKey((Object)structure2).map(Object::toString).orElseGet(structure2::toString);
                            try {
                                level.setCurrentlyGenerating(currentlyGenerating);
                                structureManager.startsForStructure(sectionPos, structure2).forEach(start -> start.placeInChunk(level, structureManager, (ChunkGenerator)this, (RandomSource)random, ChunkGeneratorMixin.getWritableArea(chunk), centerPos));
                            }
                            catch (Exception e) {
                                CrashReport report = CrashReport.forThrowable((Throwable)e, (String)"Feature placement");
                                report.addCategory("Feature").setDetail("Description", currentlyGenerating::get);
                                throw new ReportedException(report);
                            }
                            ++index;
                        }
                    }
                    if (stepIndex >= featureStepCount) continue;
                    IntArraySet possibleFeaturesThisStep = new IntArraySet();
                    for (Holder biome : possibleBiomes) {
                        List featuresInBiome = ((BiomeGenerationSettings)((ChunkGenerator)this).generationSettingsGetter.apply(biome)).features();
                        if (stepIndex >= featuresInBiome.size()) continue;
                        HolderSet featuresInBiomeThisStep = (HolderSet)featuresInBiome.get(stepIndex);
                        FeatureSorter.StepFeatureData stepFeatureData = (FeatureSorter.StepFeatureData)featureList.get(stepIndex);
                        featuresInBiomeThisStep.stream().map(Holder::value).forEach(arg_0 -> ChunkGeneratorMixin.lambda$applyBiomeDecoration$4((IntSet)possibleFeaturesThisStep, stepFeatureData, arg_0));
                    }
                    int numberOfFeaturesInStep = possibleFeaturesThisStep.size();
                    int[] indexArray = possibleFeaturesThisStep.toIntArray();
                    Arrays.sort(indexArray);
                    FeatureSorter.StepFeatureData stepFeatureData = (FeatureSorter.StepFeatureData)featureList.get(stepIndex);
                    for (int featureIndex = 0; featureIndex < numberOfFeaturesInStep; ++featureIndex) {
                        int globalIndexOfFeature = indexArray[featureIndex];
                        PlacedFeature feature = (PlacedFeature)stepFeatureData.features().get(globalIndexOfFeature);
                        Supplier<String> currentlyGenerating = () -> featureRegistry.getResourceKey((Object)feature).map(Object::toString).orElseGet(() -> ((PlacedFeature)feature).toString());
                        random.setFeatureSeed(decorationSeed, globalIndexOfFeature, stepIndex);
                        try {
                            level.setCurrentlyGenerating(currentlyGenerating);
                            feature.placeWithBiomeCheck(level, (ChunkGenerator)this, (RandomSource)random, origin);
                            continue;
                        }
                        catch (Exception e) {
                            CrashReport report = CrashReport.forThrowable((Throwable)e, (String)"Feature placement");
                            report.addCategory("Feature").setDetail("Description", currentlyGenerating::get);
                            throw new ReportedException(report);
                        }
                    }
                }
                level.setCurrentlyGenerating(null);
                if (SharedConstants.DEBUG_FEATURE_COUNT) {
                    FeatureCountTracker.chunkDecorated((ServerLevel)level.getLevel());
                }
            }
            catch (Exception e) {
                CrashReport report = CrashReport.forThrowable((Throwable)e, (String)"Biome decoration");
                report.addCategory("Generation").setDetail("CenterX", (Object)centerPos.x()).setDetail("CenterZ", (Object)centerPos.z()).setDetail("Decoration Seed", (Object)decorationSeed);
                throw new ReportedException(report);
            }
        }
    }

    @Overwrite
    public static BoundingBox getWritableArea(ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int targetBlockX = chunkPos.getMinBlockX();
        int targetBlockZ = chunkPos.getMinBlockZ();
        LevelHeightAccessor heightAccessor = chunk.getHeightAccessorForGeneration();
        int minY = heightAccessor.getMinY() + 1;
        int maxY = heightAccessor.getMaxY();
        return new BoundingBox(targetBlockX, minY, targetBlockZ, targetBlockX + 15, maxY, targetBlockZ + 15);
    }

    @Shadow
    public abstract void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4);

    public abstract void spawnOriginalMobs(WorldGenRegion var1);

    public int getSpawnHeight(LevelHeightAccessor heightAccessor) {
        return 64;
    }

    @Overwrite
    public BiomeSource getBiomeSource() {
        return ((ChunkGenerator)this).biomeSource;
    }

    @Shadow
    public abstract int getGenDepth();

    public WeightedList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome, StructureManager structureManager, MobCategory mobCategory, BlockPos pos) {
        Map structures = structureManager.getAllStructuresAt(pos);
        for (Map.Entry entry : structures.entrySet()) {
            Structure structure = (Structure)entry.getKey();
            StructureSpawnOverride override = (StructureSpawnOverride)structure.spawnOverrides().get(mobCategory);
            if (override == null) continue;
            MutableBoolean inOverrideBox = new MutableBoolean(false);
            Predicate<StructureStart> check = override.boundingBox() == StructureSpawnOverride.BoundingBoxType.PIECE ? start -> structureManager.structureHasPieceAt(pos, start) : start -> start.getBoundingBox().isInside((Vec3i)pos);
            structureManager.fillStartsForStructure(structure, (LongSet)entry.getValue(), start -> {
                if (inOverrideBox.isFalse() && check.test((StructureStart)start)) {
                    inOverrideBox.setTrue();
                }
            });
            if (!inOverrideBox.isTrue()) continue;
            return override.spawns();
        }
        return ((Biome)biome.value()).getMobSettings().getMobs(mobCategory);
    }

    @Overwrite
    public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState state, StructureManager structureManager, ChunkAccess centerChunk, StructureTemplateManager structureTemplateManager, ResourceKey<Level> level) {
        if (!SharedConstants.DEBUG_DISABLE_STRUCTURES) {
            ChunkPos sourceChunkPos = centerChunk.getPos();
            SectionPos sectionPos = SectionPos.bottomOf((ChunkAccess)centerChunk);
            RandomState randomState = state.randomState();
            state.possibleStructureSets().forEach(set -> {
                StructurePlacement featurePlacement = ((StructureSet)set.value()).placement();
                List structures = ((StructureSet)set.value()).structures();
                for (StructureSet.StructureSelectionEntry structure : structures) {
                    StructureStart existingStart = structureManager.getStartForStructure(sectionPos, (Structure)structure.structure().value(), (StructureAccess)centerChunk);
                    if (existingStart == null || !existingStart.isValid()) continue;
                    return;
                }
                if (featurePlacement.isStructureChunk(state, sourceChunkPos.x(), sourceChunkPos.z())) {
                    if (structures.size() == 1) {
                        ((ChunkGenerator)this).tryGenerateStructure((StructureSet.StructureSelectionEntry)structures.get(0), structureManager, registryAccess, randomState, structureTemplateManager, state.getLevelSeed(), centerChunk, sourceChunkPos, sectionPos, level);
                    } else {
                        ArrayList options = new ArrayList(structures.size());
                        options.addAll(structures);
                        WorldgenRandom random = new WorldgenRandom((RandomSource)new LegacyRandomSource(0L));
                        random.setLargeFeatureSeed(state.getLevelSeed(), sourceChunkPos.x(), sourceChunkPos.z());
                        int total = 0;
                        for (StructureSet.StructureSelectionEntry option : options) {
                            total += option.weight();
                        }
                        while (!options.isEmpty()) {
                            StructureSet.StructureSelectionEntry option;
                            int choice = random.nextInt(total);
                            int index = 0;
                            Iterator i$ = options.iterator();
                            while (i$.hasNext() && (choice -= (option = (StructureSet.StructureSelectionEntry)i$.next()).weight()) >= 0) {
                                ++index;
                            }
                            StructureSet.StructureSelectionEntry selected = (StructureSet.StructureSelectionEntry)options.get(index);
                            if (((ChunkGenerator)this).tryGenerateStructure(selected, structureManager, registryAccess, randomState, structureTemplateManager, state.getLevelSeed(), centerChunk, sourceChunkPos, sectionPos, level)) {
                                return;
                            }
                            options.remove(index);
                            total -= selected.weight();
                        }
                    }
                }
            });
        }
    }

    @Overwrite
    public boolean tryGenerateStructure(StructureSet.StructureSelectionEntry selected, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess centerChunk, ChunkPos sourceChunkPos, SectionPos sectionPos, ResourceKey<Level> level) {
        Structure structure = (Structure)selected.structure().value();
        int references = ChunkGeneratorMixin.fetchReferences(structureManager, centerChunk, sectionPos, structure);
        HolderSet biomeAllowedForStructure = structure.biomes();
        Predicate<Holder> biomePredicate = arg_0 -> ((HolderSet)biomeAllowedForStructure).contains(arg_0);
        StructureStart start = structure.generate(selected.structure(), level, registryAccess, (ChunkGenerator)this, ((ChunkGenerator)this).biomeSource, randomState, structureTemplateManager, seed, sourceChunkPos, references, (LevelHeightAccessor)centerChunk, biomePredicate);
        if (start.isValid()) {
            structureManager.setStartForStructure(sectionPos, structure, start, (StructureAccess)centerChunk);
            return true;
        }
        return false;
    }

    @Overwrite
    public static int fetchReferences(StructureManager structureManager, ChunkAccess centerChunk, SectionPos sectionPos, Structure structure) {
        StructureStart prevEntry = structureManager.getStartForStructure(sectionPos, structure, (StructureAccess)centerChunk);
        return prevEntry != null ? prevEntry.getReferences() : 0;
    }

    @Overwrite
    public void createReferences(WorldGenLevel level, StructureManager structureManager, ChunkAccess centerChunk) {
        int range = 8;
        ChunkPos chunkPos = centerChunk.getPos();
        int targetX = chunkPos.x();
        int targetZ = chunkPos.z();
        int targetBlockX = chunkPos.getMinBlockX();
        int targetBlockZ = chunkPos.getMinBlockZ();
        SectionPos pos = SectionPos.bottomOf((ChunkAccess)centerChunk);
        for (int sourceX = targetX - 8; sourceX <= targetX + 8; ++sourceX) {
            for (int sourceZ = targetZ - 8; sourceZ <= targetZ + 8; ++sourceZ) {
                long sourceChunkKey = ChunkPos.pack((int)sourceX, (int)sourceZ);
                for (StructureStart start : level.getChunk(sourceX, sourceZ).getAllStarts().values()) {
                    try {
                        if (!start.isValid() || !start.getBoundingBox().intersects(targetBlockX, targetBlockZ, targetBlockX + 15, targetBlockZ + 15)) continue;
                        structureManager.addReferenceForStructure(pos, start.getStructure(), sourceChunkKey, (StructureAccess)centerChunk);
                    }
                    catch (Exception e) {
                        CrashReport report = CrashReport.forThrowable((Throwable)e, (String)"Generating structure reference");
                        CrashReportCategory structure = report.addCategory("Structure");
                        Optional configuredStructuresRegistry = level.registryAccess().lookup(Registries.STRUCTURE);
                        structure.setDetail("Id", () -> configuredStructuresRegistry.map(r -> r.getKey((Object)start.getStructure()).toString()).orElse("UNKNOWN"));
                        structure.setDetail("Name", () -> BuiltInRegistries.STRUCTURE_TYPE.getKey((Object)start.getStructure().type()).toString());
                        structure.setDetail("Class", () -> start.getStructure().getClass().getCanonicalName());
                        throw new ReportedException(report);
                    }
                }
            }
        }
    }

    @Shadow
    public abstract CompletableFuture<ChunkAccess> fillFromNoise(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4);

    public abstract int getSeaLevel();

    public abstract int getMinY();

    public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5);

    public abstract NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4);

    public int getFirstFreeHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return ((ChunkGenerator)this).getBaseHeight(x, z, type, heightAccessor, randomState);
    }

    @Overwrite
    public int getFirstOccupiedHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return ((ChunkGenerator)this).getBaseHeight(x, z, type, heightAccessor, randomState) - 1;
    }

    @Shadow
    public abstract void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3);

    @Deprecated
    public BiomeGenerationSettings getBiomeGenerationSettings(Holder<Biome> biome) {
        return (BiomeGenerationSettings)((ChunkGenerator)this).generationSettingsGetter.apply(biome);
    }

    private static /* synthetic */ void lambda$applyBiomeDecoration$4(IntSet possibleFeaturesThisStep, FeatureSorter.StepFeatureData stepFeatureData, PlacedFeature featurex) {
        possibleFeaturesThisStep.add(stepFeatureData.indexMapping().applyAsInt(featurex));
    }

    private static /* synthetic */ void lambda$applyBiomeDecoration$1(WorldGenLevel level, Set possibleBiomes, ChunkPos chunkPos) {
        ChunkAccess chunkInRange = level.getChunk(chunkPos.x(), chunkPos.z());
        for (LevelChunkSection section : chunkInRange.getSections()) {
            if (section == null) continue;
            section.getBiomes().getAll(possibleBiomes::add);
        }
    }
}


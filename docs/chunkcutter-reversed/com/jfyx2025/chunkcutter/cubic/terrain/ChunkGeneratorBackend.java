/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.QuartPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.WorldGenRegion
 *  net.minecraft.util.StaticCache2D
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.ProtoChunk
 *  net.minecraft.world.level.chunk.status.ChunkPyramid
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.chunk.status.ChunkStep
 *  net.minecraft.world.level.levelgen.BelowZeroRetrogen
 *  net.minecraft.world.level.levelgen.Heightmap
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.RandomState
 *  net.minecraft.world.level.levelgen.blending.Blender
 *  net.minecraft.world.level.material.FluidState
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.SectionLoadMetrics;
import com.jfyx2025.chunkcutter.cubic.terrain.BlockWriter;
import com.jfyx2025.chunkcutter.cubic.terrain.NoiseBounds;
import com.jfyx2025.chunkcutter.cubic.terrain.NoiseTerrainChunk;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainAlgorithm3D;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainCacheControl;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.material.FluidState;

public final class ChunkGeneratorBackend
implements TerrainAlgorithm3D,
TerrainCacheControl {
    private final ServerLevel level;
    private final ChunkGenerator generator;
    private final RandomState randomState;
    private final NoiseBounds bounds;
    private final Holder<Biome> defaultBiome;
    private final int featureWriteRadius;
    private final Set<Long> prewarmedRegions = ConcurrentHashMap.newKeySet();
    private final ExecutorService referenceExecutor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2), task -> {
        Thread thread = new Thread(task, "ChunkCutter-Reference");
        thread.setDaemon(true);
        return thread;
    });
    private final Map<Long, ProtoChunk> chunks = new ConcurrentHashMap<Long, ProtoChunk>();
    private final Map<Long, ProtoChunk> referenceChunks = new ConcurrentHashMap<Long, ProtoChunk>();
    private final Map<Long, Map<Integer, List<CompoundTag>>> blockEntityTags = new ConcurrentHashMap<Long, Map<Integer, List<CompoundTag>>>();
    private final Map<Long, Map<Integer, List<BlockPos>>> blockEntityPositions = new ConcurrentHashMap<Long, Map<Integer, List<BlockPos>>>();
    private final Set<Long> featuresApplied = ConcurrentHashMap.newKeySet();
    private final Set<Long> carversReady = ConcurrentHashMap.newKeySet();
    private final Map<Long, CountDownLatch> carverLatches = new ConcurrentHashMap<Long, CountDownLatch>();
    private final Set<Long> building = ConcurrentHashMap.newKeySet();
    private final Set<Long> buildingReferences = ConcurrentHashMap.newKeySet();
    private volatile boolean stopping;
    private static final Object FEATURE_APPLICATION_LOCK = new Object();

    public ChunkGeneratorBackend(ServerLevel level, ChunkGenerator generator) {
        ServerChunkCache serverChunkCache = level.getChunkSource();
        if (!(serverChunkCache instanceof ServerChunkCache)) {
            throw new IllegalStateException("vanilla terrain source requires ServerChunkCache");
        }
        ServerChunkCache chunkCache = serverChunkCache;
        this.level = level;
        this.generator = generator;
        this.randomState = chunkCache.randomState();
        this.bounds = new NoiseBounds(generator.getMinY(), generator.getGenDepth());
        this.defaultBiome = level.palettedContainerFactory().defaultBiome();
        this.featureWriteRadius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FEATURES).blockStateWriteRadius();
    }

    @Override
    public int heightAt(int x, int z) {
        return this.generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, (LevelHeightAccessor)this.bounds, this.randomState);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BlockState stateAt(int x, int y, int z) {
        ProtoChunk chunk;
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING || y < this.bounds.getMinY() || y >= this.bounds.getMinY() + this.bounds.getHeight()) {
            return Blocks.AIR.defaultBlockState();
        }
        int cx = x >> 4;
        int cz = z >> 4;
        this.applyFeatureNeighborhood(cx, cz);
        ProtoChunk protoChunk = chunk = this.chunk(cx, cz);
        synchronized (protoChunk) {
            int index = chunk.getSectionIndex(y);
            if (index < 0 || index >= chunk.getSections().length) {
                return Blocks.AIR.defaultBlockState();
            }
            return chunk.getSections()[index].getBlockState(x & 0xF, y & 0xF, z & 0xF);
        }
    }

    @Override
    public Holder<Biome> biomeAt(int x, int y, int z) {
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING || y < this.bounds.getMinY() || y >= this.bounds.getMinY() + this.bounds.getHeight()) {
            return this.defaultBiome;
        }
        return this.generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock((int)x), QuartPos.fromBlock((int)y), QuartPos.fromBlock((int)z), this.randomState.sampler());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CompoundTag> blockEntitiesAt(int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ) {
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return List.of();
        }
        ArrayList<CompoundTag> tags = new ArrayList<CompoundTag>();
        int maxX = minX + sizeX - 1;
        int maxY = minY + sizeY - 1;
        int maxZ = minZ + sizeZ - 1;
        int minChunkX = minX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkX = maxX >> 4;
        int maxChunkZ = maxZ >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                ProtoChunk chunk = this.chunk(cx, cz);
                long chunkKey = ChunkPos.pack((int)cx, (int)cz);
                Map sectionCache = this.blockEntityTags.computeIfAbsent(chunkKey, ignored -> new ConcurrentHashMap());
                Map positionCache = this.blockEntityPositions.computeIfAbsent(chunkKey, ignored -> new ConcurrentHashMap());
                ProtoChunk protoChunk = chunk;
                synchronized (protoChunk) {
                    for (int sy = minY >> 4; sy <= maxY >> 4; ++sy) {
                        List sectionTags = (List)sectionCache.get(sy);
                        if (sectionTags == null) {
                            List<BlockPos> positions = (List<BlockPos>)positionCache.get(sy);
                            if (positions == null) {
                                ArrayList<BlockPos> collectedPositions = new ArrayList<BlockPos>();
                                for (BlockPos pos : chunk.getBlockEntitiesPos()) {
                                    if (pos.getY() >> 4 != sy) continue;
                                    collectedPositions.add(pos);
                                }
                                positions = List.copyOf(collectedPositions);
                                positionCache.put(sy, positions);
                            }
                            ArrayList<CompoundTag> collected = new ArrayList<CompoundTag>();
                            for (BlockPos pos : positions) {
                                CompoundTag tag;
                                if (pos.getX() < minX || pos.getX() > maxX || pos.getY() < minY || pos.getY() > maxY || pos.getZ() < minZ || pos.getZ() > maxZ || (tag = chunk.getBlockEntityNbtForSaving(pos, (HolderLookup.Provider)this.level.registryAccess())) == null) continue;
                                collected.add(tag);
                            }
                            sectionTags = List.copyOf(collected);
                            sectionCache.put(sy, sectionTags);
                        }
                        tags.addAll(sectionTags);
                    }
                    continue;
                }
            }
        }
        return tags;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateRegion(int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ, BlockWriter out) {
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return;
        }
        int minChunkX = minX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkX = minX + sizeX - 1 >> 4;
        int maxChunkZ = minZ + sizeZ - 1 >> 4;
        int minGenY = this.bounds.getMinY();
        int maxGenY = minGenY + this.bounds.getHeight();
        int fromY = Math.max(minY, minGenY);
        int toY = Math.min(minY + sizeY, maxGenY);
        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                ProtoChunk chunk;
                this.applyFeatureNeighborhood(cx, cz);
                ProtoChunk protoChunk = chunk = this.chunk(cx, cz);
                synchronized (protoChunk) {
                    int chunkMinX = cx << 4;
                    int chunkMinZ = cz << 4;
                    int fromX = Math.max(minX, chunkMinX);
                    int fromZ = Math.max(minZ, chunkMinZ);
                    int toX = Math.min(minX + sizeX, chunkMinX + 16);
                    int toZ = Math.min(minZ + sizeZ, chunkMinZ + 16);
                    for (int x = fromX; x < toX; ++x) {
                        for (int z = fromZ; z < toZ; ++z) {
                            for (int y = fromY; y < toY; ++y) {
                                BlockState state = chunk.getSections()[chunk.getSectionIndex(y)].getBlockState(x & 0xF, y & 0xF, z & 0xF);
                                if (state.isAir()) continue;
                                out.set(x, y, z, state);
                            }
                        }
                    }
                    continue;
                }
            }
        }
    }

    private void applyFeatureNeighborhood(int cx, int cz) {
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return;
        }
        int radius = this.featureWriteRadius;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                this.chunk(cx + dx, cz + dz);
            }
        }
    }

    private void prewarmReferenceStarts(int cx, int cz) {
        if (this.stopping) {
            return;
        }
        long regionKey = (long)(cx >> 4) << 32 ^ (long)(cz >> 4) & 0xFFFFFFFFL;
        if (!this.prewarmedRegions.add(regionKey)) {
            return;
        }
        int radius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.STRUCTURE_REFERENCES).directDependencies().getRadius();
        ArrayList<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                int rx = cx + dx;
                int rz = cz + dz;
                futures.add(CompletableFuture.runAsync(() -> this.referenceChunk(rx, rz, false), this.referenceExecutor));
            }
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        while (!all.isDone()) {
            if (this.stopping) {
                all.cancel(true);
                return;
            }
            try {
                all.get(500L, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException dz) {
            }
            catch (Exception e) {
                return;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ProtoChunk chunk(int cx, int cz) {
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return this.emptyChunk(cx, cz);
        }
        long key = ChunkPos.pack((int)cx, (int)cz);
        ProtoChunk fast = this.chunks.get(key);
        if (fast != null && this.featuresApplied.contains(key)) {
            return fast;
        }
        this.prewarmReferenceStarts(cx, cz);
        fast = this.chunks.get(key);
        if (fast != null && (this.featuresApplied.contains(key) || this.building.contains(key))) {
            return fast;
        }
        ProtoChunk chunk = this.chunks.computeIfAbsent(key, ignored -> this.newChunk(cx, cz));
        if (this.building.contains(key)) {
            return chunk;
        }
        Object object = chunk;
        synchronized (object) {
            if (this.building.contains(key)) {
                return chunk;
            }
            this.advance(chunk, cx, cz, ChunkStatus.CARVERS, this.building, true);
            if (chunk.getPersistedStatus().isOrAfter(ChunkStatus.CARVERS)) {
                this.markCarversReady(key);
            }
        }
        if (!this.featuresApplied.contains(key) && this.awaitFeatureNeighbors(cx, cz)) {
            object = FEATURE_APPLICATION_LOCK;
            synchronized (object) {
                if (!this.featuresApplied.contains(key)) {
                    this.applyFeatures(chunk, cx, cz);
                    this.featuresApplied.add(key);
                }
            }
        }
        return chunk;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ProtoChunk baseChunk(int cx, int cz, ChunkStatus target) {
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return this.emptyChunk(cx, cz);
        }
        long key = ChunkPos.pack((int)cx, (int)cz);
        ProtoChunk fast = this.chunks.get(key);
        if (fast != null && (fast.getPersistedStatus().isOrAfter(target) || this.building.contains(key))) {
            return fast;
        }
        ProtoChunk chunk = this.chunks.computeIfAbsent(key, ignored -> this.newChunk(cx, cz));
        if (this.building.contains(key)) {
            return chunk;
        }
        ProtoChunk protoChunk = chunk;
        synchronized (protoChunk) {
            if (this.building.contains(key)) {
                return chunk;
            }
            this.advance(chunk, cx, cz, target, this.building, false);
            if (chunk.getPersistedStatus().isOrAfter(ChunkStatus.CARVERS)) {
                this.markCarversReady(key);
            }
        }
        return chunk;
    }

    private void markCarversReady(long key) {
        CountDownLatch latch;
        if (this.carversReady.add(key) && (latch = this.carverLatches.remove(key)) != null) {
            latch.countDown();
        }
    }

    private boolean awaitCarvers(int cx, int cz) {
        long key = ChunkPos.pack((int)cx, (int)cz);
        if (this.carversReady.contains(key)) {
            return true;
        }
        this.baseChunk(cx, cz, ChunkStatus.CARVERS);
        if (this.carversReady.contains(key)) {
            return true;
        }
        CountDownLatch latch = this.carverLatches.computeIfAbsent(key, ignored -> new CountDownLatch(1));
        if (this.carversReady.contains(key)) {
            latch.countDown();
            return true;
        }
        long deadline = System.nanoTime() + 60000000000L;
        while (!this.carversReady.contains(key)) {
            if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
                return false;
            }
            if (System.nanoTime() > deadline) {
                return false;
            }
            try {
                latch.await(200L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    private boolean awaitFeatureNeighbors(int cx, int cz) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0 || this.awaitCarvers(cx + dx, cz + dz)) continue;
                return false;
            }
        }
        return true;
    }

    private ProtoChunk emptyChunk(int cx, int cz) {
        NoiseTerrainChunk chunk = new NoiseTerrainChunk(new ChunkPos(cx, cz), this.bounds, this.level.palettedContainerFactory());
        chunk.setPersistedStatus(ChunkStatus.BIOMES);
        return chunk;
    }

    private ProtoChunk scratchChunk(int cx, int cz, ProtoChunk center) {
        NoiseTerrainChunk chunk = new NoiseTerrainChunk(new ChunkPos(cx, cz), this.bounds, this.level.palettedContainerFactory());
        chunk.fillBiomesFromNoise((qx, qy, qz, sampler) -> center.getNoiseBiome(qx, qy, qz), this.randomState.sampler());
        chunk.setPersistedStatus(ChunkStatus.BIOMES);
        return chunk;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ProtoChunk referenceChunk(int cx, int cz, boolean withReferences) {
        long key = ChunkPos.pack((int)cx, (int)cz);
        ProtoChunk chunk = this.referenceChunks.computeIfAbsent(key, ignored -> this.newChunk(cx, cz));
        if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return chunk;
        }
        if (this.buildingReferences.contains(key)) {
            return chunk;
        }
        ProtoChunk protoChunk = chunk;
        synchronized (protoChunk) {
            if (this.buildingReferences.contains(key)) {
                return chunk;
            }
            if (this.buildingReferences.add(key)) {
                try {
                    this.advance(chunk, cx, cz, withReferences ? ChunkStatus.STRUCTURE_REFERENCES : ChunkStatus.STRUCTURE_STARTS, this.buildingReferences, false);
                    if (!this.stopping && chunk.getPersistedStatus().isBefore(ChunkStatus.BIOMES)) {
                        chunk.fillBiomesFromNoise((qx, qy, qz, sampler) -> this.generator.getBiomeSource().getNoiseBiome(qx, qy, qz, sampler), this.randomState.sampler());
                        chunk.setPersistedStatus(ChunkStatus.BIOMES);
                    }
                }
                finally {
                    this.buildingReferences.remove(key);
                }
            }
        }
        return chunk;
    }

    /*
     * Loose catch block
     */
    private <T> T await(CompletableFuture<T> future) {
        while (true) {
            if (this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
                return null;
            }
            try {
                return future.get(500L, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e) {
                if (!this.stopping) continue;
                return null;
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            break;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void beginShutdown() {
        this.stopping = true;
        this.referenceExecutor.shutdownNow();
    }

    @Override
    public boolean isStopping() {
        return this.stopping;
    }

    @Override
    public void clearColumn(int cx, int cz) {
        long key = ChunkPos.pack((int)cx, (int)cz);
        this.chunks.remove(key);
        this.referenceChunks.remove(key);
        this.featuresApplied.remove(key);
        this.carversReady.remove(key);
        this.carverLatches.remove(key);
        this.blockEntityTags.remove(key);
        this.blockEntityPositions.remove(key);
    }

    private ProtoChunk newChunk(int cx, int cz) {
        return new NoiseTerrainChunk(new ChunkPos(cx, cz), this.bounds, this.level.palettedContainerFactory());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void advance(ProtoChunk chunk, int cx, int cz, ChunkStatus target, Set<Long> buildingSet, boolean expandNeighbors) {
        long key = ChunkPos.pack((int)cx, (int)cz);
        ChunkStatus current = chunk.getPersistedStatus();
        if (current == null) {
            current = ChunkStatus.EMPTY;
            chunk.setPersistedStatus(current);
        }
        if (current.isOrAfter(target) || !buildingSet.add(key)) {
            return;
        }
        try {
            long stageStarted;
            if (current.isBefore(ChunkStatus.STRUCTURE_STARTS)) {
                stageStarted = System.nanoTime();
                if (this.level.getServer().getWorldGenSettings().options().generateStructures()) {
                    this.generator.createStructures(this.level.registryAccess(), this.level.getChunkSource().getGeneratorState(), this.level.structureManager(), (ChunkAccess)chunk, this.level.getStructureManager(), this.level.dimension());
                }
                chunk.setPersistedStatus(ChunkStatus.STRUCTURE_STARTS);
                if (buildingSet == this.building) {
                    this.level.onStructureStartsAvailable((ChunkAccess)chunk);
                }
                current = ChunkStatus.STRUCTURE_STARTS;
                SectionLoadMetrics.recordStructure(System.nanoTime() - stageStarted);
            }
            if (target.isOrBefore(ChunkStatus.STRUCTURE_STARTS) || this.stopping) {
                return;
            }
            if (current.isBefore(ChunkStatus.STRUCTURE_REFERENCES)) {
                stageStarted = System.nanoTime();
                WorldGenRegion referenceRegion = this.region(chunk, cx, cz, ChunkStatus.STRUCTURE_REFERENCES, true, true);
                this.generator.createReferences((WorldGenLevel)referenceRegion, this.level.structureManager().forWorldGenRegion(referenceRegion), (ChunkAccess)chunk);
                chunk.setPersistedStatus(ChunkStatus.STRUCTURE_REFERENCES);
                current = ChunkStatus.STRUCTURE_REFERENCES;
                SectionLoadMetrics.recordReference(System.nanoTime() - stageStarted);
            }
            if (target.isOrBefore(ChunkStatus.STRUCTURE_REFERENCES) || this.stopping) {
                return;
            }
            if (current.isBefore(ChunkStatus.BIOMES)) {
                stageStarted = System.nanoTime();
                WorldGenRegion biomeRegion = this.region(chunk, cx, cz, ChunkStatus.BIOMES, true, true);
                if (this.await(this.generator.createBiomes(this.randomState, Blender.of((WorldGenRegion)biomeRegion), this.level.structureManager().forWorldGenRegion(biomeRegion), (ChunkAccess)chunk)) == null) {
                    return;
                }
                chunk.setPersistedStatus(ChunkStatus.BIOMES);
                current = ChunkStatus.BIOMES;
                SectionLoadMetrics.recordBiomeStage(System.nanoTime() - stageStarted);
            }
            if (target.isOrBefore(ChunkStatus.BIOMES) || this.stopping) {
                return;
            }
            if (current.isBefore(ChunkStatus.NOISE)) {
                stageStarted = System.nanoTime();
                WorldGenRegion noiseRegion = this.region(chunk, cx, cz, ChunkStatus.NOISE, true, true);
                if (this.await(this.generator.fillFromNoise(Blender.of((WorldGenRegion)noiseRegion), this.randomState, this.level.structureManager().forWorldGenRegion(noiseRegion), (ChunkAccess)chunk)) == null) {
                    return;
                }
                if (chunk.getBelowZeroRetrogen() != null) {
                    BelowZeroRetrogen retrogen = chunk.getBelowZeroRetrogen();
                    BelowZeroRetrogen.replaceOldBedrock((ProtoChunk)chunk);
                    if (retrogen.hasBedrockHoles()) {
                        retrogen.applyBedrockMask(chunk);
                    }
                }
                chunk.setPersistedStatus(ChunkStatus.NOISE);
                current = ChunkStatus.NOISE;
                SectionLoadMetrics.recordNoise(System.nanoTime() - stageStarted);
            }
            if (target.isOrBefore(ChunkStatus.NOISE) || this.stopping) {
                return;
            }
            if (current.isBefore(ChunkStatus.SURFACE)) {
                stageStarted = System.nanoTime();
                Heightmap.primeHeightmaps((ChunkAccess)chunk, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
                WorldGenRegion surfaceRegion = this.region(chunk, cx, cz, ChunkStatus.SURFACE, expandNeighbors, false);
                this.generator.buildSurface(surfaceRegion, this.level.structureManager().forWorldGenRegion(surfaceRegion), this.randomState, (ChunkAccess)chunk);
                chunk.setPersistedStatus(ChunkStatus.SURFACE);
                current = ChunkStatus.SURFACE;
                SectionLoadMetrics.recordSurface(System.nanoTime() - stageStarted);
            }
            if (target.isOrBefore(ChunkStatus.SURFACE) || this.stopping) {
                return;
            }
            if (current.isBefore(ChunkStatus.CARVERS)) {
                stageStarted = System.nanoTime();
                WorldGenRegion carverRegion = this.region(chunk, cx, cz, ChunkStatus.CARVERS, expandNeighbors, false);
                Blender.addAroundOldChunksCarvingMaskFilter((WorldGenLevel)carverRegion, (ProtoChunk)chunk);
                this.generator.applyCarvers(carverRegion, this.level.getSeed(), this.randomState, this.level.getBiomeManager(), this.level.structureManager().forWorldGenRegion(carverRegion), (ChunkAccess)chunk);
                Heightmap.primeHeightmaps((ChunkAccess)chunk, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
                chunk.setPersistedStatus(ChunkStatus.CARVERS);
                SectionLoadMetrics.recordCarver(System.nanoTime() - stageStarted);
            }
        }
        finally {
            buildingSet.remove(key);
        }
    }

    private void applyFeatures(ProtoChunk proto, int cx, int cz) {
        if (SharedConstants.DEBUG_DISABLE_FEATURES || this.stopping || !this.level.getServer().isRunning() && !CubeProvider.PREPARING) {
            return;
        }
        long featureStarted = System.nanoTime();
        Heightmap.primeHeightmaps((ChunkAccess)proto, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
        WorldGenRegion region = this.region(proto, cx, cz, ChunkStatus.FEATURES, true);
        this.generator.applyBiomeDecoration((WorldGenLevel)region, (ChunkAccess)proto, this.level.structureManager().forWorldGenRegion(region));
        Blender.generateBorderTicks((WorldGenRegion)region, (ChunkAccess)proto);
        proto.setPersistedStatus(ChunkStatus.FEATURES);
        SectionLoadMetrics.recordFeature(System.nanoTime() - featureStarted);
    }

    private WorldGenRegion region(ProtoChunk proto, int cx, int cz, ChunkStatus status, boolean allowNeighbors) {
        return this.region(proto, cx, cz, status, allowNeighbors, status == ChunkStatus.STRUCTURE_REFERENCES);
    }

    private WorldGenRegion region(ProtoChunk proto, int cx, int cz, ChunkStatus status, boolean allowNeighbors, boolean referenceMode) {
        StaticCache2D cache = StaticCache2D.create((int)cx, (int)cz, (int)0, (x, z) -> new ScratchHolder(new ChunkPos(x, z), (ChunkAccess)proto));
        ChunkStep step = ChunkPyramid.GENERATION_PYRAMID.getStepTo(status);
        ChunkStatus neighborTarget = referenceMode ? ChunkStatus.STRUCTURE_REFERENCES : (status == ChunkStatus.FEATURES ? ChunkStatus.CARVERS : (status == ChunkStatus.SURFACE ? ChunkStatus.SURFACE : ChunkStatus.NOISE));
        return new AbsoluteWorldGenRegion(this, (StaticCache2D<GenerationChunkHolder>)cache, step, proto, allowNeighbors, referenceMode, neighborTarget, false);
    }

    private final class AbsoluteWorldGenRegion
    extends WorldGenRegion {
        private final ProtoChunk center;
        private final int centerX;
        private final int centerZ;
        private final ChunkStep step;
        private final int neighborRadius;
        private final boolean allowNeighbors;
        private final boolean referenceMode;
        private final ChunkStatus neighborTarget;
        private final boolean referenceWithReferences;
        final /* synthetic */ ChunkGeneratorBackend this$0;

        private AbsoluteWorldGenRegion(ChunkGeneratorBackend chunkGeneratorBackend, StaticCache2D<GenerationChunkHolder> cache, ChunkStep step, ProtoChunk proto, boolean allowNeighbors, boolean referenceMode, ChunkStatus neighborTarget, boolean referenceWithReferences) {
            ChunkGeneratorBackend chunkGeneratorBackend2 = chunkGeneratorBackend;
            Objects.requireNonNull(chunkGeneratorBackend2);
            this.this$0 = chunkGeneratorBackend2;
            super(chunkGeneratorBackend.level, cache, step, (ChunkAccess)proto);
            this.center = proto;
            this.centerX = proto.getPos().x();
            this.centerZ = proto.getPos().z();
            this.step = step;
            this.neighborRadius = step.targetStatus() == ChunkStatus.CARVERS ? Math.max(1, step.blockStateWriteRadius()) : Math.max(step.directDependencies().getRadius(), step.blockStateWriteRadius());
            this.allowNeighbors = allowNeighbors;
            this.referenceMode = referenceMode;
            this.neighborTarget = neighborTarget;
            this.referenceWithReferences = referenceWithReferences;
        }

        private boolean outsideNeighborRadius(int cx, int cz) {
            return Math.max(Math.abs(cx - this.centerX), Math.abs(cz - this.centerZ)) > this.neighborRadius;
        }

        private ProtoChunk ensure(int x, int z) {
            int cx = x >> 4;
            int cz = z >> 4;
            if (cx == this.centerX && cz == this.centerZ) {
                return this.center;
            }
            if (!this.allowNeighbors || this.outsideNeighborRadius(cx, cz)) {
                return this.this$0.scratchChunk(cx, cz, this.center);
            }
            return this.referenceMode ? this.this$0.referenceChunk(cx, cz, this.referenceWithReferences) : this.this$0.baseChunk(cx, cz, this.neighborTarget);
        }

        public ChunkAccess getChunk(int x, int z) {
            if (x == this.centerX && z == this.centerZ) {
                return this.center;
            }
            if (!this.allowNeighbors || this.outsideNeighborRadius(x, z)) {
                return this.this$0.scratchChunk(x, z, this.center);
            }
            return this.referenceMode ? this.this$0.referenceChunk(x, z, this.referenceWithReferences) : this.this$0.baseChunk(x, z, this.neighborTarget);
        }

        public ChunkAccess getChunk(int x, int z, ChunkStatus status, boolean load) {
            return this.getChunk(x, z);
        }

        public boolean hasChunk(int x, int z) {
            return true;
        }

        public BlockState getBlockState(BlockPos pos) {
            return this.ensure(pos.getX(), pos.getZ()).getBlockState(pos);
        }

        public FluidState getFluidState(BlockPos pos) {
            return this.ensure(pos.getX(), pos.getZ()).getFluidState(pos);
        }

        public BlockEntity getBlockEntity(BlockPos pos) {
            EntityBlock entityBlock;
            ProtoChunk chunk = this.ensure(pos.getX(), pos.getZ());
            BlockEntity blockEntity = chunk.getBlockEntity(pos);
            if (blockEntity != null) {
                return blockEntity;
            }
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof EntityBlock && (blockEntity = (entityBlock = (EntityBlock)block).newBlockEntity(pos.immutable(), state)) != null) {
                chunk.setBlockEntity(blockEntity);
            }
            return blockEntity;
        }

        public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
            Map<Integer, List<BlockPos>> positionsBySection;
            Block block;
            if (!this.ensureCanWrite(pos)) {
                return false;
            }
            ProtoChunk chunk = this.ensure(pos.getX(), pos.getZ());
            BlockState previous = chunk.setBlockState(pos, state, flags);
            if (previous != null && !previous.equals((Object)state)) {
                this.this$0.level.updatePOIOnBlockStateChange(pos, previous, state);
            }
            if ((block = state.getBlock()) instanceof EntityBlock) {
                EntityBlock entityBlock = (EntityBlock)block;
                BlockEntity blockEntity = entityBlock.newBlockEntity(pos.immutable(), state);
                if (blockEntity != null) {
                    chunk.setBlockEntity(blockEntity);
                }
            } else {
                chunk.removeBlockEntity(pos);
            }
            long beChunkKey = ChunkPos.pack((int)(pos.getX() >> 4), (int)(pos.getZ() >> 4));
            int beSectionY = pos.getY() >> 4;
            Map<Integer, List<CompoundTag>> tagsBySection = this.this$0.blockEntityTags.get(beChunkKey);
            if (tagsBySection != null) {
                tagsBySection.remove(beSectionY);
            }
            if ((positionsBySection = this.this$0.blockEntityPositions.get(beChunkKey)) != null) {
                positionsBySection.remove(beSectionY);
            }
            return true;
        }

        public boolean ensureCanWrite(BlockPos pos) {
            return super.ensureCanWrite(pos);
        }

        public boolean isWithinWriteZone(BlockPos pos) {
            return super.isWithinWriteZone(pos);
        }

        public int getHeight(Heightmap.Types type, int x, int z) {
            return this.ensure(x, z).getHeight(type, x & 0xF, z & 0xF) + 1;
        }

        public int getMinY() {
            return this.this$0.bounds.getMinY();
        }

        public int getHeight() {
            return this.this$0.bounds.getHeight();
        }
    }

    private static final class ScratchHolder
    extends GenerationChunkHolder {
        private final ChunkAccess chunk;

        private ScratchHolder(ChunkPos pos, ChunkAccess chunk) {
            super(pos);
            this.chunk = chunk;
        }

        public void addSaveDependency(CompletableFuture<?> sync) {
        }

        public int getTicketLevel() {
            return 33;
        }

        public int getQueueLevel() {
            return 0;
        }

        public ChunkAccess getChunkIfPresentUnchecked(ChunkStatus status) {
            return this.chunk;
        }

        public ChunkAccess getChunkIfPresent(ChunkStatus status) {
            return this.chunk;
        }
    }
}


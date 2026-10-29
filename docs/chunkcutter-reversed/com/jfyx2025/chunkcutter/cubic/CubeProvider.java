/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager
 *  net.minecraft.world.level.levelgen.Heightmap
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeSectionStore;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.cubic.SectionEntityController;
import com.jfyx2025.chunkcutter.cubic.SectionLevelChunk;
import com.jfyx2025.chunkcutter.cubic.SectionLoadMetrics;
import com.jfyx2025.chunkcutter.cubic.terrain.SectionFiller;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainSources;
import com.jfyx2025.chunkcutter.mixin.ServerLevelAccessor;
import com.jfyx2025.chunkcutter.network.SectionsPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.jspecify.annotations.Nullable;

public final class CubeProvider {
    private static final Map<PendingKey, CompletableFuture<Void>> PENDING_GENERATIONS = new ConcurrentHashMap<PendingKey, CompletableFuture<Void>>();
    private static final Map<PendingKey, Long> PENDING_SINCE = new ConcurrentHashMap<PendingKey, Long>();
    private static final Set<PendingKey> PENDING_LOADS = ConcurrentHashMap.newKeySet();
    private static final Map<String, Set<Long>> SECTIONS_SENT = new ConcurrentHashMap<String, Set<Long>>();
    private static final AtomicReference<ExecutorService[]> TERRAIN_EXECUTORS = new AtomicReference();
    private static final Map<ResourceKey<Level>, CubeMap> BOOTSTRAP_CUBES = new ConcurrentHashMap<ResourceKey<Level>, CubeMap>();
    public static volatile boolean PREPARING = false;
    private static final Set<String> PRELOADED = ConcurrentHashMap.newKeySet();
    private static final Map<ResourceKey<Level>, Set<Long>> PRELOADED_CUBES = new ConcurrentHashMap<ResourceKey<Level>, Set<Long>>();

    private static PendingKey pendingKey(ServerLevel level, long node) {
        return new PendingKey((ResourceKey<Level>)level.dimension(), node);
    }

    private CubeProvider() {
    }

    private static ExecutorService[] terrainExecutors() {
        return TERRAIN_EXECUTORS.updateAndGet(existing -> {
            if (existing != null && ((ExecutorService[])existing).length > 0 && !existing[0].isShutdown()) {
                return existing;
            }
            int threads = Math.max(1, Math.min(255, Runtime.getRuntime().availableProcessors() - 1));
            ExecutorService[] shards = new ExecutorService[threads];
            for (int i = 0; i < threads; ++i) {
                int index = i;
                shards[i] = Executors.newSingleThreadExecutor(task -> {
                    Thread thread = new Thread(task, "ChunkCutter-Terrain-" + index);
                    thread.setDaemon(true);
                    return thread;
                });
            }
            return shards;
        });
    }

    private static ExecutorService terrainExecutorFor(int cx, int cz) {
        ExecutorService[] shards = CubeProvider.terrainExecutors();
        int index = Math.floorMod(Long.hashCode(ChunkPos.pack((int)cx, (int)cz)), shards.length);
        return shards[index];
    }

    public static void shutdownTerrainExecutor() {
        ExecutorService[] shards = TERRAIN_EXECUTORS.getAndSet(null);
        if (shards == null) {
            return;
        }
        for (ExecutorService executor : shards) {
            executor.shutdownNow();
        }
        for (ExecutorService executor : shards) {
            try {
                executor.awaitTermination(2L, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static CubeMap cubes(ServerLevel level) {
        CubeMap map = ((CubeSource)level.getChunkSource()).chunkcutter$cubeMap();
        return map == null ? BOOTSTRAP_CUBES.computeIfAbsent((ResourceKey<Level>)level.dimension(), ignored -> new CubeMap()) : map;
    }

    public static void loadCube(ServerLevel level, CloPos clo, PalettedContainerFactory factory, CubeMap map) {
        if (map.contains(clo.asLong())) {
            return;
        }
        try {
            CubeSectionStore.Loaded data = CubeSectionStore.load((Level)level, clo, factory);
            if (data != null && data.section() != null) {
                CubeProvider.install(level, clo, data, map);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void requestLoad(ServerLevel level, CloPos clo, PalettedContainerFactory factory, CubeMap map) {
        long node = clo.asLong();
        PendingKey key = CubeProvider.pendingKey(level, node);
        if (map.contains(node) || PENDING_LOADS.contains(key) || PENDING_GENERATIONS.containsKey(key)) {
            return;
        }
        if (!PENDING_LOADS.add(key)) {
            return;
        }
        try {
            long submitted = System.nanoTime();
            CompletableFuture.runAsync(() -> {
                long started = System.nanoTime();
                CubeSectionStore.Loaded loaded = null;
                try {
                    loaded = CubeSectionStore.load((Level)level, clo, factory);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                SectionLoadMetrics.recordLoad(started - submitted, System.nanoTime() - started);
                CubeSectionStore.Loaded result = loaded;
                level.getServer().execute(() -> {
                    long installStarted = System.nanoTime();
                    try {
                        if (!level.getServer().isRunning()) {
                            return;
                        }
                        if (result != null && result.section() != null) {
                            CubeProvider.install(level, clo, result, CubeProvider.cubes(level));
                        } else if (!CubeProvider.cubes(level).contains(node)) {
                            PENDING_LOADS.remove(key);
                            CubeProvider.requestGenerate(level, clo);
                        }
                    }
                    finally {
                        SectionLoadMetrics.recordInstall(System.nanoTime() - installStarted);
                        PENDING_LOADS.remove(key);
                    }
                });
            }, CubeProvider.terrainExecutorFor(clo.x(), clo.z()));
        }
        catch (RejectedExecutionException e) {
            PENDING_LOADS.remove(key);
        }
    }

    public static void enqueueMissingSection(ServerLevel level, int sx, int sy, int sz) {
        CloPos clo = CloPos.cube(sx, sy, sz);
        if (CubeProvider.cubes(level).contains(clo.asLong()) || CubeProvider.isPending(level, clo)) {
            return;
        }
        ChunkCutterMod.sectionGenerationQueue((ResourceKey<Level>)level.dimension()).add(level, clo.asLong());
    }

    public static void loadWantedColumn(ServerLevel level, int cx, int cz, PalettedContainerFactory factory, CubeMap map) {
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
        ArrayList<Integer> wantedY = new ArrayList<Integer>();
        for (int sy2 = level.getMinSectionY(); sy2 <= level.getMaxSectionY(); ++sy2) {
            if (!clm.wants(cx, sy2, cz)) continue;
            wantedY.add(sy2);
        }
        wantedY.sort(Comparator.comparingInt(sy -> sy).reversed());
        Iterator iterator = wantedY.iterator();
        while (iterator.hasNext()) {
            int sy3 = (Integer)iterator.next();
            CloPos clo = CloPos.cube(cx, sy3, cz);
            long node = clo.asLong();
            if (map.contains(node) || CubeProvider.isPending(level, clo)) continue;
            CubeProvider.requestLoad(level, clo, factory, map);
        }
    }

    private static void install(ServerLevel level, CloPos clo, CubeSectionStore.Loaded data, CubeMap map) {
        int idx;
        LevelChunkSection section = data.section();
        if (section == null) {
            return;
        }
        CubeChunk cube = new CubeChunk(clo, level.palettedContainerFactory());
        cube.setSection(section);
        cube.chunkcutter$setBlockEntities(data.blockEntities());
        map.put(cube);
        ChunkCutterMod.sectionGenerationQueue((ResourceKey<Level>)level.dimension()).remove(clo.asLong());
        CubeProvider.refreshEntityStatus(level, clo);
        LevelChunk column = CubeProvider.columnChunk(level, clo.x(), clo.z());
        if (column != null && (idx = column.getSectionIndexFromSectionY(clo.y())) >= 0 && idx < column.getSectionsCount()) {
            column.getSections()[idx] = section;
            if (column instanceof SectionLevelChunk) {
                SectionLevelChunk view = (SectionLevelChunk)column;
                view.chunkcutter$setSection(idx, section);
            }
            CubeProvider.restoreBlockEntities((ChunkAccess)column, section, data.blockEntities(), level);
        }
        if (data.blockLight() != null) {
            level.getLightEngine().queueSectionData(LightLayer.BLOCK, clo.asSectionPos(), data.blockLight());
        }
        if (data.skyLight() != null) {
            level.getLightEngine().queueSectionData(LightLayer.SKY, clo.asSectionPos(), data.skyLight());
        }
        level.getLightEngine().updateSectionStatus(clo.asSectionPos(), section.hasOnlyAir());
        CubeProvider.sendSection(level, clo, section, data.blockEntities());
    }

    public static void refreshEntityStatus(ServerLevel level, CloPos clo) {
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
        boolean accessible = clm.wants(clo.x(), clo.y(), clo.z()) || clm.keep(clo.x(), clo.y(), clo.z());
        boolean ticking = clm.ticking(clo.x(), clo.y(), clo.z());
        CubeProvider.setEntityStatus(level, clo, accessible, ticking);
    }

    private static void setEntityStatus(ServerLevel level, CloPos clo, boolean accessible, boolean ticking) {
        ServerLevelAccessor acc;
        PersistentEntitySectionManager<Entity> manager;
        if (level instanceof ServerLevelAccessor && (manager = (acc = (ServerLevelAccessor)level).chunkcutter$entityManager()) instanceof SectionEntityController) {
            SectionEntityController controller = (SectionEntityController)manager;
            long sectionKey = clo.asSectionPos().asLong();
            controller.chunkcutter$setSectionStatus(sectionKey, accessible, ticking);
            if (accessible) {
                controller.chunkcutter$loadSectionEntities(sectionKey);
            }
        }
    }

    private static void sendSection(ServerLevel level, CloPos clo, LevelChunkSection section, List<CompoundTag> blockEntities) {
        SectionsPayload.SectionEntry entry = CubeProvider.encodeSection(level, clo, section, blockEntities);
        for (ServerPlayer player : level.players()) {
            int view;
            if (!CubeProvider.within3D(player, clo, view = Math.max(1, player.requestedViewDistance()))) continue;
            CubeProvider.sentSections(player, level).add(clo.asLong());
            ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)new SectionsPayload(level.dimension().identifier(), List.of(entry)));
        }
    }

    private static String sentKey(ServerPlayer player, ServerLevel level) {
        return String.valueOf(player.getUUID()) + "@" + String.valueOf(level.dimension().identifier());
    }

    private static Set<Long> sentSections(ServerPlayer player, ServerLevel level) {
        return SECTIONS_SENT.computeIfAbsent(CubeProvider.sentKey(player, level), ignored -> ConcurrentHashMap.newKeySet());
    }

    public static void clearSentSections(ServerPlayer player) {
        String prefix = String.valueOf(player.getUUID()) + "@";
        SECTIONS_SENT.keySet().removeIf(key -> key.startsWith(prefix));
    }

    public static void sendLoadedSections(ServerPlayer player) {
        ServerLevel serverLevel = player.level();
        if (!(serverLevel instanceof ServerLevel)) {
            return;
        }
        ServerLevel level = serverLevel;
        CubeMap map = CubeProvider.cubes(level);
        Set<Long> sent = CubeProvider.sentSections(player, level);
        int view = Math.max(1, player.requestedViewDistance());
        ArrayList<SectionsPayload.SectionEntry> batch = new ArrayList<SectionsPayload.SectionEntry>();
        for (long key : map.keys().toLongArray()) {
            CubeChunk cube;
            CloPos clo = CloPos.cubeOf(key);
            if (!CubeProvider.within3D(player, clo, view) || !sent.add(key) || (cube = map.get(clo)) == null || cube.section() == null) continue;
            batch.add(CubeProvider.encodeSection(level, clo, cube.section(), cube.chunkcutter$blockEntities()));
            if (batch.size() < 64) continue;
            ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)new SectionsPayload(level.dimension().identifier(), batch));
            batch = new ArrayList();
        }
        if (!batch.isEmpty()) {
            ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)new SectionsPayload(level.dimension().identifier(), batch));
        }
    }

    private static void sendSectionRemoval(ServerLevel level, CloPos clo) {
        SectionsPayload payload = new SectionsPayload(level.dimension().identifier(), List.of(new SectionsPayload.SectionEntry(clo.asLong(), new byte[0], null, null, List.of())));
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)payload);
        }
    }

    private static boolean within3D(ServerPlayer player, CloPos clo, int viewDistance) {
        int psx = SectionPos.blockToSectionCoord((int)player.getBlockX());
        int psy = SectionPos.blockToSectionCoord((int)player.getBlockY());
        int psz = SectionPos.blockToSectionCoord((int)player.getBlockZ());
        return Math.abs(clo.x() - psx) <= viewDistance && Math.abs(clo.y() - psy) <= viewDistance && Math.abs(clo.z() - psz) <= viewDistance;
    }

    public static SectionsPayload.SectionEntry encodeSection(ServerLevel level, CloPos clo, LevelChunkSection section, List<CompoundTag> blockEntities) {
        ByteBuf raw = Unpooled.buffer();
        FriendlyByteBuf fb = new FriendlyByteBuf(raw);
        section.write(fb);
        byte[] data = new byte[fb.writerIndex()];
        fb.getBytes(0, data);
        return new SectionsPayload.SectionEntry(clo.asLong(), data, CubeProvider.lightBytes(level, LightLayer.BLOCK, clo), CubeProvider.lightBytes(level, LightLayer.SKY, clo), blockEntities);
    }

    private static byte[] lightBytes(ServerLevel level, LightLayer layer, CloPos clo) {
        try {
            DataLayer layerData = level.getLightEngine().getLayerListener(layer).getDataLayerData(clo.asSectionPos());
            return layerData == null ? null : (byte[])layerData.getData().clone();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean unloadCube(ServerLevel level, CloPos clo, PalettedContainerFactory factory, CubeMap map) {
        ServerLevelAccessor acc;
        PersistentEntitySectionManager<Entity> manager;
        CubeChunk cube;
        Set<Long> protectedCubes = PRELOADED_CUBES.get(level.dimension());
        if (protectedCubes != null && protectedCubes.contains(clo.asLong())) {
            CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
            if (clm.wants(clo.x(), clo.y(), clo.z()) || clm.keep(clo.x(), clo.y(), clo.z())) {
                return true;
            }
            protectedCubes.remove(clo.asLong());
        }
        if ((cube = map.get(clo)) == null) {
            return true;
        }
        if (level instanceof ServerLevelAccessor && (manager = (acc = (ServerLevelAccessor)level).chunkcutter$entityManager()) instanceof SectionEntityController) {
            SectionEntityController controller = (SectionEntityController)manager;
            controller.chunkcutter$unloadSectionEntities(clo.asSectionPos().asLong());
        }
        List<CompoundTag> blockEntities = cube.chunkcutter$blockEntities();
        LevelChunk column = CubeProvider.columnChunk(level, clo.x(), clo.z());
        if (column != null) {
            blockEntities = CubeProvider.collectBeTags((ChunkAccess)column, CubeProvider.blockEntitiesInSection((ChunkAccess)column, clo.y()), level);
        }
        try {
            CubeSectionStore.save((Level)level, clo, cube.section(), factory, blockEntities);
        }
        catch (Throwable t) {
            return false;
        }
        CubeProvider.setEntityStatus(level, clo, false, false);
        CubeProvider.sendSectionRemoval(level, clo);
        for (ServerPlayer player : level.players()) {
            CubeProvider.sentSections(player, level).remove(clo.asLong());
        }
        map.remove(clo.asLong());
        return true;
    }

    public static CompletableFuture<Void> adoptColumn(ServerLevel level, LevelChunk column) {
        ChunkGenerator chunkGenerator;
        if (!ChunkCutterConfig.isEnabled()) {
            return CompletableFuture.completedFuture(null);
        }
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
        CubeMap map = CubeProvider.cubes(level);
        PalettedContainerFactory factory = level.palettedContainerFactory();
        ChunkPos pos = column.getPos();
        int cx = pos.x();
        int cz = pos.z();
        LevelChunkSection[] sections = column.getSections();
        if (ChunkCutterConfig.isEnabled() && (chunkGenerator = level.getChunkSource().getGenerator()) instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator noiseGen = (NoiseBasedChunkGenerator)chunkGenerator;
            boolean spawnPhase = level.players().isEmpty();
            for (int sy = level.getMaxSectionY(); sy >= level.getMinSectionY(); --sy) {
                CloPos clo;
                long key;
                if (!clm.wants(cx, sy, cz) || map.contains(key = (clo = CloPos.cube(cx, sy, cz)).asLong()) || CubeProvider.isPending(level, clo)) continue;
                CubeProvider.requestLoad(level, clo, factory, map);
            }
            return CompletableFuture.completedFuture(null).thenRun(() -> CubeProvider.primeColumnHeightmaps(column));
        }
        for (int i = 0; i < sections.length; ++i) {
            LevelChunkSection section = sections[i];
            if (section == null || section.hasOnlyAir()) continue;
            int sy = column.getSectionYFromSectionIndex(i);
            CloPos clo = CloPos.cube(cx, sy, cz);
            long key = clo.asLong();
            if (clm.wants(cx, sy, cz)) {
                if (ChunkCutterConfig.isEnabled()) {
                    if (!map.contains(key)) {
                        CubeProvider.generateNative(level, clo, section.copy());
                    }
                } else if (!map.contains(key)) {
                    CubeChunk cube = new CubeChunk(clo, factory);
                    cube.setSection(section);
                    map.put(cube);
                }
                CubeProvider.refreshEntityStatus(level, clo);
                continue;
            }
            CubeProvider.setEntityStatus(level, clo, false, false);
        }
        return CompletableFuture.completedFuture(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void preloadSpawn(ServerLevel level) {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        if (!PRELOADED.add("spawn@" + String.valueOf(level.dimension().identifier()))) {
            return;
        }
        BlockPos spawn = level.getRespawnData().pos();
        int px = SectionPos.blockToSectionCoord((int)spawn.getX());
        int py = SectionPos.blockToSectionCoord((int)spawn.getY());
        int pz = SectionPos.blockToSectionCoord((int)spawn.getZ());
        Set protectedCubes = PRELOADED_CUBES.computeIfAbsent((ResourceKey<Level>)level.dimension(), ignored -> ConcurrentHashMap.newKeySet());
        CubeMap map = CubeProvider.cubes(level);
        PREPARING = true;
        try {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    for (int dy = 1; dy >= -1; --dy) {
                        CloPos clo = CloPos.cube(px + dx, py + dy, pz + dz);
                        long node = clo.asLong();
                        protectedCubes.add(node);
                        if (map.contains(node)) continue;
                        try {
                            SectionFiller.Filled filled = SectionFiller.fill(level, clo);
                            if (filled == null || filled.section() == null) continue;
                            CubeProvider.installGenerated(level, clo, filled.section(), filled.blockEntities(), null);
                            continue;
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                }
            }
        }
        finally {
            PREPARING = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void prepareSpawnColumn(ServerLevel level, int x, int y, int z) {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        int px = SectionPos.blockToSectionCoord((int)x);
        int py = SectionPos.blockToSectionCoord((int)y);
        int pz = SectionPos.blockToSectionCoord((int)z);
        PREPARING = true;
        try {
            for (int dy = 1; dy >= -1; --dy) {
                CloPos clo = CloPos.cube(px, py + dy, pz);
                if (CubeProvider.cubes(level).contains(clo.asLong())) continue;
                try {
                    SectionFiller.Filled filled = SectionFiller.fill(level, clo);
                    if (filled == null || filled.section() == null) continue;
                    CubeProvider.installGenerated(level, clo, filled.section(), filled.blockEntities(), null);
                    continue;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        finally {
            PREPARING = false;
        }
    }

    public static void preloadPlayer(ServerLevel level, ServerPlayer player) {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        String key = String.valueOf(player.getUUID()) + "@" + String.valueOf(level.dimension().identifier());
        if (!PRELOADED.add(key)) {
            return;
        }
        CubeProvider.preloadArea(level, SectionPos.blockToSectionCoord((int)player.getBlockX()), SectionPos.blockToSectionCoord((int)player.getBlockY()), SectionPos.blockToSectionCoord((int)player.getBlockZ()));
    }

    private static void preloadArea(ServerLevel level, int px, int py, int pz) {
        Set protectedCubes = PRELOADED_CUBES.computeIfAbsent((ResourceKey<Level>)level.dimension(), ignored -> ConcurrentHashMap.newKeySet());
        CubeMap map = CubeProvider.cubes(level);
        PalettedContainerFactory factory = level.palettedContainerFactory();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                for (int dy = 1; dy >= -1; --dy) {
                    CloPos clo = CloPos.cube(px + dx, py + dy, pz + dz);
                    long node = clo.asLong();
                    protectedCubes.add(node);
                    if (map.contains(node) || CubeProvider.isPending(level, clo)) continue;
                    CubeProvider.requestLoad(level, clo, factory, map);
                }
            }
        }
    }

    public static void clearPreloaded(ServerPlayer player) {
        String prefix = String.valueOf(player.getUUID()) + "@";
        PRELOADED.removeIf(key -> key.startsWith(prefix));
        CubeProvider.clearSentSections(player);
    }

    public static void clearAllPreloaded() {
        PRELOADED.clear();
        PRELOADED_CUBES.clear();
        BOOTSTRAP_CUBES.clear();
        SECTIONS_SENT.clear();
    }

    private static CompletableFuture<Void> generateNative(ServerLevel level, CloPos clo, LevelChunkSection columnSnapshot) {
        CompletionStage future;
        if (!level.getServer().isRunning()) {
            return CompletableFuture.completedFuture(null);
        }
        long node = clo.asLong();
        PendingKey key = CubeProvider.pendingKey(level, node);
        CompletableFuture<Void> existing = PENDING_GENERATIONS.get(key);
        if (existing != null) {
            return existing;
        }
        try {
            future = CompletableFuture.supplyAsync(() -> SectionFiller.fill(level, clo), CubeProvider.terrainExecutorFor(clo.x(), clo.z())).thenCompose(gen -> {
                if (gen == null || gen.section() == null || !level.getServer().isRunning()) {
                    return CompletableFuture.completedFuture(null);
                }
                CompletableFuture installed = new CompletableFuture();
                level.getServer().execute(() -> {
                    long started = System.nanoTime();
                    try {
                        CubeProvider.installGenerated(level, clo, gen.section(), gen.blockEntities(), columnSnapshot);
                        installed.complete(null);
                    }
                    catch (Throwable t) {
                        installed.completeExceptionally(t);
                    }
                    finally {
                        SectionLoadMetrics.recordInstall(System.nanoTime() - started);
                    }
                });
                return installed;
            });
        }
        catch (RejectedExecutionException e) {
            return CompletableFuture.completedFuture(null);
        }
        PENDING_GENERATIONS.put(key, (CompletableFuture<Void>)future);
        PENDING_SINCE.put(key, System.nanoTime());
        ((CompletableFuture)future).whenComplete((arg_0, arg_1) -> CubeProvider.lambda$generateNative$3(key, (CompletableFuture)future, arg_0, arg_1));
        return future;
    }

    public static boolean isPending(ServerLevel level, CloPos clo) {
        PendingKey key = CubeProvider.pendingKey(level, clo.asLong());
        return PENDING_GENERATIONS.containsKey(key) || PENDING_LOADS.contains(key);
    }

    public static int pendingCount() {
        return PENDING_GENERATIONS.size() + PENDING_LOADS.size();
    }

    public static int preloadedCount(ServerLevel level) {
        Set<Long> keys = PRELOADED_CUBES.get(level.dimension());
        return keys == null ? 0 : keys.size();
    }

    public static int preloadedLoadedCount(ServerLevel level) {
        Set<Long> keys = PRELOADED_CUBES.get(level.dimension());
        if (keys == null) {
            return 0;
        }
        CubeMap map = CubeProvider.cubes(level);
        int loaded = 0;
        for (long key : keys) {
            if (!map.contains(key)) continue;
            ++loaded;
        }
        return loaded;
    }

    public static long oldestPendingAgeMs() {
        long oldest = Long.MAX_VALUE;
        for (long since : PENDING_SINCE.values()) {
            if (since >= oldest) continue;
            oldest = since;
        }
        return oldest == Long.MAX_VALUE ? 0L : (System.nanoTime() - oldest) / 1000000L;
    }

    public static long oldestPendingNode() {
        long node = Long.MIN_VALUE;
        long oldest = Long.MAX_VALUE;
        for (Map.Entry<PendingKey, Long> entry : PENDING_SINCE.entrySet()) {
            if (entry.getValue() >= oldest) continue;
            oldest = entry.getValue();
            node = entry.getKey().node();
        }
        return node;
    }

    public static void ensureSectionForWrite(ServerLevel level, int sx, int sy, int sz, LevelChunk column) {
        CloPos clo;
        long key;
        CubeMap map = CubeProvider.cubes(level);
        if (!map.contains(key = (clo = CloPos.cube(sx, sy, sz)).asLong())) {
            CubeProvider.requestLoad(level, clo, level.palettedContainerFactory(), map);
            return;
        }
        CubeChunk cube = map.get(clo);
        if (cube == null || cube.section() == null) {
            return;
        }
        int idx = column.getSectionIndexFromSectionY(sy);
        if (idx >= 0 && idx < column.getSectionsCount() && column.getSections()[idx] != cube.section()) {
            column.getSections()[idx] = cube.section();
        }
    }

    public static boolean requestGenerate(ServerLevel level, CloPos clo) {
        if (!ChunkCutterConfig.isEnabled()) {
            return false;
        }
        long node = clo.asLong();
        if (CubeProvider.cubes(level).contains(node)) {
            return false;
        }
        PendingKey key = CubeProvider.pendingKey(level, node);
        if (PENDING_GENERATIONS.containsKey(key) || PENDING_LOADS.contains(key)) {
            return true;
        }
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
        if (!clm.wants(clo.x(), clo.y(), clo.z())) {
            return false;
        }
        CubeProvider.generateNative(level, clo, null);
        return true;
    }

    public static void installGenerated(ServerLevel level, CloPos clo, LevelChunkSection section, List<CompoundTag> generatedBlockEntities, LevelChunkSection columnSnapshot) {
        LevelChunk column;
        if (!level.getServer().isRunning() && !PREPARING) {
            return;
        }
        CubeMap map = CubeProvider.cubes(level);
        if (map.contains(clo.asLong())) {
            return;
        }
        if (columnSnapshot != null) {
            CubeProvider.mergeColumnFeatures(section, columnSnapshot);
        }
        if ((column = CubeProvider.columnChunk(level, clo.x(), clo.z())) != null) {
            for (BlockPos blockEntityPos : CubeProvider.blockEntitiesInSection((ChunkAccess)column, clo.y())) {
                column.removeBlockEntity(blockEntityPos);
            }
        }
        List<CompoundTag> beTags = generatedBlockEntities == null ? List.of() : generatedBlockEntities;
        CubeProvider.install(level, clo, new CubeSectionStore.Loaded(section, beTags, null, null), map);
        if (column != null) {
            CubeProvider.primeColumnHeightmaps(column);
        }
        try {
            CubeSectionStore.save((Level)level, clo, section, level.palettedContainerFactory(), beTags);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void primeColumnHeightmaps(LevelChunk column) {
        Heightmap.primeHeightmaps((ChunkAccess)column, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
    }

    private static void mergeColumnFeatures(LevelChunkSection target, LevelChunkSection source) {
        for (int y = 0; y < 16; ++y) {
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) {
                    BlockState dst;
                    BlockState src = source.getBlockState(x, y, z);
                    if (src.isAir() || src.equals((Object)(dst = target.getBlockState(x, y, z)))) continue;
                    target.setBlockState(x, y, z, src);
                }
            }
        }
    }

    public static @Nullable LevelChunk columnChunk(ServerLevel level, int cx, int cz) {
        return level.getChunkSource().getChunkNow(cx, cz);
    }

    public static void unloadColumnCubes(ServerLevel level, int cx, int cz, PalettedContainerFactory factory, CubeMap map) {
        long[] keys;
        if (map == null) {
            return;
        }
        for (long key : keys = map.keys().toLongArray()) {
            CloPos pos = CloPos.cubeOf(key);
            if (pos.x() != cx || pos.z() != cz) continue;
            CubeProvider.unloadCube(level, pos, factory, map);
        }
        TerrainSources.clearColumn(level, cx, cz);
    }

    public static int saveAll(ServerLevel level) {
        ServerChunkCache serverChunkCache = level.getChunkSource();
        if (!(serverChunkCache instanceof CubeSource)) {
            return 0;
        }
        CubeSource source = (CubeSource)serverChunkCache;
        CubeMap map = source.chunkcutter$cubeMap();
        if (map == null) {
            return 0;
        }
        int saved = 0;
        for (long key : map.keys().toLongArray()) {
            CubeChunk cube = map.get(key);
            if (cube == null || cube.section() == null) continue;
            CloPos clo = CloPos.cubeOf(key);
            LevelChunk column = CubeProvider.columnChunk(level, clo.x(), clo.z());
            List<BlockPos> bePositions = column == null ? List.of() : CubeProvider.blockEntitiesInSection((ChunkAccess)column, clo.y());
            List<CompoundTag> beTags = column == null ? cube.chunkcutter$blockEntities() : CubeProvider.collectBeTags((ChunkAccess)column, bePositions, level);
            LevelChunkSection section = cube.section();
            if (section.hasOnlyAir() && (beTags == null || beTags.isEmpty())) continue;
            try {
                CubeSectionStore.save((Level)level, clo, section, level.palettedContainerFactory(), beTags);
                ++saved;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return saved;
    }

    public static void clearPending() {
        for (CompletableFuture<Void> future : PENDING_GENERATIONS.values()) {
            future.cancel(true);
        }
        PENDING_GENERATIONS.clear();
        PENDING_SINCE.clear();
        PENDING_LOADS.clear();
    }

    public static void clearLogs() {
    }

    private static List<BlockPos> blockEntitiesInSection(ChunkAccess chunk, int sectionY) {
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (BlockPos bp : chunk.getBlockEntitiesPos()) {
            if (SectionPos.blockToSectionCoord((int)bp.getY()) != sectionY) continue;
            result.add(bp);
        }
        return result;
    }

    private static List<CompoundTag> collectBeTags(ChunkAccess chunk, List<BlockPos> positions, ServerLevel level) {
        ArrayList<CompoundTag> tags = new ArrayList<CompoundTag>(positions.size());
        for (BlockPos bp : positions) {
            CompoundTag t = chunk.getBlockEntityNbtForSaving(bp, (HolderLookup.Provider)level.registryAccess());
            if (t == null) continue;
            tags.add(t);
        }
        return tags;
    }

    private static void restoreBlockEntities(ChunkAccess chunk, LevelChunkSection section, List<CompoundTag> beTags, ServerLevel level) {
        for (CompoundTag t : beTags) {
            BlockState st;
            BlockPos bp = new BlockPos(t.getIntOr("x", 0), t.getIntOr("y", 0), t.getIntOr("z", 0));
            BlockEntity be = BlockEntity.loadStatic((BlockPos)bp, (BlockState)(st = section.getBlockState(bp.getX() & 0xF, bp.getY() & 0xF, bp.getZ() & 0xF)), (CompoundTag)t, (HolderLookup.Provider)level.registryAccess());
            if (be == null) continue;
            chunk.setBlockEntity(be);
        }
    }

    private static /* synthetic */ void lambda$generateNative$3(PendingKey key, CompletableFuture future, Void v, Throwable e) {
        if (e != null) {
            // empty if block
        }
        PENDING_GENERATIONS.remove(key, future);
        PENDING_SINCE.remove(key);
    }

    private record PendingKey(ResourceKey<Level> dimension, long node) {
    }
}


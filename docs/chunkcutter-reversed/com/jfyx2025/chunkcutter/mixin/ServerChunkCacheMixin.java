/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkLevel
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkResult
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerChunkCache$MainThreadExecutor
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.ThreadedLevelLightEngine
 *  net.minecraft.server.level.Ticket
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.Util
 *  net.minecraft.util.VisibleForDebug
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.ai.village.poi.PoiManager
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.LocalMobCapCalculator
 *  net.minecraft.world.level.NaturalSpawner
 *  net.minecraft.world.level.NaturalSpawner$SpawnState
 *  net.minecraft.world.level.TicketStorage
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.ChunkGeneratorStructureState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LightChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.chunk.storage.ChunkScanAccess
 *  net.minecraft.world.level.gamerules.GameRules
 *  net.minecraft.world.level.levelgen.RandomState
 *  net.minecraft.world.level.storage.SavedDataStorage
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.annotations.VisibleForTesting;
import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerChunkCache.class})
public abstract class ServerChunkCacheMixin
implements CubeSource {
    private CubeMap chunkcutter$bootstrapCubes;
    @Shadow
    private DistanceManager distanceManager;
    @Shadow
    private ServerLevel level;
    @Shadow
    private Thread mainThread;
    @Shadow
    private ThreadedLevelLightEngine lightEngine;
    @Shadow
    private ServerChunkCache.MainThreadExecutor mainThreadProcessor;
    @Shadow
    public ChunkMap chunkMap;
    @Shadow
    private SavedDataStorage savedDataStorage;
    @Shadow
    private TicketStorage ticketStorage;
    @Shadow
    private long lastInhabitedUpdate;
    @Shadow
    private boolean spawnEnemies;
    @Shadow
    private static int CACHE_SIZE;
    @Shadow
    private long[] lastChunkPos;
    @Shadow
    private @Nullable ChunkStatus[] lastChunkStatus;
    @Shadow
    private @Nullable ChunkAccess[] lastChunk;
    @Shadow
    private List<LevelChunk> spawningChunks;
    @Shadow
    private Set<ChunkHolder> chunkHoldersToBroadcast;
    @Shadow
    @VisibleForDebug
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable NaturalSpawner.SpawnState lastSpawnState;

    @Override
    public CubeMap chunkcutter$cubeMap() {
        if (this.chunkMap == null) {
            if (this.chunkcutter$bootstrapCubes == null) {
                this.chunkcutter$bootstrapCubes = new CubeMap();
            }
            return this.chunkcutter$bootstrapCubes;
        }
        return ((CubeSource)this.chunkMap).chunkcutter$cubeMap();
    }

    @Inject(method={"addTicketAndLoadWithRadius"}, at={@At(value="HEAD")}, cancellable=true)
    private void chunkcutter$skipPlayerSpawnChunks(TicketType type, ChunkPos pos, int radius, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        if (type == TicketType.PLAYER_SPAWN) {
            System.out.println("[CC-LOAD] skipped vanilla player spawn chunks r=" + radius + " at " + String.valueOf(pos));
            cir.setReturnValue(CompletableFuture.completedFuture(null));
        }
    }

    @Overwrite
    public ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Overwrite
    public @Nullable ChunkHolder getVisibleChunkIfPresent(long key) {
        return this.chunkMap.getVisibleChunkIfPresent(key);
    }

    @Overwrite
    public void storeInCache(long pos, @Nullable ChunkAccess chunk, ChunkStatus status) {
        for (int i = 3; i > 0; --i) {
            this.lastChunkPos[i] = this.lastChunkPos[i - 1];
            this.lastChunkStatus[i] = this.lastChunkStatus[i - 1];
            this.lastChunk[i] = this.lastChunk[i - 1];
        }
        this.lastChunkPos[0] = pos;
        this.lastChunkStatus[0] = status;
        this.lastChunk[0] = chunk;
    }

    @Overwrite
    public @Nullable ChunkAccess getChunk(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
        if (!this.mainThreadProcessor.isSameThread()) {
            return CompletableFuture.supplyAsync(() -> this.getChunk(x, z, targetStatus, loadOrGenerate), (Executor)this.mainThreadProcessor).join();
        }
        ProfilerFiller profiler = Profiler.get();
        profiler.incrementCounter("getChunk");
        long pos = ChunkPos.pack((int)x, (int)z);
        for (int i = 0; i < 4; ++i) {
            ChunkAccess chunkAccess;
            if (pos != this.lastChunkPos[i] || targetStatus != this.lastChunkStatus[i] || (chunkAccess = this.lastChunk[i]) == null && loadOrGenerate) continue;
            return chunkAccess;
        }
        profiler.incrementCounter("getChunkCacheMiss");
        CompletableFuture<ChunkResult<ChunkAccess>> serverFuture = this.getChunkFutureMainThread(x, z, targetStatus, loadOrGenerate);
        this.mainThreadProcessor.managedBlock(serverFuture::isDone);
        ChunkResult<ChunkAccess> chunkResult = serverFuture.join();
        ChunkAccess chunk = (ChunkAccess)chunkResult.orElse(null);
        if (chunk == null && loadOrGenerate) {
            throw (IllegalStateException)Util.pauseInIde((Throwable)new IllegalStateException("Chunk not there when requested: " + chunkResult.getError()));
        }
        this.storeInCache(pos, chunk, targetStatus);
        return chunk;
    }

    @Overwrite
    public @Nullable LevelChunk getChunkNow(int x, int z) {
        if (!this.mainThreadProcessor.isSameThread()) {
            return null;
        }
        Profiler.get().incrementCounter("getChunkNow");
        long pos = ChunkPos.pack((int)x, (int)z);
        for (int i = 0; i < 4; ++i) {
            LevelChunk levelChunk;
            if (pos != this.lastChunkPos[i] || this.lastChunkStatus[i] != ChunkStatus.FULL) continue;
            ChunkAccess chunkAccess = this.lastChunk[i];
            return chunkAccess instanceof LevelChunk ? (levelChunk = (LevelChunk)chunkAccess) : null;
        }
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(pos);
        if (chunkHolder == null) {
            return null;
        }
        ChunkAccess chunk = chunkHolder.getChunkIfPresent(ChunkStatus.FULL);
        if (chunk != null) {
            this.storeInCache(pos, chunk, ChunkStatus.FULL);
            if (chunk instanceof LevelChunk) {
                LevelChunk levelChunk = (LevelChunk)chunk;
                return levelChunk;
            }
        }
        return null;
    }

    @Overwrite
    public void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunkStatus, null);
        Arrays.fill(this.lastChunk, null);
    }

    @Overwrite
    public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
        CompletionStage<Object> serverFuture;
        boolean isMainThread = this.mainThreadProcessor.isSameThread();
        if (isMainThread) {
            serverFuture = this.getChunkFutureMainThread(x, z, targetStatus, loadOrGenerate);
            this.mainThreadProcessor.managedBlock(() -> serverFuture.isDone());
        } else {
            serverFuture = CompletableFuture.supplyAsync(() -> this.getChunkFutureMainThread(x, z, targetStatus, loadOrGenerate), (Executor)this.mainThreadProcessor).thenCompose(chunk -> chunk);
        }
        return serverFuture;
    }

    @Overwrite
    public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFutureMainThread(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
        ChunkPos pos = new ChunkPos(x, z);
        long key = pos.pack();
        int targetTicketLevel = ChunkLevel.byStatus((ChunkStatus)targetStatus);
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
        if (loadOrGenerate) {
            this.addTicket(new Ticket(TicketType.UNKNOWN, targetTicketLevel), pos);
            if (this.chunkAbsent(chunkHolder, targetTicketLevel)) {
                ProfilerFiller profiler = Profiler.get();
                profiler.push("chunkLoad");
                this.runDistanceManagerUpdates();
                chunkHolder = this.getVisibleChunkIfPresent(key);
                profiler.pop();
                if (this.chunkAbsent(chunkHolder, targetTicketLevel)) {
                    throw (IllegalStateException)Util.pauseInIde((Throwable)new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }
        return this.chunkAbsent(chunkHolder, targetTicketLevel) ? GenerationChunkHolder.UNLOADED_CHUNK_FUTURE : chunkHolder.scheduleChunkGenerationTask(targetStatus, this.chunkMap);
    }

    @Overwrite
    public boolean chunkAbsent(@Nullable ChunkHolder chunkHolder, int targetTicketLevel) {
        return chunkHolder == null || chunkHolder.getTicketLevel() > targetTicketLevel;
    }

    @Overwrite
    public boolean hasChunk(int x, int z) {
        int targetTicketLevel;
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(new ChunkPos(x, z).pack());
        return !this.chunkAbsent(chunkHolder, targetTicketLevel = ChunkLevel.byStatus((ChunkStatus)ChunkStatus.FULL));
    }

    @Overwrite
    public @Nullable LightChunk getChunkForLighting(int x, int z) {
        long key = ChunkPos.pack((int)x, (int)z);
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
        return chunkHolder == null ? null : chunkHolder.getChunkIfPresentUnchecked(ChunkStatus.INITIALIZE_LIGHT.getParent());
    }

    @Overwrite
    public Level getLevel() {
        return this.level;
    }

    @Overwrite
    public boolean pollTask() {
        return this.mainThreadProcessor.pollTask();
    }

    @Overwrite
    public boolean runDistanceManagerUpdates() {
        boolean updated = this.distanceManager.runAllUpdates(this.chunkMap);
        boolean promoted = this.chunkMap.promoteChunkMap();
        this.chunkMap.runGenerationTasks();
        if (!updated && !promoted) {
            return false;
        }
        this.clearCache();
        return true;
    }

    @Overwrite
    public boolean isPositionTicking(long chunkKey) {
        if (!this.level.shouldTickBlocksAt(chunkKey)) {
            return false;
        }
        ChunkHolder holder = this.getVisibleChunkIfPresent(chunkKey);
        return holder == null ? false : holder.getTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).isSuccess();
    }

    @Overwrite
    public void save(boolean flushStorage) {
        this.runDistanceManagerUpdates();
        this.chunkMap.saveAllChunks(flushStorage);
    }

    @Overwrite
    public void close() throws IOException {
        this.save(true);
        this.savedDataStorage.close();
        this.lightEngine.close();
        this.chunkMap.close();
    }

    @Overwrite
    public void tick(BooleanSupplier haveTime, boolean tickChunks) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("purge");
        if (this.level.tickRateManager().runsNormally() || !tickChunks) {
            this.ticketStorage.purgeStaleTickets(this.chunkMap);
        }
        this.runDistanceManagerUpdates();
        profiler.popPush("chunks");
        if (tickChunks) {
            this.tickChunks();
            this.chunkMap.tick();
        }
        profiler.popPush("unload");
        this.chunkMap.tick(haveTime);
        profiler.pop();
        this.clearCache();
    }

    @Overwrite
    public void tickChunks() {
        long time = this.level.getGameTime();
        long timeDiff = time - this.lastInhabitedUpdate;
        this.lastInhabitedUpdate = time;
        if (!this.level.isDebug()) {
            ProfilerFiller profiler = Profiler.get();
            profiler.push("pollingChunks");
            if (this.level.tickRateManager().runsNormally()) {
                profiler.push("tickingChunks");
                this.tickChunks(profiler, timeDiff);
                profiler.pop();
            }
            this.broadcastChangedChunks(profiler);
            profiler.pop();
        }
    }

    @Overwrite
    public void broadcastChangedChunks(ProfilerFiller profiler) {
        profiler.push("broadcast");
        for (ChunkHolder chunkHolder : this.chunkHoldersToBroadcast) {
            LevelChunk chunk = chunkHolder.getTickingChunk();
            if (chunk == null) continue;
            chunkHolder.broadcastChanges(chunk);
        }
        this.chunkHoldersToBroadcast.clear();
        profiler.pop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Overwrite
    public void tickChunks(ProfilerFiller profiler, long timeDiff) {
        List spawningCategories;
        NaturalSpawner.SpawnState spawnCookie;
        profiler.push("naturalSpawnCount");
        int chunkCount = this.distanceManager.getNaturalSpawnChunkCount();
        this.lastSpawnState = spawnCookie = NaturalSpawner.createState((int)chunkCount, (Iterable)this.level.getAllEntities(), this::getFullChunk, (LocalMobCapCalculator)new LocalMobCapCalculator(this.chunkMap));
        boolean doMobSpawning = (Boolean)this.level.getGameRules().get(GameRules.SPAWN_MOBS);
        int tickSpeed = (Integer)this.level.getGameRules().get(GameRules.RANDOM_TICK_SPEED);
        if (doMobSpawning) {
            boolean spawnPersistent = this.level.getGameTime() % 400L == 0L;
            spawningCategories = NaturalSpawner.getFilteredSpawningCategories((NaturalSpawner.SpawnState)spawnCookie, (boolean)this.spawnEnemies, (boolean)spawnPersistent);
        } else {
            spawningCategories = List.of();
        }
        List<LevelChunk> spawningChunks = this.spawningChunks;
        try {
            profiler.popPush("filteringSpawningChunks");
            this.chunkMap.collectSpawningChunks(spawningChunks);
            profiler.popPush("shuffleSpawningChunks");
            Util.shuffle(spawningChunks, (RandomSource)this.level.getRandom());
            profiler.popPush("tickSpawningChunks");
            for (LevelChunk chunk : spawningChunks) {
                this.tickSpawningChunk(chunk, timeDiff, spawningCategories, spawnCookie);
            }
        }
        finally {
            spawningChunks.clear();
        }
        profiler.popPush("tickTickingChunks");
        this.chunkMap.forEachBlockTickingChunk(chunkx -> this.level.tickChunk(chunkx, tickSpeed));
        if (doMobSpawning) {
            profiler.popPush("customSpawners");
            this.level.tickCustomSpawners(this.spawnEnemies);
        }
        profiler.pop();
    }

    @Overwrite
    public void tickSpawningChunk(LevelChunk chunk, long timeDiff, List<MobCategory> spawningCategories, NaturalSpawner.SpawnState spawnCookie) {
        ChunkPos chunkPos = chunk.getPos();
        chunk.incrementInhabitedTime(timeDiff);
        if (this.distanceManager.inEntityTickingRange(chunkPos.pack())) {
            this.level.tickThunder(chunk);
        }
        if (!spawningCategories.isEmpty() && this.level.canSpawnEntitiesInChunk(chunkPos)) {
            NaturalSpawner.spawnForChunk((ServerLevel)this.level, (LevelChunk)chunk, (NaturalSpawner.SpawnState)spawnCookie, spawningCategories);
        }
    }

    @Overwrite
    public void getFullChunk(long chunkKey, Consumer<LevelChunk> output) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(chunkKey);
        if (chunkHolder != null) {
            chunkHolder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).ifSuccess(output);
        }
    }

    @Overwrite
    public String gatherStats() {
        return Integer.toString(this.getLoadedChunksCount());
    }

    @Overwrite
    @VisibleForTesting
    public int getPendingTasksCount() {
        return this.mainThreadProcessor.getPendingTasksCount();
    }

    @Overwrite
    public ChunkGenerator getGenerator() {
        return this.chunkMap.generator();
    }

    @Overwrite
    public ChunkGeneratorStructureState getGeneratorState() {
        return this.chunkMap.generatorState();
    }

    @Overwrite
    public RandomState randomState() {
        return this.chunkMap.randomState();
    }

    @Overwrite
    public int getLoadedChunksCount() {
        return this.chunkMap.size();
    }

    @Overwrite
    public void blockChanged(BlockPos pos) {
        int zc;
        int xc = SectionPos.blockToSectionCoord((int)pos.getX());
        ChunkHolder chunk = this.getVisibleChunkIfPresent(ChunkPos.pack((int)xc, (int)(zc = SectionPos.blockToSectionCoord((int)pos.getZ()))));
        if (chunk != null && chunk.blockChanged(pos)) {
            this.chunkHoldersToBroadcast.add(chunk);
        }
    }

    @Overwrite
    public void onLightUpdate(LightLayer layer, SectionPos pos) {
        this.mainThreadProcessor.execute(() -> {
            ChunkHolder chunk = this.getVisibleChunkIfPresent(pos.chunk().pack());
            if (chunk != null && chunk.sectionLightChanged(layer, pos.y())) {
                this.chunkHoldersToBroadcast.add(chunk);
            }
        });
    }

    @Overwrite
    public boolean hasActiveTickets() {
        return this.ticketStorage.shouldKeepDimensionActive();
    }

    @Overwrite
    public void addTicket(Ticket ticket, ChunkPos pos) {
        this.ticketStorage.addTicket(ticket, pos);
    }

    @Overwrite
    public CompletableFuture<?> addTicketAndLoadWithRadius(TicketType type, ChunkPos pos, int radius) {
        if (!type.doesLoad()) {
            throw new IllegalStateException("Ticket type " + String.valueOf(type) + " does not trigger chunk loading");
        }
        if (type.canExpireIfUnloaded()) {
            throw new IllegalStateException("Ticket type " + String.valueOf(type) + " can expire before it loads, cannot fetch asynchronously");
        }
        this.addTicketWithRadius(type, pos, radius);
        this.runDistanceManagerUpdates();
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(pos.pack());
        Objects.requireNonNull(chunkHolder, "No chunk was scheduled for loading");
        return this.chunkMap.getChunkRangeFuture(chunkHolder, radius, distance -> ChunkStatus.FULL);
    }

    @Overwrite
    public void addTicketWithRadius(TicketType type, ChunkPos pos, int radius) {
        this.ticketStorage.addTicketWithRadius(type, pos, radius);
    }

    @Overwrite
    public void removeTicketWithRadius(TicketType type, ChunkPos pos, int radius) {
        this.ticketStorage.removeTicketWithRadius(type, pos, radius);
    }

    @Overwrite
    public boolean updateChunkForced(ChunkPos pos, boolean forced) {
        return this.ticketStorage.updateChunkForced(pos, forced);
    }

    @Overwrite
    public LongSet getForceLoadedChunks() {
        return this.ticketStorage.getForceLoadedChunks();
    }

    @Overwrite
    public void move(ServerPlayer player) {
        if (!player.isRemoved()) {
            this.chunkMap.move(player);
            if (player.isReceivingWaypoints()) {
                this.level.getWaypointManager().updatePlayer(player);
            }
        }
    }

    @Overwrite
    public boolean hasEntityWithId(int id) {
        return this.chunkMap.hasEntityWithId(id);
    }

    @Overwrite
    public void removeEntity(Entity entity) {
        this.chunkMap.removeEntity(entity);
    }

    @Overwrite
    public void addEntity(Entity entity) {
        this.chunkMap.addEntity(entity);
    }

    @Overwrite
    public void sendToTrackingPlayersAndSelf(Entity entity, Packet<? super ClientGamePacketListener> packet) {
        this.chunkMap.sendToTrackingPlayersAndSelf(entity, packet);
    }

    @Overwrite
    public void sendToTrackingPlayers(Entity entity, Packet<? super ClientGamePacketListener> packet) {
        this.chunkMap.sendToTrackingPlayers(entity, packet);
    }

    @Overwrite
    public void setViewDistance(int newDistance) {
        this.chunkMap.setServerViewDistance(newDistance);
    }

    @Overwrite
    public void setSimulationDistance(int simulationDistance) {
        this.distanceManager.updateSimulationDistance(simulationDistance);
    }

    @Overwrite
    public void setSpawnSettings(boolean spawnEnemies) {
        this.spawnEnemies = spawnEnemies;
    }

    @Overwrite
    public String getChunkDebugData(ChunkPos pos) {
        return this.chunkMap.getChunkDebugData(pos);
    }

    @Overwrite
    public SavedDataStorage getDataStorage() {
        return this.savedDataStorage;
    }

    @Overwrite
    public PoiManager getPoiManager() {
        return this.chunkMap.getPoiManager();
    }

    @Overwrite
    public ChunkScanAccess chunkScanner() {
        return this.chunkMap.chunkScanner();
    }

    @Overwrite
    @VisibleForDebug
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable NaturalSpawner.SpawnState getLastSpawnState() {
        return this.lastSpawnState;
    }

    @Overwrite
    public void deactivateTicketsOnClosing() {
        this.ticketStorage.deactivateTicketsOnClosing();
    }

    @Overwrite
    public void onChunkReadyToSend(ChunkHolder chunk) {
        if (chunk.hasChangesToBroadcast()) {
            this.chunkHoldersToBroadcast.add(chunk);
        }
    }
}


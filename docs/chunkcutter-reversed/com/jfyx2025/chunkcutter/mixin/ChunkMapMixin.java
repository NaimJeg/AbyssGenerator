/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongListIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtException
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket
 *  net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ChunkGenerationTask
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkHolder$PlayerProvider
 *  net.minecraft.server.level.ChunkLevel
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkMap$DistanceManager
 *  net.minecraft.server.level.ChunkMap$TrackedEntity
 *  net.minecraft.server.level.ChunkResult
 *  net.minecraft.server.level.ChunkTaskDispatcher
 *  net.minecraft.server.level.ChunkTaskPriorityQueue
 *  net.minecraft.server.level.ChunkTrackingView
 *  net.minecraft.server.level.ChunkTrackingView$Positioned
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.GeneratingChunkMap
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.server.level.PlayerMap
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.ThreadedLevelLightEngine
 *  net.minecraft.util.CsvOutput
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StaticCache2D
 *  net.minecraft.util.TriState
 *  net.minecraft.util.Util
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ai.village.poi.PoiManager
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragonPart
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.TicketStorage
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.ChunkGeneratorStructureState
 *  net.minecraft.world.level.chunk.ImposterProtoChunk
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.ProtoChunk
 *  net.minecraft.world.level.chunk.UpgradeData
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.chunk.status.ChunkStep
 *  net.minecraft.world.level.chunk.status.ChunkType
 *  net.minecraft.world.level.chunk.status.WorldGenContext
 *  net.minecraft.world.level.chunk.storage.SerializableChunkData
 *  net.minecraft.world.level.chunk.storage.SimpleRegionStorage
 *  net.minecraft.world.level.entity.ChunkStatusUpdateListener
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.gamerules.GameRules
 *  net.minecraft.world.level.levelgen.RandomState
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CloHolderSource;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeHolder;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.cubic.SectionGenerationQueue;
import com.jfyx2025.chunkcutter.cubic.SectionLoadMetrics;
import com.jfyx2025.chunkcutter.cubic.SingleSectionHeightAccessor;
import com.jfyx2025.chunkcutter.network.SectionsPayload;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ChunkTaskDispatcher;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.GeneratingChunkMap;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.PlayerMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkMap.class})
public abstract class ChunkMapMixin
implements CubeSource,
CloHolderSource {
    private CubeMap chunkcutter$cubes = new CubeMap();
    @Shadow
    private static ChunkResult<List<ChunkAccess>> UNLOADED_CHUNK_LIST_RESULT;
    @Shadow
    private static CompletableFuture<ChunkResult<List<ChunkAccess>>> UNLOADED_CHUNK_LIST_FUTURE;
    @Shadow
    private static byte CHUNK_TYPE_REPLACEABLE;
    @Shadow
    private static byte CHUNK_TYPE_UNKNOWN;
    @Shadow
    private static byte CHUNK_TYPE_FULL;
    @Shadow
    private static int CHUNK_SAVED_PER_TICK;
    @Shadow
    private static int CHUNK_SAVED_EAGERLY_PER_TICK;
    @Shadow
    private static int EAGER_CHUNK_SAVE_COOLDOWN_IN_MILLIS;
    @Shadow
    private static int MAX_ACTIVE_CHUNK_WRITES;
    @Shadow
    public static int MIN_VIEW_DISTANCE;
    @Shadow
    public static int MAX_VIEW_DISTANCE;
    @Shadow
    public static int FORCED_TICKET_LEVEL;
    @Shadow
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap;
    @Shadow
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;
    @Shadow
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads;
    @Shadow
    private List<ChunkGenerationTask> pendingGenerationTasks;
    @Shadow
    private ServerLevel level;
    @Shadow
    private ThreadedLevelLightEngine lightEngine;
    @Shadow
    private BlockableEventLoop<Runnable> mainThreadExecutor;
    @Shadow
    private RandomState randomState;
    @Shadow
    private ChunkGeneratorStructureState chunkGeneratorState;
    @Shadow
    private TicketStorage ticketStorage;
    @Shadow
    private PoiManager poiManager;
    @Shadow
    private LongSet toDrop;
    @Shadow
    private boolean modified;
    @Shadow
    private ChunkTaskDispatcher worldgenTaskDispatcher;
    @Shadow
    private ChunkTaskDispatcher lightTaskDispatcher;
    @Shadow
    private ChunkStatusUpdateListener chunkStatusListener;
    @Shadow
    private ChunkMap.DistanceManager distanceManager;
    @Shadow
    private String storageName;
    @Shadow
    private PlayerMap playerMap;
    @Shadow
    private Int2ObjectMap<ChunkMap.TrackedEntity> entityMap;
    @Shadow
    private Long2ByteMap chunkTypeCache;
    @Shadow
    private Long2LongMap nextChunkSaveTime;
    @Shadow
    private LongSet chunksToEagerlySave;
    @Shadow
    private Queue<Runnable> unloadQueue;
    @Shadow
    private AtomicInteger activeChunkWrites;
    @Shadow
    private int serverViewDistance;
    @Shadow
    private WorldGenContext worldGenContext;
    private Long2ObjectMap<ChunkHolder> chunkcutter$cubeHolders;
    private static final Map<Integer, List<int[]>> chunkcutter$sweepColumns;

    @Override
    public CubeMap chunkcutter$cubeMap() {
        CubeMap map = this.chunkcutter$cubes;
        if (map == null) {
            this.chunkcutter$cubes = map = new CubeMap();
        }
        return map;
    }

    @Overwrite
    public void setChunkUnsaved(ChunkPos chunkPos) {
        this.chunksToEagerlySave.add(chunkPos.pack());
    }

    @Overwrite
    public ChunkGenerator generator() {
        return this.worldGenContext.generator();
    }

    @Overwrite
    public ChunkGeneratorStructureState generatorState() {
        return this.chunkGeneratorState;
    }

    @Overwrite
    public RandomState randomState() {
        return this.randomState;
    }

    @Overwrite
    public boolean isChunkTracked(ServerPlayer player, int chunkX, int chunkZ) {
        return player.getChunkTrackingView().contains(chunkX, chunkZ) && !player.connection.chunkSender.isPending(ChunkPos.pack((int)chunkX, (int)chunkZ));
    }

    @Overwrite
    public boolean isChunkOnTrackedBorder(ServerPlayer player, int chunkX, int chunkZ) {
        if (!this.isChunkTracked(player, chunkX, chunkZ)) {
            return false;
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0 || this.isChunkTracked(player, chunkX + dx, chunkZ + dz)) continue;
                return true;
            }
        }
        return false;
    }

    @Overwrite
    public ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Overwrite
    public @Nullable ChunkHolder getUpdatingChunkIfPresent(long key) {
        return (ChunkHolder)this.updatingChunkMap.get(key);
    }

    @Override
    public @Nullable ChunkHolder chunkcutter$getHolder(CloPos pos) {
        return (ChunkHolder)this.updatingChunkMap.get(pos.asLong());
    }

    private Long2ObjectMap<ChunkHolder> chunkcutter$cubeHolders() {
        if (this.chunkcutter$cubeHolders == null) {
            this.chunkcutter$cubeHolders = new Long2ObjectOpenHashMap();
        }
        return this.chunkcutter$cubeHolders;
    }

    @Override
    public ChunkHolder chunkcutter$getOrCreateCubeHolder(CloPos pos) {
        long key = pos.asLong();
        ChunkHolder existing = (ChunkHolder)this.chunkcutter$cubeHolders().get(key);
        if (existing != null) {
            return existing;
        }
        ChunkHolder holder = new ChunkHolder(new ChunkPos(pos.x(), pos.z()), 33, (LevelHeightAccessor)new SingleSectionHeightAccessor(pos.y()), (LevelLightEngine)this.lightEngine, this::onLevelChange, (ChunkHolder.PlayerProvider)this);
        ((CubeHolder)holder).chunkcutter$setCubeKey(key);
        this.chunkcutter$cubeHolders().put(key, (Object)holder);
        return holder;
    }

    @Override
    public void chunkcutter$removeCubeHolder(long key) {
        this.chunkcutter$cubeHolders().remove(key);
    }

    private void chunkcutter$removeColumnCubeHolders(int cx, int cz) {
        LongIterator it = this.chunkcutter$cubeHolders().keySet().iterator();
        while (it.hasNext()) {
            long key = it.nextLong();
            CloPos pos = CloPos.cubeOf(key);
            if (pos.x() != cx || pos.z() != cz) continue;
            it.remove();
        }
    }

    @Override
    public LevelChunk chunkcutter$getLevelCube(CloPos pos) {
        ChunkHolder holder = (ChunkHolder)this.chunkcutter$cubeHolders().get(pos.asLong());
        return holder == null ? null : holder.getTickingChunk();
    }

    @Overwrite
    public @Nullable ChunkHolder getVisibleChunkIfPresent(long key) {
        return (ChunkHolder)this.visibleChunkMap.get(key);
    }

    @Overwrite
    public @Nullable ChunkStatus getLatestStatus(long key) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
        return chunkHolder != null ? chunkHolder.getLatestStatus() : null;
    }

    @Overwrite
    public IntSupplier getChunkQueueLevel(long pos) {
        return () -> {
            ChunkHolder chunk = this.getVisibleChunkIfPresent(pos);
            return chunk == null ? ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1 : Math.min(chunk.getQueueLevel(), ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1);
        };
    }

    @Overwrite
    public String getChunkDebugData(ChunkPos pos) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(pos.pack());
        if (chunkHolder == null) {
            return "null";
        }
        String result = chunkHolder.getTicketLevel() + "\n";
        ChunkStatus status = chunkHolder.getLatestStatus();
        ChunkAccess chunk = chunkHolder.getLatestChunk();
        if (status != null) {
            result = result + "St: \u00a7" + status.getIndex() + String.valueOf(status) + "\u00a7r\n";
        }
        if (chunk != null) {
            result = result + "Ch: \u00a7" + chunk.getPersistedStatus().getIndex() + String.valueOf(chunk.getPersistedStatus()) + "\u00a7r\n";
        }
        FullChunkStatus fullStatus = chunkHolder.getFullStatus();
        result = result + "\u00a7" + fullStatus.ordinal() + String.valueOf(fullStatus);
        return result + "\u00a7r";
    }

    @Overwrite
    public CompletableFuture<ChunkResult<List<ChunkAccess>>> getChunkRangeFuture(ChunkHolder centerChunk, int range, IntFunction<ChunkStatus> distanceToStatus) {
        if (range == 0) {
            ChunkStatus status = distanceToStatus.apply(0);
            return centerChunk.scheduleChunkGenerationTask(status, (ChunkMap)this).thenApply(r -> r.map(List::of));
        }
        int chunkCount = Mth.square((int)(range * 2 + 1));
        ArrayList<CompletableFuture> deps = new ArrayList<CompletableFuture>(chunkCount);
        ChunkPos centerPos = centerChunk.getPos();
        for (int z = -range; z <= range; ++z) {
            for (int x = -range; x <= range; ++x) {
                int distance = Math.max(Math.abs(x), Math.abs(z));
                long chunkNode = ChunkPos.pack((int)(centerPos.x() + x), (int)(centerPos.z() + z));
                ChunkHolder chunk = this.getUpdatingChunkIfPresent(chunkNode);
                if (chunk == null) {
                    return UNLOADED_CHUNK_LIST_FUTURE;
                }
                ChunkStatus depStatus = distanceToStatus.apply(distance);
                deps.add(chunk.scheduleChunkGenerationTask(depStatus, (ChunkMap)this));
            }
        }
        return Util.sequence(deps).thenApply(chunkResults -> {
            ArrayList<ChunkAccess> chunks = new ArrayList<ChunkAccess>(chunkResults.size());
            for (ChunkResult chunkResult : chunkResults) {
                if (chunkResult == null) {
                    throw this.debugFuturesAndCreateReportedException(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
                }
                ChunkAccess chunkx = (ChunkAccess)chunkResult.orElse(null);
                if (chunkx == null) {
                    return UNLOADED_CHUNK_LIST_RESULT;
                }
                chunks.add(chunkx);
            }
            return ChunkResult.of(chunks);
        });
    }

    @Overwrite
    public ReportedException debugFuturesAndCreateReportedException(IllegalStateException exception, String details) {
        StringBuilder sb = new StringBuilder();
        Consumer<ChunkHolder> addToDebug = holder -> holder.getAllFutures().forEach(pair -> {
            ChunkStatus status = (ChunkStatus)pair.getFirst();
            CompletableFuture future = (CompletableFuture)pair.getSecond();
            if (future != null && future.isDone() && future.join() == null) {
                sb.append(holder.getPos()).append(" - status: ").append(status).append(" future: ").append(future).append(System.lineSeparator());
            }
        });
        sb.append("Updating:").append(System.lineSeparator());
        this.updatingChunkMap.values().forEach(addToDebug);
        sb.append("Visible:").append(System.lineSeparator());
        this.visibleChunkMap.values().forEach(addToDebug);
        CrashReport report = CrashReport.forThrowable((Throwable)exception, (String)"Chunk loading");
        CrashReportCategory category = report.addCategory("Chunk loading");
        category.setDetail("Details", (Object)details);
        category.setDetail("Futures", (Object)sb);
        return new ReportedException(report);
    }

    @Overwrite
    public CompletableFuture<ChunkResult<LevelChunk>> prepareEntityTickingChunk(ChunkHolder chunk) {
        return this.getChunkRangeFuture(chunk, 2, distance -> ChunkStatus.FULL).thenApply(chunkResult -> chunkResult.map(list -> (LevelChunk)list.get(list.size() / 2)));
    }

    @Overwrite
    public @Nullable ChunkHolder updateChunkScheduling(long node, int level, @Nullable ChunkHolder chunk, int oldLevel) {
        if (!ChunkLevel.isLoaded((int)oldLevel) && !ChunkLevel.isLoaded((int)level)) {
            return chunk;
        }
        if (chunk != null) {
            chunk.setTicketLevel(level);
        }
        if (chunk != null) {
            if (!ChunkLevel.isLoaded((int)level)) {
                this.toDrop.add(node);
            } else {
                this.toDrop.remove(node);
            }
        }
        if (ChunkLevel.isLoaded((int)level) && chunk == null) {
            chunk = (ChunkHolder)this.pendingUnloads.remove(node);
            if (chunk != null) {
                chunk.setTicketLevel(level);
            } else {
                chunk = new ChunkHolder(ChunkPos.unpack((long)node), level, (LevelHeightAccessor)this.level, (LevelLightEngine)this.lightEngine, this::onLevelChange, (ChunkHolder.PlayerProvider)this);
            }
            this.updatingChunkMap.put(node, (Object)chunk);
            this.modified = true;
        }
        return chunk;
    }

    @Overwrite
    public void onLevelChange(ChunkPos pos, IntSupplier oldLevel, int newLevel, IntConsumer setQueueLevel) {
        this.worldgenTaskDispatcher.onLevelChange(pos, oldLevel, newLevel, setQueueLevel);
        this.lightTaskDispatcher.onLevelChange(pos, oldLevel, newLevel, setQueueLevel);
    }

    @Overwrite
    public void saveAllChunks(boolean flushStorage) {
        if (flushStorage) {
            List<ChunkHolder> chunksToSave = this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).toList();
            MutableBoolean didWork = new MutableBoolean();
            do {
                didWork.setFalse();
                chunksToSave.stream().map(chunkx -> {
                    this.mainThreadExecutor.managedBlock(() -> ((ChunkHolder)chunkx).isReadyForSaving());
                    return chunkx.getLatestChunk();
                }).filter(chunkAccess -> chunkAccess instanceof ImposterProtoChunk || chunkAccess instanceof LevelChunk).filter(this::save).forEach(c -> didWork.setTrue());
            } while (didWork.isTrue());
            this.poiManager.flushAll();
            this.processUnloads(() -> true);
            ((SimpleRegionStorage)this).synchronize(true).join();
        } else {
            this.nextChunkSaveTime.clear();
            long now = Util.getMillis();
            for (ChunkHolder chunk : this.visibleChunkMap.values()) {
                this.saveChunkIfNeeded(chunk, now);
            }
        }
    }

    @Overwrite
    public void tick(BooleanSupplier haveTime) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("poi");
        this.poiManager.tick(haveTime);
        profiler.popPush("chunk_unload");
        if (!this.level.noSave()) {
            this.processUnloads(haveTime);
        }
        profiler.popPush("chunkcutter_cubes");
        this.chunkcutter$tickCubes();
        profiler.pop();
    }

    private void chunkcutter$tickCubes() {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        SectionGenerationQueue genQueue = ChunkCutterMod.sectionGenerationQueue((ResourceKey<Level>)this.level.dimension());
        genQueue.tick(this.level);
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)this.level.dimension());
        CubeMap map = this.chunkcutter$cubeMap();
        LongListIterator iterator = clm.tracker().changed().iterator();
        while (iterator.hasNext()) {
            long node = iterator.nextLong();
            iterator.remove();
            CloPos clo = CloPos.cubeOf(node);
            if (map.contains(node)) {
                CubeProvider.refreshEntityStatus(this.level, clo);
            }
            if (clm.wants(clo.x(), clo.y(), clo.z())) {
                if (map.contains(node) || CubeProvider.isPending(this.level, clo)) continue;
                CubeProvider.loadWantedColumn(this.level, clo.x(), clo.z(), this.level.palettedContainerFactory(), map);
                continue;
            }
            if (clm.wants(clo.x(), clo.y(), clo.z()) || !map.contains(node)) continue;
            this.chunkcutter$updateCubeScheduling(clo, false, false);
        }
        this.chunkcutter$sweep3D(clm, map, genQueue);
        SectionLoadMetrics.logIfDue(this.level.getGameTime(), map.size(), genQueue.size(), CubeProvider.pendingCount(), clm.tracker().changed().size());
    }

    private static List<int[]> chunkcutter$columnOrder(int radius) {
        return chunkcutter$sweepColumns.computeIfAbsent(radius, r -> {
            ArrayList<int[]> columns = new ArrayList<int[]>((r * 2 + 1) * (r * 2 + 1));
            for (int dx = -r.intValue(); dx <= r; ++dx) {
                int dz = -r.intValue();
                while (dz <= r) {
                    columns.add(new int[]{dx, dz++});
                }
            }
            columns.sort((a, b) -> {
                int db;
                int da = Math.max(Math.abs(a[0]), Math.abs(a[1]));
                if (da != (db = Math.max(Math.abs(b[0]), Math.abs(b[1])))) {
                    return Integer.compare(da, db);
                }
                int sa = a[0] * a[0] + a[1] * a[1];
                int sb = b[0] * b[0] + b[1] * b[1];
                return Integer.compare(sa, sb);
            });
            return List.copyOf(columns);
        });
    }

    private void chunkcutter$sweep3D(CubeLoadManager clm, CubeMap map, SectionGenerationQueue queue) {
        int radius = clm.viewDistance();
        if (radius <= 0 || this.level.players().isEmpty()) {
            return;
        }
        long started = System.nanoTime();
        int scanned = 0;
        int added = 0;
        List<int[]> columns = ChunkMapMixin.chunkcutter$columnOrder(radius);
        for (ServerPlayer player : this.level.players()) {
            int psx = player.getBlockX() >> 4;
            int psz = player.getBlockZ() >> 4;
            for (int[] offset : columns) {
                ++scanned;
                int cx = psx + offset[0];
                int cz = psz + offset[1];
                int bestSy = Integer.MIN_VALUE;
                for (int sy = this.level.getMaxSectionY(); sy >= this.level.getMinSectionY(); --sy) {
                    CloPos clo;
                    if (!clm.wants(cx, sy, cz) || map.contains((clo = CloPos.cube(cx, sy, cz)).asLong()) || CubeProvider.isPending(this.level, clo)) continue;
                    bestSy = sy;
                    break;
                }
                if (bestSy == Integer.MIN_VALUE) continue;
                ++added;
                queue.add(this.level, CloPos.cube(cx, bestSy, cz).asLong());
            }
        }
        SectionLoadMetrics.recordSweep(scanned, added, System.nanoTime() - started);
    }

    private double chunkcutter$distance(CloPos clo) {
        if (this.level.players().isEmpty()) {
            return 0.0;
        }
        double best = Double.MAX_VALUE;
        for (ServerPlayer player : this.level.players()) {
            double dx = (player.getBlockX() >> 4) - clo.x();
            double dy = (player.getBlockY() >> 4) - clo.y();
            double dz = (player.getBlockZ() >> 4) - clo.z();
            best = Math.min(best, Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz))));
        }
        return best;
    }

    private boolean chunkcutter$updateCubeScheduling(CloPos clo, boolean load, boolean allowGenerate) {
        CubeMap map = this.chunkcutter$cubeMap();
        PalettedContainerFactory factory = this.level.palettedContainerFactory();
        if (load) {
            long key = clo.asLong();
            if (CubeProvider.isPending(this.level, clo)) {
                return false;
            }
            CubeProvider.requestLoad(this.level, clo, factory, map);
            return false;
        }
        CubeProvider.unloadCube(this.level, clo, factory, map);
        return false;
    }

    @Overwrite
    public boolean hasWork() {
        return this.lightEngine.hasLightWork() || !this.pendingUnloads.isEmpty() || !this.updatingChunkMap.isEmpty() || this.poiManager.hasWork() || !this.toDrop.isEmpty() || !this.unloadQueue.isEmpty() || this.worldgenTaskDispatcher.hasWork() || this.lightTaskDispatcher.hasWork() || this.distanceManager.hasTickets();
    }

    @Overwrite
    public void processUnloads(BooleanSupplier haveTime) {
        Runnable unloadTask;
        LongIterator iterator = this.toDrop.iterator();
        while (iterator.hasNext()) {
            long pos = iterator.nextLong();
            ChunkHolder chunkHolder = (ChunkHolder)this.updatingChunkMap.get(pos);
            if (chunkHolder != null) {
                this.updatingChunkMap.remove(pos);
                this.pendingUnloads.put(pos, (Object)chunkHolder);
                this.modified = true;
                int ccx = CloPos.chunkOf(pos).x();
                int ccz = CloPos.chunkOf(pos).z();
                CubeProvider.unloadColumnCubes(this.level, ccx, ccz, this.level.palettedContainerFactory(), this.chunkcutter$cubeMap());
                this.chunkcutter$removeColumnCubeHolders(ccx, ccz);
                this.scheduleUnload(pos, chunkHolder);
            }
            iterator.remove();
        }
        for (int minimalNumberOfChunksToProcess = Math.max(0, this.unloadQueue.size() - 2000); (minimalNumberOfChunksToProcess > 0 || haveTime.getAsBoolean()) && (unloadTask = this.unloadQueue.poll()) != null; --minimalNumberOfChunksToProcess) {
            unloadTask.run();
        }
        this.saveChunksEagerly(haveTime);
    }

    @Overwrite
    public void saveChunksEagerly(BooleanSupplier haveTime) {
        long now = Util.getMillis();
        int eagerlySavedCount = 0;
        LongIterator iterator = this.chunksToEagerlySave.iterator();
        while (eagerlySavedCount < 20 && this.activeChunkWrites.get() < 128 && haveTime.getAsBoolean() && iterator.hasNext()) {
            ChunkAccess latestChunk;
            long chunkPos = iterator.nextLong();
            ChunkHolder chunkHolder = (ChunkHolder)this.visibleChunkMap.get(chunkPos);
            ChunkAccess chunkAccess = latestChunk = chunkHolder != null ? chunkHolder.getLatestChunk() : null;
            if (latestChunk == null || !latestChunk.isUnsaved()) {
                iterator.remove();
                continue;
            }
            if (!this.saveChunkIfNeeded(chunkHolder, now)) continue;
            ++eagerlySavedCount;
            iterator.remove();
        }
    }

    @Overwrite
    public void scheduleUnload(long pos, ChunkHolder chunkHolder) {
        CompletableFuture saveSyncFuture = chunkHolder.getSaveSyncFuture();
        ((CompletableFuture)saveSyncFuture.thenRunAsync(() -> {
            CompletableFuture currentFuture = chunkHolder.getSaveSyncFuture();
            if (currentFuture != saveSyncFuture) {
                this.scheduleUnload(pos, chunkHolder);
            } else {
                ChunkAccess chunk = chunkHolder.getLatestChunk();
                if (this.pendingUnloads.remove(pos, (Object)chunkHolder) && chunk != null) {
                    LevelChunk levelChunk;
                    if (chunk instanceof LevelChunk) {
                        levelChunk = (LevelChunk)chunk;
                        levelChunk.setLoaded(false);
                    }
                    this.save(chunk);
                    if (chunk instanceof LevelChunk) {
                        levelChunk = (LevelChunk)chunk;
                        this.level.unload(levelChunk);
                    }
                    this.lightEngine.updateChunkStatus(chunk.getPos());
                    this.lightEngine.tryScheduleUpdate();
                    this.nextChunkSaveTime.remove(chunk.getPos().pack());
                }
            }
        }, this.unloadQueue::add)).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                // empty if block
            }
        });
    }

    @Overwrite
    public boolean promoteChunkMap() {
        if (!this.modified) {
            return false;
        }
        this.visibleChunkMap = this.updatingChunkMap.clone();
        this.modified = false;
        return true;
    }

    @Overwrite
    public CompletableFuture<ChunkAccess> scheduleChunkLoad(ChunkPos pos) {
        CompletionStage chunkDataFuture = this.readChunk(pos).thenApplyAsync(chunkData -> chunkData.map(tag -> {
            SerializableChunkData parsedData = SerializableChunkData.parse((LevelHeightAccessor)this.level, (PalettedContainerFactory)this.level.palettedContainerFactory(), (CompoundTag)tag);
            if (parsedData == null) {
                // empty if block
            }
            return parsedData;
        }), Util.backgroundExecutor().forName("parseChunk"));
        CompletableFuture poiFuture = this.poiManager.prefetch(pos);
        return ((CompletableFuture)((CompletableFuture)((CompletableFuture)chunkDataFuture).thenCombine((CompletionStage)poiFuture, (chunkData, ignored) -> chunkData)).thenApplyAsync(chunkData -> {
            Profiler.get().incrementCounter("chunkLoad");
            if (chunkData.isPresent()) {
                ProtoChunk chunk = ((SerializableChunkData)chunkData.get()).read(this.level, this.poiManager, ((SimpleRegionStorage)this).storageInfo(), pos);
                this.markPosition(pos, chunk.getPersistedStatus().getChunkType());
                return chunk;
            }
            return this.createEmptyChunk(pos);
        }, (Executor)this.mainThreadExecutor)).exceptionallyAsync(throwable -> this.handleChunkLoadFailure((Throwable)throwable, pos), (Executor)this.mainThreadExecutor);
    }

    @Overwrite
    public ChunkAccess handleChunkLoadFailure(Throwable throwable, ChunkPos pos) {
        boolean ioException;
        Throwable throwable2;
        Throwable unwrapped;
        if (throwable instanceof CompletionException) {
            CompletionException e = (CompletionException)throwable;
            v0 = e.getCause();
        } else {
            v0 = unwrapped = throwable;
        }
        if (unwrapped instanceof ReportedException) {
            ReportedException e = (ReportedException)unwrapped;
            throwable2 = e.getCause();
        } else {
            throwable2 = unwrapped;
        }
        Throwable cause = throwable2;
        boolean alwaysThrow = cause instanceof Error;
        boolean bl = ioException = cause instanceof IOException || cause instanceof NbtException;
        if (!alwaysThrow) {
            if (!ioException) {
                // empty if block
            }
            this.level.getServer().reportChunkLoadFailure(cause, ((SimpleRegionStorage)this).storageInfo(), pos);
            return this.createEmptyChunk(pos);
        }
        CrashReport report = CrashReport.forThrowable((Throwable)throwable, (String)"Exception loading chunk");
        CrashReportCategory chunkBeingLoaded = report.addCategory("Chunk being loaded");
        chunkBeingLoaded.setDetail("pos", (Object)pos);
        this.markPositionReplaceable(pos);
        throw new ReportedException(report);
    }

    @Overwrite
    public ChunkAccess createEmptyChunk(ChunkPos pos) {
        this.markPositionReplaceable(pos);
        return new ProtoChunk(pos, UpgradeData.EMPTY, (LevelHeightAccessor)this.level, this.level.palettedContainerFactory(), null);
    }

    @Overwrite
    public void markPositionReplaceable(ChunkPos pos) {
        this.chunkTypeCache.put(pos.pack(), (byte)-1);
    }

    @Overwrite
    public byte markPosition(ChunkPos pos, ChunkType type) {
        return this.chunkTypeCache.put(pos.pack(), (byte)(type == ChunkType.PROTOCHUNK ? -1 : 1));
    }

    @Overwrite
    public GenerationChunkHolder acquireGeneration(long chunkNode) {
        ChunkHolder chunkHolder = (ChunkHolder)this.updatingChunkMap.get(chunkNode);
        chunkHolder.increaseGenerationRefCount();
        return chunkHolder;
    }

    @Overwrite
    public void releaseGeneration(GenerationChunkHolder chunkHolder) {
        chunkHolder.decreaseGenerationRefCount();
    }

    @Overwrite
    public CompletableFuture<ChunkAccess> applyStep(GenerationChunkHolder chunkHolder, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache) {
        ChunkPos pos = chunkHolder.getPos();
        if (step.targetStatus() == ChunkStatus.EMPTY) {
            return this.scheduleChunkLoad(pos);
        }
        try {
            GenerationChunkHolder holder = (GenerationChunkHolder)cache.get(pos.x(), pos.z());
            ChunkAccess centerChunk = holder.getChunkIfPresentUnchecked(step.targetStatus().getParent());
            if (centerChunk == null) {
                throw new IllegalStateException("Parent chunk missing");
            }
            return step.apply(this.worldGenContext, cache, centerChunk);
        }
        catch (Exception e) {
            e.getStackTrace();
            CrashReport report = CrashReport.forThrowable((Throwable)e, (String)"Exception generating new chunk");
            CrashReportCategory category = report.addCategory("Chunk to be generated");
            category.setDetail("Status being generated", () -> step.targetStatus().getName());
            category.setDetail("Location", (Object)String.format(Locale.ROOT, "%d,%d", pos.x(), pos.z()));
            category.setDetail("Position hash", (Object)ChunkPos.pack((int)pos.x(), (int)pos.z()));
            category.setDetail("Generator", (Object)this.generator());
            throw new ReportedException(report);
        }
    }

    @Overwrite
    public ChunkGenerationTask scheduleGenerationTask(ChunkStatus targetStatus, ChunkPos pos) {
        ChunkGenerationTask task = ChunkGenerationTask.create((GeneratingChunkMap)((GeneratingChunkMap)this), (ChunkStatus)targetStatus, (ChunkPos)pos);
        this.pendingGenerationTasks.add(task);
        return task;
    }

    @Overwrite
    public void runGenerationTask(ChunkGenerationTask task) {
        GenerationChunkHolder chunk = task.getCenter();
        this.worldgenTaskDispatcher.submit(() -> {
            CompletableFuture future = task.runUntilWait();
            if (future != null) {
                future.thenRun(() -> this.runGenerationTask(task));
            }
        }, chunk.getPos().pack(), () -> ((GenerationChunkHolder)chunk).getQueueLevel());
    }

    @Overwrite
    public void runGenerationTasks() {
        this.pendingGenerationTasks.forEach(this::runGenerationTask);
        this.pendingGenerationTasks.clear();
    }

    @Overwrite
    public CompletableFuture<ChunkResult<LevelChunk>> prepareTickingChunk(ChunkHolder chunk) {
        CompletableFuture<ChunkResult<List<ChunkAccess>>> future = this.getChunkRangeFuture(chunk, 1, distance -> ChunkStatus.FULL);
        return ((CompletableFuture)future.thenComposeAsync(chunkResult -> {
            List list = (List)chunkResult.orElse(null);
            if (list == null) {
                return CompletableFuture.completedFuture(chunkResult.map(l -> null));
            }
            LevelChunk levelChunk = (LevelChunk)list.get(list.size() / 2);
            return CubeProvider.adoptColumn(this.level, levelChunk).thenApply(v -> chunkResult.map(l -> levelChunk));
        }, (Executor)this.mainThreadExecutor)).thenApplyAsync(chunkResult -> {
            LevelChunk levelChunk = (LevelChunk)chunkResult.orElse(null);
            if (levelChunk == null) {
                return chunkResult;
            }
            levelChunk.postProcessGeneration(this.level);
            this.level.startTickingChunk(levelChunk);
            CompletableFuture sendSyncFuture = chunk.getSendSyncFuture();
            if (sendSyncFuture.isDone()) {
                this.onChunkReadyToSend(chunk, levelChunk);
            } else {
                sendSyncFuture.thenAcceptAsync(ignored -> this.onChunkReadyToSend(chunk, levelChunk), (Executor)this.mainThreadExecutor);
            }
            return chunkResult;
        }, (Executor)this.mainThreadExecutor);
    }

    @Overwrite
    public void onChunkReadyToSend(ChunkHolder chunkHolder, LevelChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        if (ChunkCutterConfig.isEnabled()) {
            this.chunkcutter$sendSections(chunk);
        } else {
            for (ServerPlayer player : this.playerMap.getAllPlayers()) {
                if (!player.getChunkTrackingView().contains(chunkPos)) continue;
                ChunkMapMixin.markChunkPendingToSend(player, chunk);
            }
        }
        this.level.getChunkSource().onChunkReadyToSend(chunkHolder);
        this.level.debugSynchronizers().registerChunk(chunk);
    }

    private void chunkcutter$sendSections(LevelChunk chunk) {
        ArrayList<SectionsPayload.SectionEntry> entries = new ArrayList<SectionsPayload.SectionEntry>();
        CubeMap map = this.chunkcutter$cubeMap();
        for (long key : map.keys().toLongArray()) {
            CubeChunk cube;
            CloPos clo = CloPos.cubeOf(key);
            if (clo.x() != chunk.getPos().x() || clo.z() != chunk.getPos().z() || (cube = map.get(clo)) == null || cube.section() == null || cube.section().hasOnlyAir()) continue;
            entries.add(CubeProvider.encodeSection(this.level, clo, cube.section(), cube.chunkcutter$blockEntities()));
        }
        if (entries.isEmpty()) {
            return;
        }
        Object object = this.playerMap.getAllPlayers().iterator();
        while (object.hasNext()) {
            ServerPlayer player = (ServerPlayer)object.next();
            ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)new SectionsPayload(this.level.dimension().identifier(), entries));
        }
    }

    private static boolean chunkcutter$within3D(ServerPlayer player, long cubePos, int viewDistance) {
        CloPos clo = CloPos.cubeOf(cubePos);
        int psx = SectionPos.blockToSectionCoord((int)player.getBlockX());
        int psy = SectionPos.blockToSectionCoord((int)player.getBlockY());
        int psz = SectionPos.blockToSectionCoord((int)player.getBlockZ());
        return Math.abs(clo.x() - psx) <= viewDistance && Math.abs(clo.y() - psy) <= viewDistance && Math.abs(clo.z() - psz) <= viewDistance;
    }

    @Overwrite
    public CompletableFuture<ChunkResult<LevelChunk>> prepareAccessibleChunk(ChunkHolder chunk) {
        return this.getChunkRangeFuture(chunk, 1, ChunkLevel::getStatusAroundFullChunk).thenComposeAsync(chunkResult -> {
            List list = (List)chunkResult.orElse(null);
            if (list == null) {
                return CompletableFuture.completedFuture(chunkResult.map(l -> null));
            }
            LevelChunk levelChunk = (LevelChunk)list.get(list.size() / 2);
            return CubeProvider.adoptColumn(this.level, levelChunk).thenApply(v -> chunkResult.map(l -> levelChunk));
        }, (Executor)this.mainThreadExecutor);
    }

    @Overwrite
    public Stream<ChunkHolder> allChunksWithAtLeastStatus(ChunkStatus status) {
        int level = ChunkLevel.byStatus((ChunkStatus)status);
        return this.visibleChunkMap.values().stream().filter(chunk -> chunk.getTicketLevel() <= level);
    }

    @Overwrite
    public boolean saveChunkIfNeeded(ChunkHolder chunk, long now) {
        if (chunk.wasAccessibleSinceLastSave() && chunk.isReadyForSaving()) {
            ChunkAccess chunkAccess = chunk.getLatestChunk();
            if (!(chunkAccess instanceof ImposterProtoChunk) && !(chunkAccess instanceof LevelChunk)) {
                return false;
            }
            if (!chunkAccess.isUnsaved()) {
                return false;
            }
            long chunkPos = chunkAccess.getPos().pack();
            long nextSaveTime = this.nextChunkSaveTime.getOrDefault(chunkPos, -1L);
            if (now < nextSaveTime) {
                return false;
            }
            boolean saved = this.save(chunkAccess);
            chunk.refreshAccessibility();
            if (saved) {
                this.nextChunkSaveTime.put(chunkPos, now + 10000L);
            }
            return saved;
        }
        return false;
    }

    @Overwrite
    public boolean save(ChunkAccess chunk) {
        this.poiManager.flush(chunk.getPos());
        if (!chunk.tryMarkSaved()) {
            return false;
        }
        ChunkPos pos = chunk.getPos();
        try {
            ChunkStatus status = chunk.getPersistedStatus();
            if (status.getChunkType() != ChunkType.LEVELCHUNK) {
                if (this.isExistingChunkFull(pos)) {
                    return false;
                }
                if (status == ChunkStatus.EMPTY && chunk.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                    return false;
                }
            }
            Profiler.get().incrementCounter("chunkSave");
            SerializableChunkData data = SerializableChunkData.copyOf((ServerLevel)this.level, (ChunkAccess)chunk);
            CompletableFuture<CompoundTag> encodedData = CompletableFuture.supplyAsync(() -> ((SerializableChunkData)data).write(), (Executor)Util.backgroundExecutor());
            ((SimpleRegionStorage)this).write(pos, encodedData::join).handle((ignored, throwable) -> {
                if (throwable != null) {
                    this.level.getServer().reportChunkSaveFailure(throwable, ((SimpleRegionStorage)this).storageInfo(), pos);
                }
                return null;
            });
            this.markPosition(pos, status.getChunkType());
            return true;
        }
        catch (Exception e) {
            this.level.getServer().reportChunkSaveFailure((Throwable)e, ((SimpleRegionStorage)this).storageInfo(), pos);
            return false;
        }
    }

    @Overwrite
    public boolean isExistingChunkFull(ChunkPos pos) {
        CompoundTag currentTag;
        byte cachedChunkType = this.chunkTypeCache.get(pos.pack());
        if (cachedChunkType != 0) {
            return cachedChunkType == 1;
        }
        try {
            currentTag = this.readChunk(pos).join().orElse(null);
            if (currentTag == null) {
                this.markPositionReplaceable(pos);
                return false;
            }
        }
        catch (Exception e) {
            this.markPositionReplaceable(pos);
            return false;
        }
        ChunkType chunkType = SerializableChunkData.getChunkStatusFromTag((CompoundTag)currentTag).getChunkType();
        return this.markPosition(pos, chunkType) == 1;
    }

    @Overwrite
    public void setServerViewDistance(int newViewDistance) {
        int actualNewDistance = Mth.clamp((int)newViewDistance, (int)2, (int)32);
        if (actualNewDistance != this.serverViewDistance) {
            this.serverViewDistance = actualNewDistance;
            this.distanceManager.updatePlayerTickets(this.serverViewDistance);
            for (ServerPlayer player : this.playerMap.getAllPlayers()) {
                this.updateChunkTracking(player);
            }
        }
    }

    @Overwrite
    public int getPlayerViewDistance(ServerPlayer player) {
        return Mth.clamp((int)player.requestedViewDistance(), (int)2, (int)this.serverViewDistance);
    }

    @Overwrite
    public void markChunkPendingToSend(ServerPlayer player, ChunkPos pos) {
        LevelChunk chunk = this.getChunkToSend(pos.pack());
        if (chunk != null) {
            ChunkMapMixin.markChunkPendingToSend(player, chunk);
        }
    }

    @Overwrite
    public static void markChunkPendingToSend(ServerPlayer player, LevelChunk chunk) {
        player.connection.chunkSender.markChunkPendingToSend(chunk);
    }

    @Overwrite
    public static void dropChunk(ServerPlayer player, ChunkPos pos) {
        player.connection.chunkSender.dropChunk(player, pos);
    }

    @Overwrite
    public @Nullable LevelChunk getChunkToSend(long key) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
        return chunkHolder == null ? null : chunkHolder.getChunkToSend();
    }

    @Overwrite
    public int size() {
        return this.visibleChunkMap.size();
    }

    @Overwrite
    public DistanceManager getDistanceManager() {
        return this.distanceManager;
    }

    @Overwrite
    public void dumpChunks(Writer output) throws IOException {
        CsvOutput csvOutput = CsvOutput.builder().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("block_entity_count").addColumn("ticking_ticket").addColumn("ticking_level").addColumn("block_ticks").addColumn("fluid_ticks").build(output);
        for (Long2ObjectMap.Entry entry : this.visibleChunkMap.long2ObjectEntrySet()) {
            long posKey = entry.getLongKey();
            ChunkPos pos = ChunkPos.unpack((long)posKey);
            ChunkHolder holder = (ChunkHolder)entry.getValue();
            Optional<ChunkAccess> chunk = Optional.ofNullable(holder.getLatestChunk());
            Optional<Object> fullChunk = chunk.flatMap(chunkAccess -> {
                Optional<Object> optional;
                if (chunkAccess instanceof LevelChunk) {
                    LevelChunk levelChunk = (LevelChunk)chunkAccess;
                    optional = Optional.of(levelChunk);
                } else {
                    optional = Optional.empty();
                }
                return optional;
            });
            csvOutput.writeRow(new Object[]{pos.x(), pos.z(), holder.getTicketLevel(), chunk.isPresent(), chunk.map(ChunkAccess::getPersistedStatus).orElse(null), fullChunk.map(LevelChunk::getFullStatus).orElse(null), ChunkMapMixin.printFuture(holder.getFullChunkFuture()), ChunkMapMixin.printFuture(holder.getTickingChunkFuture()), ChunkMapMixin.printFuture(holder.getEntityTickingChunkFuture()), this.ticketStorage.getTicketDebugString(posKey, false), this.anyPlayerCloseEnoughForSpawning(pos), fullChunk.map(c -> c.getBlockEntities().size()).orElse(0), this.ticketStorage.getTicketDebugString(posKey, true), this.distanceManager.getChunkLevel(posKey, true), fullChunk.map(levelChunk -> levelChunk.getBlockTicks().count()).orElse(0), fullChunk.map(levelChunk -> levelChunk.getFluidTicks().count()).orElse(0)});
        }
    }

    @Overwrite
    public static String printFuture(CompletableFuture<ChunkResult<LevelChunk>> future) {
        try {
            ChunkResult result = future.getNow(null);
            if (result != null) {
                return result.isSuccess() ? "done" : "unloaded";
            }
            return "not completed";
        }
        catch (CompletionException e) {
            return "failed " + e.getCause().getMessage();
        }
        catch (CancellationException e) {
            return "cancelled";
        }
    }

    @Overwrite
    public CompletableFuture<Optional<CompoundTag>> readChunk(ChunkPos pos) {
        return ((SimpleRegionStorage)this).read(pos).thenApplyAsync(chunkTag -> chunkTag.map(this::upgradeChunkTag), Util.backgroundExecutor().forName("upgradeChunk"));
    }

    @Overwrite
    public CompoundTag upgradeChunkTag(CompoundTag tag) {
        return ((SimpleRegionStorage)this).upgradeChunkTag(tag, -1, ChunkMapMixin.getChunkDataFixContextTag((ResourceKey<Level>)this.level.dimension(), this.generator().getTypeNameForDataFixer()), SharedConstants.getCurrentVersion().dataVersion().version());
    }

    @Overwrite
    public static CompoundTag getChunkDataFixContextTag(ResourceKey<Level> dimension, Optional<Identifier> generatorIdentifier) {
        CompoundTag contextTag = new CompoundTag();
        contextTag.putString("dimension", dimension.identifier().toString());
        generatorIdentifier.ifPresent(identifier -> contextTag.putString("generator", identifier.toString()));
        return contextTag;
    }

    @Overwrite
    public void collectSpawningChunks(List<LevelChunk> output) {
        LongIterator spawnCandidateChunks = this.distanceManager.getSpawnCandidateChunks();
        while (spawnCandidateChunks.hasNext()) {
            LevelChunk chunk;
            ChunkHolder holder = (ChunkHolder)this.visibleChunkMap.get(spawnCandidateChunks.nextLong());
            if (holder == null || (chunk = holder.getTickingChunk()) == null || !this.anyPlayerCloseEnoughForSpawningInternal(holder.getPos())) continue;
            output.add(chunk);
        }
    }

    @Overwrite
    public void forEachBlockTickingChunk(Consumer<LevelChunk> tickingChunkConsumer) {
        this.distanceManager.forEachEntityTickingChunk(chunkPos -> {
            LevelChunk chunk;
            ChunkHolder holder = (ChunkHolder)this.visibleChunkMap.get(chunkPos);
            if (holder != null && (chunk = holder.getTickingChunk()) != null) {
                tickingChunkConsumer.accept(chunk);
            }
        });
    }

    @Overwrite
    public boolean anyPlayerCloseEnoughForSpawning(ChunkPos pos) {
        TriState triState = this.distanceManager.hasPlayersNearby(pos.pack());
        return triState == TriState.DEFAULT ? this.anyPlayerCloseEnoughForSpawningInternal(pos) : triState.toBoolean(true);
    }

    @Overwrite
    public boolean anyPlayerCloseEnoughTo(BlockPos pos, int maxDistance) {
        Vec3 target = new Vec3((Vec3i)pos);
        for (ServerPlayer player : this.playerMap.getAllPlayers()) {
            if (!this.playerIsCloseEnoughTo(player, target, maxDistance)) continue;
            return true;
        }
        return false;
    }

    @Overwrite
    public boolean anyPlayerCloseEnoughForSpawningInternal(ChunkPos pos) {
        for (ServerPlayer player : this.playerMap.getAllPlayers()) {
            if (!this.playerIsCloseEnoughForSpawning(player, pos)) continue;
            return true;
        }
        return false;
    }

    @Overwrite
    public List<ServerPlayer> getPlayersCloseForSpawning(ChunkPos pos) {
        long key = pos.pack();
        if (!this.distanceManager.hasPlayersNearby(key).toBoolean(true)) {
            return List.of();
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (ServerPlayer player : this.playerMap.getAllPlayers()) {
            if (!this.playerIsCloseEnoughForSpawning(player, pos)) continue;
            builder.add((Object)player);
        }
        return builder.build();
    }

    @Overwrite
    public boolean playerIsCloseEnoughForSpawning(ServerPlayer player, ChunkPos pos) {
        if (player.isSpectator()) {
            return false;
        }
        double distanceToChunk = ChunkMapMixin.euclideanDistanceSquared(pos, player.position());
        return distanceToChunk < 16384.0;
    }

    @Overwrite
    public boolean playerIsCloseEnoughTo(ServerPlayer player, Vec3 pos, int maxDistance) {
        double dz;
        double dy;
        if (player.isSpectator()) {
            return false;
        }
        double dx = Math.abs(player.getX() - pos.x);
        return Math.max(dx, Math.max(dy = Math.abs(player.getY() - pos.y), dz = Math.abs(player.getZ() - pos.z))) < (double)maxDistance;
    }

    @Overwrite
    public static double euclideanDistanceSquared(ChunkPos chunkPos, Vec3 pos) {
        double xPos = SectionPos.sectionToBlockCoord((int)chunkPos.x(), (int)8);
        double zPos = SectionPos.sectionToBlockCoord((int)chunkPos.z(), (int)8);
        double xd = Math.abs(xPos - pos.x);
        double zd = Math.abs(zPos - pos.z);
        double max = Math.max(xd, zd);
        return max * max;
    }

    @Overwrite
    public boolean skipPlayer(ServerPlayer player) {
        return player.isSpectator() && (Boolean)this.level.getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS) == false;
    }

    @Overwrite
    public void updatePlayerStatus(ServerPlayer player, boolean added) {
        boolean ignored = this.skipPlayer(player);
        boolean wasIgnored = this.playerMap.ignoredOrUnknown(player);
        if (added) {
            this.playerMap.addPlayer(player, ignored);
            this.updatePlayerPos(player);
            if (!ignored) {
                this.distanceManager.addPlayer(SectionPos.of((EntityAccess)player), player);
            }
            player.setChunkTrackingView(ChunkTrackingView.EMPTY);
            this.updateChunkTracking(player);
        } else {
            SectionPos lastPos = player.getLastSectionPos();
            this.playerMap.removePlayer(player);
            if (!wasIgnored) {
                this.distanceManager.removePlayer(lastPos, player);
            }
            this.applyChunkTrackingView(player, ChunkTrackingView.EMPTY);
        }
    }

    @Overwrite
    public void updatePlayerPos(ServerPlayer player) {
        SectionPos pos = SectionPos.of((EntityAccess)player);
        player.setLastSectionPos(pos);
    }

    @Overwrite
    public void move(ServerPlayer player) {
        boolean positionChanged;
        for (ChunkMap.TrackedEntity trackedEntity : this.entityMap.values()) {
            if (trackedEntity.entity == player) {
                trackedEntity.updatePlayers(this.level.players());
                continue;
            }
            trackedEntity.updatePlayer(player);
        }
        SectionPos oldSection = player.getLastSectionPos();
        SectionPos newSection = SectionPos.of((EntityAccess)player);
        boolean wasIgnored = this.playerMap.ignored(player);
        boolean ignored = this.skipPlayer(player);
        boolean bl = positionChanged = oldSection.asLong() != newSection.asLong();
        if (positionChanged || wasIgnored != ignored) {
            this.updatePlayerPos(player);
            if (!wasIgnored) {
                this.distanceManager.removePlayer(oldSection, player);
            }
            if (!ignored) {
                this.distanceManager.addPlayer(newSection, player);
            }
            if (!wasIgnored && ignored) {
                this.playerMap.ignorePlayer(player);
            }
            if (wasIgnored && !ignored) {
                this.playerMap.unIgnorePlayer(player);
            }
            this.updateChunkTracking(player);
        }
    }

    @Overwrite
    public void updateChunkTracking(ServerPlayer player) {
        ChunkTrackingView.Positioned view;
        ChunkPos chunkPos = player.chunkPosition();
        int playerViewDistance = this.getPlayerViewDistance(player);
        ChunkTrackingView chunkTrackingView = player.getChunkTrackingView();
        if (!(chunkTrackingView instanceof ChunkTrackingView.Positioned) || !(view = (ChunkTrackingView.Positioned)chunkTrackingView).center().equals((Object)chunkPos) || view.viewDistance() != playerViewDistance) {
            this.applyChunkTrackingView(player, ChunkTrackingView.of((ChunkPos)chunkPos, (int)playerViewDistance));
        }
    }

    @Overwrite
    public void applyChunkTrackingView(ServerPlayer player, ChunkTrackingView next) {
        if (player.level() == this.level) {
            ChunkTrackingView previous = player.getChunkTrackingView();
            if (next instanceof ChunkTrackingView.Positioned) {
                ChunkTrackingView.Positioned from;
                ChunkTrackingView.Positioned to = (ChunkTrackingView.Positioned)next;
                if (!(previous instanceof ChunkTrackingView.Positioned) || !(from = (ChunkTrackingView.Positioned)previous).center().equals((Object)to.center())) {
                    player.connection.send((Packet)new ClientboundSetChunkCacheCenterPacket(to.center().x(), to.center().z()));
                }
            }
            ChunkTrackingView.difference((ChunkTrackingView)previous, (ChunkTrackingView)next, pos -> this.markChunkPendingToSend(player, (ChunkPos)pos), pos -> ChunkMapMixin.dropChunk(player, pos));
            player.setChunkTrackingView(next);
        }
    }

    @Overwrite
    public List<ServerPlayer> getPlayers(ChunkPos pos, boolean borderOnly) {
        Set allPlayers = this.playerMap.getAllPlayers();
        ImmutableList.Builder result = ImmutableList.builder();
        for (ServerPlayer player : allPlayers) {
            if ((!borderOnly || !this.isChunkOnTrackedBorder(player, pos.x(), pos.z())) && (borderOnly || !this.isChunkTracked(player, pos.x(), pos.z()))) continue;
            result.add((Object)player);
        }
        return result.build();
    }

    @Overwrite
    public boolean hasEntityWithId(int id) {
        return this.entityMap.containsKey(id);
    }

    @Overwrite
    public void addEntity(Entity entity) {
        EntityType type;
        int range;
        if (!(entity instanceof EnderDragonPart) && (range = (type = entity.getType()).clientTrackingRange() * 16) != 0) {
            int updateInterval = type.updateInterval();
            if (this.entityMap.containsKey(entity.getId())) {
                throw (IllegalStateException)Util.pauseInIde((Throwable)new IllegalStateException("Entity is already tracked!"));
            }
            ChunkMap chunkMap = (ChunkMap)this;
            Objects.requireNonNull(chunkMap);
            ChunkMap.TrackedEntity trackedEntity = new ChunkMap.TrackedEntity(chunkMap, entity, range, updateInterval, type.trackDeltas());
            this.entityMap.put(entity.getId(), (Object)trackedEntity);
            trackedEntity.updatePlayers(this.level.players());
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)entity;
                this.updatePlayerStatus(player, true);
                for (ChunkMap.TrackedEntity e : this.entityMap.values()) {
                    if (e.entity == player) continue;
                    e.updatePlayer(player);
                }
            }
        }
    }

    @Overwrite
    public void removeEntity(Entity entity) {
        ChunkMap.TrackedEntity trackedEntity;
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)entity;
            this.updatePlayerStatus(player, false);
            for (ChunkMap.TrackedEntity trackedEntity2 : this.entityMap.values()) {
                trackedEntity2.removePlayer(player);
            }
        }
        if ((trackedEntity = (ChunkMap.TrackedEntity)this.entityMap.remove(entity.getId())) != null) {
            trackedEntity.broadcastRemoved();
        }
    }

    @Overwrite
    public void tick() {
        for (ServerPlayer player : this.playerMap.getAllPlayers()) {
            this.updateChunkTracking(player);
        }
        ArrayList movedPlayers = Lists.newArrayList();
        List players = this.level.players();
        for (ChunkMap.TrackedEntity trackedEntity : this.entityMap.values()) {
            boolean sectionPosChanged;
            SectionPos oldPos = trackedEntity.lastSectionPos;
            SectionPos newPos = SectionPos.of((EntityAccess)trackedEntity.entity);
            boolean bl = sectionPosChanged = !Objects.equals(oldPos, newPos);
            if (sectionPosChanged) {
                trackedEntity.updatePlayers(players);
                Entity entity = trackedEntity.entity;
                if (entity instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)entity;
                    movedPlayers.add(serverPlayer);
                }
                trackedEntity.lastSectionPos = newPos;
            }
            if (!sectionPosChanged && !trackedEntity.entity.needsSync && !this.distanceManager.inEntityTickingRange(newPos.chunk().pack())) continue;
            trackedEntity.serverEntity.sendChanges();
        }
        if (!movedPlayers.isEmpty()) {
            for (ChunkMap.TrackedEntity trackedEntity : this.entityMap.values()) {
                trackedEntity.updatePlayers((List)movedPlayers);
            }
        }
    }

    @Overwrite
    public void sendToTrackingPlayers(Entity entity, Packet<? super ClientGamePacketListener> packet) {
        ChunkMap.TrackedEntity trackedEntity = (ChunkMap.TrackedEntity)this.entityMap.get(entity.getId());
        if (trackedEntity != null) {
            trackedEntity.sendToTrackingPlayers(packet);
        }
    }

    @Overwrite
    public void sendToTrackingPlayersFiltered(Entity entity, Packet<? super ClientGamePacketListener> packet, Predicate<ServerPlayer> targetPredicate) {
        ChunkMap.TrackedEntity trackedEntity = (ChunkMap.TrackedEntity)this.entityMap.get(entity.getId());
        if (trackedEntity != null) {
            trackedEntity.sendToTrackingPlayersFiltered(packet, targetPredicate);
        }
    }

    @Overwrite
    public void sendToTrackingPlayersAndSelf(Entity entity, Packet<? super ClientGamePacketListener> packet) {
        ChunkMap.TrackedEntity trackedEntity = (ChunkMap.TrackedEntity)this.entityMap.get(entity.getId());
        if (trackedEntity != null) {
            trackedEntity.sendToTrackingPlayersAndSelf(packet);
        }
    }

    @Overwrite
    public boolean isTrackedByAnyPlayer(Entity entity) {
        ChunkMap.TrackedEntity trackedEntity = (ChunkMap.TrackedEntity)this.entityMap.get(entity.getId());
        return trackedEntity != null ? !trackedEntity.seenBy.isEmpty() : false;
    }

    @Overwrite
    public void forEachEntityTrackedBy(ServerPlayer player, Consumer<Entity> consumer) {
        for (ChunkMap.TrackedEntity entity : this.entityMap.values()) {
            if (!entity.seenBy.contains(player.connection)) continue;
            consumer.accept(entity.entity);
        }
    }

    @Overwrite
    public void resendBiomesForChunks(List<ChunkAccess> chunks) {
        HashMap<ServerPlayer, List> chunksForPlayers = new HashMap<ServerPlayer, List>();
        for (ChunkAccess chunkAccess : chunks) {
            LevelChunk levelChunk;
            ChunkPos pos = chunkAccess.getPos();
            LevelChunk chunk = chunkAccess instanceof LevelChunk ? (levelChunk = (LevelChunk)chunkAccess) : this.level.getChunk(pos.x(), pos.z());
            for (ServerPlayer player : this.getPlayers(pos, false)) {
                chunksForPlayers.computeIfAbsent(player, p -> new ArrayList()).add(chunk);
            }
        }
        chunksForPlayers.forEach((playerx, chunkList) -> playerx.connection.send((Packet)ClientboundChunksBiomesPacket.forChunks((List)chunkList)));
    }

    @Overwrite
    public PoiManager getPoiManager() {
        return this.poiManager;
    }

    @Overwrite
    public String getStorageName() {
        return this.storageName;
    }

    @Overwrite
    public void onFullChunkStatusChange(ChunkPos pos, FullChunkStatus status) {
        this.chunkStatusListener.onChunkStatusChange(pos, status);
    }

    @Overwrite
    public void waitForLightBeforeSending(ChunkPos centerChunk, int chunkRadius) {
        int affectedLightChunkRadius = chunkRadius + 1;
        ChunkPos.rangeClosed((ChunkPos)centerChunk, (int)affectedLightChunkRadius).forEach(chunkPos -> {
            ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(chunkPos.pack());
            if (chunkHolder != null) {
                chunkHolder.addSendDependency(this.lightEngine.waitForPendingTasks(chunkPos.x(), chunkPos.z()));
            }
        });
    }

    @Overwrite
    public void forEachReadyToSendChunk(Consumer<LevelChunk> consumer) {
        for (ChunkHolder chunkHolder : this.visibleChunkMap.values()) {
            LevelChunk chunk = chunkHolder.getChunkToSend();
            if (chunk == null) continue;
            consumer.accept(chunk);
        }
    }

    static {
        chunkcutter$sweepColumns = new ConcurrentHashMap<Integer, List<int[]>>();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.CrashReport
 *  net.minecraft.server.level.ChunkGenerationTask
 *  net.minecraft.server.level.ChunkLevel
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkResult
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.GeneratingChunkMap
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.util.StaticCache2D
 *  net.minecraft.util.VisibleForDebug
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ImposterProtoChunk
 *  net.minecraft.world.level.chunk.ProtoChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.chunk.status.ChunkStep
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.CrashReport;
import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.GeneratingChunkMap;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={GenerationChunkHolder.class})
public abstract class GenerationChunkHolderMixin {
    @Shadow
    private static List<ChunkStatus> CHUNK_STATUSES;
    @Shadow
    private static ChunkResult<ChunkAccess> NOT_DONE_YET;
    @Shadow
    public static ChunkResult<ChunkAccess> UNLOADED_CHUNK;
    @Shadow
    public static CompletableFuture<ChunkResult<ChunkAccess>> UNLOADED_CHUNK_FUTURE;
    @Shadow
    protected ChunkPos pos;
    @Shadow
    private volatile @Nullable ChunkStatus highestAllowedStatus;
    @Shadow
    private AtomicReference<@Nullable ChunkStatus> startedWork;
    @Shadow
    private AtomicReferenceArray<@Nullable CompletableFuture<ChunkResult<ChunkAccess>>> futures;
    @Shadow
    private AtomicReference<@Nullable ChunkGenerationTask> task;
    @Shadow
    private AtomicInteger generationRefCount;
    @Shadow
    private volatile CompletableFuture<Void> generationSaveSyncFuture;

    @Overwrite
    public CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkGenerationTask(ChunkStatus status, ChunkMap scheduler) {
        if (this.isStatusDisallowed(status)) {
            return UNLOADED_CHUNK_FUTURE;
        }
        CompletableFuture<ChunkResult<ChunkAccess>> future = this.getOrCreateFuture(status);
        if (future.isDone()) {
            return future;
        }
        ChunkGenerationTask task = this.task.get();
        if (task == null || status.isAfter(task.targetStatus)) {
            this.rescheduleChunkTask(scheduler, status);
        }
        return future;
    }

    @Overwrite
    public CompletableFuture<ChunkResult<ChunkAccess>> applyStep(ChunkStep step, GeneratingChunkMap chunkMap, StaticCache2D<GenerationChunkHolder> cache) {
        if (this.isStatusDisallowed(step.targetStatus())) {
            return UNLOADED_CHUNK_FUTURE;
        }
        return this.acquireStatusBump(step.targetStatus()) ? chunkMap.applyStep((GenerationChunkHolder)this, step, cache).handle((chunk, exception) -> {
            if (exception != null) {
                CrashReport report = CrashReport.forThrowable((Throwable)exception, (String)"Exception chunk generation/loading");
                BlockableEventLoop.relayDelayCrash((CrashReport)report);
            } else {
                this.completeFuture(step.targetStatus(), (ChunkAccess)chunk);
            }
            return ChunkResult.of((Object)chunk);
        }) : this.getOrCreateFuture(step.targetStatus());
    }

    @Overwrite
    public void updateHighestAllowedStatus(ChunkMap scheduler) {
        boolean statusDropped;
        ChunkStatus newStatus;
        ChunkStatus oldStatus = this.highestAllowedStatus;
        this.highestAllowedStatus = newStatus = ChunkLevel.generationStatus((int)this.getTicketLevel());
        boolean bl = statusDropped = oldStatus != null && (newStatus == null || newStatus.isBefore(oldStatus));
        if (statusDropped) {
            this.failAndClearPendingFuturesBetween(newStatus, oldStatus);
            if (this.task.get() != null) {
                this.rescheduleChunkTask(scheduler, this.findHighestStatusWithPendingFuture(newStatus));
            }
        }
    }

    @Overwrite
    public void replaceProtoChunk(ImposterProtoChunk chunk) {
        CompletableFuture<ChunkResult> imposterFuture = CompletableFuture.completedFuture(ChunkResult.of((Object)chunk));
        for (int i = 0; i < this.futures.length() - 1; ++i) {
            CompletableFuture<ChunkResult<ChunkAccess>> future = this.futures.get(i);
            Objects.requireNonNull(future);
            ChunkAccess maybeProtoChunk = (ChunkAccess)future.getNow(NOT_DONE_YET).orElse(null);
            if (!(maybeProtoChunk instanceof ProtoChunk)) {
                throw new IllegalStateException("Trying to replace a ProtoChunk, but found " + String.valueOf(maybeProtoChunk));
            }
            if (this.futures.compareAndSet(i, future, imposterFuture)) continue;
            throw new IllegalStateException("Future changed by other thread while trying to replace it");
        }
    }

    @Overwrite
    public void removeTask(ChunkGenerationTask task) {
        this.task.compareAndSet(task, null);
    }

    @Overwrite
    public void rescheduleChunkTask(ChunkMap scheduler, @Nullable ChunkStatus status) {
        ChunkGenerationTask newTask = status != null ? scheduler.scheduleGenerationTask(status, this.getPos()) : null;
        ChunkGenerationTask oldTask = this.task.getAndSet(newTask);
        if (oldTask != null) {
            oldTask.markForCancellation();
        }
    }

    @Overwrite
    public CompletableFuture<ChunkResult<ChunkAccess>> getOrCreateFuture(ChunkStatus status) {
        if (this.isStatusDisallowed(status)) {
            return UNLOADED_CHUNK_FUTURE;
        }
        int index = status.getIndex();
        CompletableFuture<Object> future = this.futures.get(index);
        while (future == null) {
            CompletableFuture<ChunkResult<ChunkAccess>> newValue = new CompletableFuture<ChunkResult<ChunkAccess>>();
            future = this.futures.compareAndExchange(index, null, newValue);
            if (future != null) continue;
            if (this.isStatusDisallowed(status)) {
                this.failAndClearPendingFuture(index, newValue);
                return UNLOADED_CHUNK_FUTURE;
            }
            return newValue;
        }
        return future;
    }

    @Overwrite
    public void failAndClearPendingFuturesBetween(@Nullable ChunkStatus fromExclusive, ChunkStatus toInclusive) {
        int start = fromExclusive == null ? 0 : fromExclusive.getIndex() + 1;
        int end = toInclusive.getIndex();
        for (int i = start; i <= end; ++i) {
            CompletableFuture<ChunkResult<ChunkAccess>> previous = this.futures.get(i);
            if (previous == null) continue;
            this.failAndClearPendingFuture(i, previous);
        }
    }

    @Overwrite
    public void failAndClearPendingFuture(int index, CompletableFuture<ChunkResult<ChunkAccess>> previous) {
        if (previous.complete(UNLOADED_CHUNK) && !this.futures.compareAndSet(index, previous, null)) {
            throw new IllegalStateException("Nothing else should replace the future here");
        }
    }

    @Overwrite
    public void completeFuture(ChunkStatus status, ChunkAccess chunk) {
        ChunkResult result = ChunkResult.of((Object)chunk);
        int index = status.getIndex();
        while (true) {
            CompletableFuture<ChunkResult<ChunkAccess>> future;
            if ((future = this.futures.get(index)) == null) {
                if (!this.futures.compareAndSet(index, null, CompletableFuture.completedFuture(result))) continue;
                return;
            }
            if (future.complete((ChunkResult<ChunkAccess>)result)) {
                return;
            }
            if (future.getNow(NOT_DONE_YET).isSuccess()) {
                throw new IllegalStateException("Trying to complete a future but found it to be completed successfully already");
            }
            Thread.yield();
        }
    }

    @Overwrite
    public @Nullable ChunkStatus findHighestStatusWithPendingFuture(@Nullable ChunkStatus newStatus) {
        if (newStatus == null) {
            return null;
        }
        ChunkStatus highestStatus = newStatus;
        ChunkStatus alreadyStarted = this.startedWork.get();
        while (alreadyStarted == null || highestStatus.isAfter(alreadyStarted)) {
            if (this.futures.get(highestStatus.getIndex()) != null) {
                return highestStatus;
            }
            if (highestStatus == ChunkStatus.EMPTY) break;
            highestStatus = highestStatus.getParent();
        }
        return null;
    }

    @Overwrite
    public boolean acquireStatusBump(ChunkStatus status) {
        ChunkStatus parent = status == ChunkStatus.EMPTY ? null : status.getParent();
        ChunkStatus previousStarted = this.startedWork.compareAndExchange(parent, status);
        if (previousStarted == parent) {
            return true;
        }
        if (previousStarted != null && !status.isAfter(previousStarted)) {
            return false;
        }
        throw new IllegalStateException("Unexpected last startedWork status: " + String.valueOf(previousStarted) + " while trying to start: " + String.valueOf(status));
    }

    @Overwrite
    public boolean isStatusDisallowed(ChunkStatus status) {
        ChunkStatus highestAllowedStatus = this.highestAllowedStatus;
        return highestAllowedStatus == null || status.isAfter(highestAllowedStatus);
    }

    @Overwrite
    public void increaseGenerationRefCount() {
        if (this.generationRefCount.getAndIncrement() == 0) {
            this.generationSaveSyncFuture = new CompletableFuture();
            this.addSaveDependency(this.generationSaveSyncFuture);
        }
    }

    @Overwrite
    public void decreaseGenerationRefCount() {
        CompletableFuture<Void> future = this.generationSaveSyncFuture;
        int newValue = this.generationRefCount.decrementAndGet();
        if (newValue == 0) {
            future.complete(null);
        }
        if (newValue < 0) {
            throw new IllegalStateException("More releases than claims. Count: " + newValue);
        }
    }

    @Overwrite
    public @Nullable ChunkAccess getChunkIfPresentUnchecked(ChunkStatus status) {
        CompletableFuture<ChunkResult<ChunkAccess>> future = this.futures.get(status.getIndex());
        return future == null ? null : (ChunkAccess)future.getNow(NOT_DONE_YET).orElse(null);
    }

    @Overwrite
    public @Nullable ChunkAccess getChunkIfPresent(ChunkStatus status) {
        return this.isStatusDisallowed(status) ? null : this.getChunkIfPresentUnchecked(status);
    }

    @Overwrite
    public @Nullable ChunkAccess getLatestChunk() {
        ChunkStatus status = this.startedWork.get();
        if (status == null) {
            return null;
        }
        ChunkAccess chunk = this.getChunkIfPresentUnchecked(status);
        return chunk != null ? chunk : this.getChunkIfPresentUnchecked(status.getParent());
    }

    @Overwrite
    public @Nullable ChunkStatus getPersistedStatus() {
        CompletableFuture<ChunkResult<ChunkAccess>> future = this.futures.get(ChunkStatus.EMPTY.getIndex());
        ChunkAccess chunkAccess = future == null ? null : (ChunkAccess)future.getNow(NOT_DONE_YET).orElse(null);
        return chunkAccess == null ? null : chunkAccess.getPersistedStatus();
    }

    @Overwrite
    public ChunkPos getPos() {
        return this.pos;
    }

    @Overwrite
    public FullChunkStatus getFullStatus() {
        return ChunkLevel.fullStatus((int)this.getTicketLevel());
    }

    @Shadow
    public abstract void addSaveDependency(CompletableFuture<?> var1);

    @Shadow
    public abstract int getTicketLevel();

    @Shadow
    public abstract int getQueueLevel();

    @Overwrite
    @VisibleForDebug
    public List<Pair<ChunkStatus, @Nullable CompletableFuture<ChunkResult<ChunkAccess>>>> getAllFutures() {
        ArrayList<Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>> result = new ArrayList<Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>>();
        for (int i = 0; i < CHUNK_STATUSES.size(); ++i) {
            result.add((Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>)Pair.of((Object)CHUNK_STATUSES.get(i), this.futures.get(i)));
        }
        return result;
    }

    @Overwrite
    @VisibleForDebug
    public @Nullable ChunkStatus getLatestStatus() {
        ChunkStatus status = this.startedWork.get();
        if (status == null) {
            return null;
        }
        ChunkAccess chunk = this.getChunkIfPresentUnchecked(status);
        return chunk != null ? status : status.getParent();
    }
}


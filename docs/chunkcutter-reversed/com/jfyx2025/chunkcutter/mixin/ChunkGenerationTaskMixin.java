/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ChunkGenerationTask
 *  net.minecraft.server.level.ChunkResult
 *  net.minecraft.server.level.GeneratingChunkMap
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.util.StaticCache2D
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.Zone
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.status.ChunkDependencies
 *  net.minecraft.world.level.chunk.status.ChunkPyramid
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.GeneratingChunkMap;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkGenerationTask.class})
public class ChunkGenerationTaskMixin {
    @Shadow
    private GeneratingChunkMap chunkMap;
    @Shadow
    private ChunkPos pos;
    @Shadow
    private @Nullable ChunkStatus scheduledStatus;
    @Shadow
    public ChunkStatus targetStatus;
    @Shadow
    private volatile boolean markedForCancellation;
    @Shadow
    private List<CompletableFuture<ChunkResult<ChunkAccess>>> scheduledLayer;
    @Shadow
    private StaticCache2D<GenerationChunkHolder> cache;
    @Shadow
    private boolean needsGeneration;

    @Overwrite
    public @Nullable CompletableFuture<?> runUntilWait() {
        CompletableFuture<?> waitingFor;
        while ((waitingFor = this.waitForScheduledLayer()) == null) {
            if (this.markedForCancellation || this.scheduledStatus == this.targetStatus) {
                this.releaseClaim();
                return null;
            }
            this.scheduleNextLayer();
        }
        return waitingFor;
    }

    @Overwrite
    public void scheduleNextLayer() {
        ChunkStatus statusToSchedule;
        if (this.scheduledStatus == null) {
            statusToSchedule = ChunkStatus.EMPTY;
        } else if (!this.needsGeneration && this.scheduledStatus == ChunkStatus.EMPTY && !this.canLoadWithoutGeneration()) {
            this.needsGeneration = true;
            statusToSchedule = ChunkStatus.EMPTY;
        } else {
            statusToSchedule = (ChunkStatus)ChunkStatus.getStatusList().get(this.scheduledStatus.getIndex() + 1);
        }
        this.scheduleLayer(statusToSchedule, this.needsGeneration);
        this.scheduledStatus = statusToSchedule;
    }

    @Overwrite
    public void markForCancellation() {
        this.markedForCancellation = true;
    }

    @Overwrite
    public void releaseClaim() {
        GenerationChunkHolder chunkHolder = (GenerationChunkHolder)this.cache.get(this.pos.x(), this.pos.z());
        chunkHolder.removeTask((ChunkGenerationTask)this);
        this.cache.forEach(arg_0 -> ((GeneratingChunkMap)this.chunkMap).releaseGeneration(arg_0));
    }

    @Overwrite
    public boolean canLoadWithoutGeneration() {
        if (this.targetStatus == ChunkStatus.EMPTY) {
            return true;
        }
        ChunkStatus highestGeneratedStatus = ((GenerationChunkHolder)this.cache.get(this.pos.x(), this.pos.z())).getPersistedStatus();
        if (highestGeneratedStatus != null && !highestGeneratedStatus.isBefore(this.targetStatus)) {
            ChunkDependencies dependencies = ChunkPyramid.LOADING_PYRAMID.getStepTo(this.targetStatus).accumulatedDependencies();
            int range = dependencies.getRadius();
            for (int x = this.pos.x() - range; x <= this.pos.x() + range; ++x) {
                for (int z = this.pos.z() - range; z <= this.pos.z() + range; ++z) {
                    int distance = this.pos.getChessboardDistance(x, z);
                    ChunkStatus requiredStatus = dependencies.get(distance);
                    ChunkStatus persistedStatus = ((GenerationChunkHolder)this.cache.get(x, z)).getPersistedStatus();
                    if (persistedStatus != null && !persistedStatus.isBefore(requiredStatus)) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Overwrite
    public GenerationChunkHolder getCenter() {
        return (GenerationChunkHolder)this.cache.get(this.pos.x(), this.pos.z());
    }

    @Overwrite
    public void scheduleLayer(ChunkStatus status, boolean needsGeneration) {
        try (Zone zone = Profiler.get().zone("scheduleLayer");){
            zone.addText(() -> ((ChunkStatus)status).getName());
            int radius = this.getRadiusForLayer(status, needsGeneration);
            for (int x = this.pos.x() - radius; x <= this.pos.x() + radius; ++x) {
                for (int z = this.pos.z() - radius; z <= this.pos.z() + radius; ++z) {
                    GenerationChunkHolder chunkHolder = (GenerationChunkHolder)this.cache.get(x, z);
                    if (!this.markedForCancellation && this.scheduleChunkInLayer(status, needsGeneration, chunkHolder)) continue;
                    return;
                }
            }
        }
    }

    @Overwrite
    public int getRadiusForLayer(ChunkStatus status, boolean needsGeneration) {
        ChunkPyramid pyramid = needsGeneration ? ChunkPyramid.GENERATION_PYRAMID : ChunkPyramid.LOADING_PYRAMID;
        return pyramid.getStepTo(this.targetStatus).getAccumulatedRadiusOf(status);
    }

    @Overwrite
    public boolean scheduleChunkInLayer(ChunkStatus status, boolean needsGeneration, GenerationChunkHolder chunkHolder) {
        ChunkPyramid pyramid;
        ChunkStatus persistedStatus = chunkHolder.getPersistedStatus();
        boolean generate = persistedStatus != null && status.isAfter(persistedStatus);
        ChunkPyramid chunkPyramid = pyramid = generate ? ChunkPyramid.GENERATION_PYRAMID : ChunkPyramid.LOADING_PYRAMID;
        if (generate && !needsGeneration) {
            throw new IllegalStateException("Can't load chunk, but didn't expect to need to generate");
        }
        CompletableFuture future = chunkHolder.applyStep(pyramid.getStepTo(status), this.chunkMap, this.cache);
        ChunkResult now = future.getNow(null);
        if (now == null) {
            this.scheduledLayer.add(future);
            return true;
        }
        if (now.isSuccess()) {
            return true;
        }
        this.markForCancellation();
        return false;
    }

    @Overwrite
    public @Nullable CompletableFuture<?> waitForScheduledLayer() {
        while (!this.scheduledLayer.isEmpty()) {
            CompletableFuture<ChunkResult<ChunkAccess>> lastFuture = this.scheduledLayer.getLast();
            ChunkResult resultNow = lastFuture.getNow(null);
            if (resultNow == null) {
                return lastFuture;
            }
            this.scheduledLayer.removeLast();
            if (resultNow.isSuccess()) continue;
            this.markForCancellation();
        }
        return null;
    }
}


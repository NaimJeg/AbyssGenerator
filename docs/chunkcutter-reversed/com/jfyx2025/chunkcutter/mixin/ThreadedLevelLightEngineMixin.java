/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkTaskDispatcher
 *  net.minecraft.server.level.ThreadedLevelLightEngine
 *  net.minecraft.server.level.ThreadedLevelLightEngine$TaskType
 *  net.minecraft.util.Util
 *  net.minecraft.util.thread.ConsecutiveExecutor
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskDispatcher;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ThreadedLevelLightEngine.class})
public abstract class ThreadedLevelLightEngineMixin
extends LevelLightEngine
implements AutoCloseable {
    @Shadow
    public static int DEFAULT_BATCH_SIZE;
    @Shadow
    private ConsecutiveExecutor consecutiveExecutor;
    @Shadow
    private ObjectList<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> lightTasks;
    @Shadow
    private ChunkMap chunkMap;
    @Shadow
    private ChunkTaskDispatcher taskDispatcher;
    @Shadow
    private int taskPerBatch;
    @Shadow
    private AtomicBoolean scheduled;

    public ThreadedLevelLightEngineMixin() {
        super(null, false, false);
    }

    @Override
    @Overwrite
    public void close() {
    }

    @Overwrite
    public int runLightUpdates() {
        throw (UnsupportedOperationException)Util.pauseInIde((Throwable)new UnsupportedOperationException("Ran automatically on a different thread!"));
    }

    @Overwrite
    public void checkBlock(BlockPos pos) {
        BlockPos immutable = pos.immutable();
        this.addTask(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getZ()), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> super.checkBlock(immutable), () -> "checkBlock " + String.valueOf(immutable)));
    }

    @Overwrite
    public void updateChunkStatus(ChunkPos pos) {
        this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
            int sectionY;
            super.retainData(pos, false);
            super.setLightEnabled(pos, false);
            for (sectionY = this.getMinLightSection(); sectionY < this.getMaxLightSection(); ++sectionY) {
                super.queueSectionData(LightLayer.BLOCK, SectionPos.of((ChunkPos)pos, (int)sectionY), null);
                super.queueSectionData(LightLayer.SKY, SectionPos.of((ChunkPos)pos, (int)sectionY), null);
            }
            for (sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
                super.updateSectionStatus(SectionPos.of((ChunkPos)pos, (int)sectionY), true);
            }
        }, () -> "updateChunkStatus " + String.valueOf(pos) + " true"));
    }

    @Overwrite
    public void updateSectionStatus(SectionPos pos, boolean sectionEmpty) {
        this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> super.updateSectionStatus(pos, sectionEmpty), () -> "updateSectionStatus " + String.valueOf(pos) + " " + sectionEmpty));
    }

    @Overwrite
    public void propagateLightSources(ChunkPos pos) {
        this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> super.propagateLightSources(pos), () -> "propagateLight " + String.valueOf(pos)));
    }

    @Overwrite
    public void setLightEnabled(ChunkPos pos, boolean enable) {
        this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> super.setLightEnabled(pos, enable), () -> "enableLight " + String.valueOf(pos) + " " + enable));
    }

    @Overwrite
    public void queueSectionData(LightLayer layer, SectionPos pos, @Nullable DataLayer data) {
        this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> super.queueSectionData(layer, pos, data), () -> "queueData " + String.valueOf(pos)));
    }

    @Overwrite
    public void addTask(int chunkX, int chunkZ, ThreadedLevelLightEngine.TaskType type, Runnable runnable) {
        this.addTask(chunkX, chunkZ, this.chunkMap.getChunkQueueLevel(ChunkPos.pack((int)chunkX, (int)chunkZ)), type, runnable);
    }

    @Overwrite
    public void addTask(int chunkX, int chunkZ, IntSupplier level, ThreadedLevelLightEngine.TaskType type, Runnable runnable) {
        this.taskDispatcher.submit(() -> {
            this.lightTasks.add((Object)Pair.of((Object)type, (Object)runnable));
            if (this.lightTasks.size() >= 1000) {
                this.runUpdate();
            }
        }, ChunkPos.pack((int)chunkX, (int)chunkZ), level);
    }

    @Overwrite
    public void retainData(ChunkPos pos, boolean retain) {
        this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> super.retainData(pos, retain), () -> "retainData " + String.valueOf(pos)));
    }

    @Overwrite
    public CompletableFuture<ChunkAccess> initializeLight(ChunkAccess chunk, boolean lighted) {
        ChunkPos pos = chunk.getPos();
        this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
            LevelChunkSection[] sections = chunk.getSections();
            for (int sectionIndex = 0; sectionIndex < chunk.getSectionsCount(); ++sectionIndex) {
                LevelChunkSection section = sections[sectionIndex];
                if (section == null || section.hasOnlyAir()) continue;
                int sectionY = this.levelHeightAccessor.getSectionYFromSectionIndex(sectionIndex);
                super.updateSectionStatus(SectionPos.of((ChunkPos)pos, (int)sectionY), false);
            }
        }, () -> "initializeLight: " + String.valueOf(pos)));
        return CompletableFuture.supplyAsync(() -> {
            super.setLightEnabled(pos, lighted);
            super.retainData(pos, false);
            return chunk;
        }, r -> this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
    }

    @Overwrite
    public CompletableFuture<ChunkAccess> lightChunk(ChunkAccess centerChunk, boolean lighted) {
        ChunkPos pos = centerChunk.getPos();
        centerChunk.setLightCorrect(false);
        this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
            if (!lighted) {
                super.propagateLightSources(pos);
            }
            if (SharedConstants.DEBUG_VERBOSE_SERVER_EVENTS) {
                // empty if block
            }
        }, () -> "lightChunk " + String.valueOf(pos) + " " + lighted));
        return CompletableFuture.supplyAsync(() -> {
            centerChunk.setLightCorrect(true);
            return centerChunk;
        }, r -> this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
    }

    @Overwrite
    public void tryScheduleUpdate() {
        if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
            this.consecutiveExecutor.schedule(() -> {
                this.runUpdate();
                this.scheduled.set(false);
            });
        }
    }

    @Overwrite
    public void runUpdate() {
        int count;
        int totalSize = Math.min(this.lightTasks.size(), 1000);
        ObjectListIterator iterator = this.lightTasks.iterator();
        for (count = 0; iterator.hasNext() && count < totalSize; ++count) {
            Pair task = (Pair)iterator.next();
            if (task.getFirst() != ThreadedLevelLightEngine.TaskType.PRE_UPDATE) continue;
            ((Runnable)task.getSecond()).run();
        }
        iterator.back(count);
        super.runLightUpdates();
        for (int var5 = 0; iterator.hasNext() && var5 < totalSize; ++var5) {
            Pair task = (Pair)iterator.next();
            if (task.getFirst() == ThreadedLevelLightEngine.TaskType.POST_UPDATE) {
                ((Runnable)task.getSecond()).run();
            }
            iterator.remove();
        }
    }

    @Overwrite
    public CompletableFuture<?> waitForPendingTasks(int chunkX, int chunkZ) {
        return CompletableFuture.runAsync(() -> {}, r -> this.addTask(chunkX, chunkZ, ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
    }
}


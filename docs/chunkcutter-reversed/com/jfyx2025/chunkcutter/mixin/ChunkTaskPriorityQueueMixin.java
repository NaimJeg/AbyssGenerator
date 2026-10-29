/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  net.minecraft.server.level.ChunkTaskPriorityQueue
 *  net.minecraft.server.level.ChunkTaskPriorityQueue$TasksForChunk
 *  net.minecraft.world.level.ChunkPos
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.List;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkTaskPriorityQueue.class})
public abstract class ChunkTaskPriorityQueueMixin {
    @Shadow
    public static int PRIORITY_LEVEL_COUNT;
    @Shadow
    private List<Long2ObjectLinkedOpenHashMap<List<Runnable>>> queuesPerPriority;
    @Shadow
    private volatile int topPriorityQueueIndex;
    @Shadow
    private String name;

    @Overwrite
    public void resortChunkTasks(int oldPriority, ChunkPos pos, int newPriority) {
        if (oldPriority < PRIORITY_LEVEL_COUNT) {
            Long2ObjectLinkedOpenHashMap<List<Runnable>> oldQueue = this.queuesPerPriority.get(oldPriority);
            List oldTasks = (List)oldQueue.remove(pos.pack());
            if (oldPriority == this.topPriorityQueueIndex) {
                while (this.hasWork() && this.queuesPerPriority.get(this.topPriorityQueueIndex).isEmpty()) {
                    ++this.topPriorityQueueIndex;
                }
            }
            if (oldTasks != null && !oldTasks.isEmpty()) {
                ((List)this.queuesPerPriority.get(newPriority).computeIfAbsent(pos.pack(), k -> Lists.newArrayList())).addAll(oldTasks);
                this.topPriorityQueueIndex = Math.min(this.topPriorityQueueIndex, newPriority);
            }
        }
    }

    @Overwrite
    public void submit(Runnable task, long chunkPos, int level) {
        ((List)this.queuesPerPriority.get(level).computeIfAbsent(chunkPos, p -> Lists.newArrayList())).add(task);
        this.topPriorityQueueIndex = Math.min(this.topPriorityQueueIndex, level);
    }

    @Overwrite
    public void release(long pos, boolean unschedule) {
        for (Long2ObjectLinkedOpenHashMap<List<Runnable>> queue : this.queuesPerPriority) {
            List tasks = (List)queue.get(pos);
            if (tasks == null) continue;
            if (unschedule) {
                tasks.clear();
            }
            if (!tasks.isEmpty()) continue;
            queue.remove(pos);
        }
        while (this.hasWork() && this.queuesPerPriority.get(this.topPriorityQueueIndex).isEmpty()) {
            ++this.topPriorityQueueIndex;
        }
    }

    @Overwrite
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkTaskPriorityQueue.TasksForChunk pop() {
        if (!this.hasWork()) {
            return null;
        }
        int index = this.topPriorityQueueIndex;
        Long2ObjectLinkedOpenHashMap<List<Runnable>> queue = this.queuesPerPriority.get(index);
        long chunkPos = queue.firstLongKey();
        List tasks = (List)queue.removeFirst();
        while (this.hasWork() && this.queuesPerPriority.get(this.topPriorityQueueIndex).isEmpty()) {
            ++this.topPriorityQueueIndex;
        }
        return new ChunkTaskPriorityQueue.TasksForChunk(chunkPos, tasks);
    }

    @Overwrite
    public boolean hasWork() {
        return this.topPriorityQueueIndex < PRIORITY_LEVEL_COUNT;
    }

    @Overwrite
    public String toString() {
        return this.name + " " + this.topPriorityQueueIndex + "...";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.StagingBuffer
 *  com.mojang.blaze3d.vertex.StagingBuffer$Uploader
 *  com.mojang.blaze3d.vertex.TlsfAllocator$Allocation
 *  net.minecraft.CrashReport
 *  net.minecraft.TracingExecutor
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.SectionBufferBuilderPack
 *  net.minecraft.client.renderer.SectionBufferBuilderPool
 *  net.minecraft.client.renderer.chunk.ChunkSectionLayer
 *  net.minecraft.client.renderer.chunk.SectionCompiler
 *  net.minecraft.client.renderer.chunk.SectionMesh
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection$SectionTask
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection$SectionTask$SectionTaskResult
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSectionBufferSlice
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$SectionUberBuffers
 *  net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue
 *  net.minecraft.util.VisibleForDebug
 *  net.minecraft.world.phys.Vec3
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.client.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.StagingBuffer;
import com.mojang.blaze3d.vertex.TlsfAllocator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import net.minecraft.CrashReport;
import net.minecraft.TracingExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={SectionRenderDispatcher.class})
public abstract class SectionRenderDispatcherMixin {
    @Shadow
    public static int NEARBY_SECTION_DISTANCE_IN_BLOCKS;
    @Shadow
    private SectionTaskDynamicQueue queue;
    @Shadow
    private SectionBufferBuilderPack fixedBuffers;
    @Shadow
    private SectionBufferBuilderPool bufferPool;
    @Shadow
    private volatile boolean closed;
    @Shadow
    private TracingExecutor executor;
    @Shadow
    private Consumer<SectionRenderDispatcher.RenderSection> onSectionMeshUpdate;
    @Shadow
    private AtomicReference<Vec3> cameraPosition;
    @Shadow
    private volatile SectionCompiler sectionCompiler;
    @Shadow
    private StagingBuffer stagingBuffer;
    @Shadow
    private Map<ChunkSectionLayer, SectionRenderDispatcher.SectionUberBuffers> chunkUberBuffers;
    @Shadow
    private ReentrantLock copyLock;

    @Overwrite
    public void setCompiler(SectionCompiler sectionCompiler) {
        this.sectionCompiler = sectionCompiler;
    }

    @Overwrite
    public void runTask() {
        SectionRenderDispatcher.RenderSection.SectionTask task;
        if (!(this.closed || (task = this.queue.poll(this.cameraPosition.get())) == null || task.isCompleted.get() || task.isCancelled.get())) {
            try {
                SectionBufferBuilderPack buffer = Objects.requireNonNull(this.bufferPool.acquire());
                SectionRenderDispatcher.RenderSection.SectionTask.SectionTaskResult result = task.doTask(buffer);
                task.isCompleted.set(true);
                if (result == SectionRenderDispatcher.RenderSection.SectionTask.SectionTaskResult.SUCCESSFUL) {
                    buffer.clearAll();
                } else {
                    buffer.discardAll();
                }
                this.bufferPool.release(buffer);
                this.executor.execute(this::runTask);
            }
            catch (NullPointerException e) {
                this.queue.add(task);
            }
            catch (Exception e) {
                Minecraft.getInstance().delayCrash(CrashReport.forThrowable((Throwable)e, (String)"Batching sections"));
            }
        }
    }

    @Overwrite
    public void setCameraPosition(Vec3 cameraPosition) {
        this.cameraPosition.set(cameraPosition);
    }

    @Overwrite
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SectionRenderDispatcher.RenderSectionBufferSlice getRenderSectionSlice(SectionMesh sectionMesh, ChunkSectionLayer layer) {
        SectionRenderDispatcher.SectionUberBuffers uberBuffers = this.chunkUberBuffers.get(layer);
        TlsfAllocator.Allocation vertexSlice = uberBuffers.vertexBuffer.getAllocation((Object)sectionMesh);
        if (vertexSlice == null) {
            return null;
        }
        long vertexBufferOffset = vertexSlice.getOffsetFromHeap();
        TlsfAllocator.Allocation indexSlice = uberBuffers.indexBuffer.getAllocation((Object)sectionMesh);
        long indexBufferOffset = 0L;
        GpuBuffer indexBuffer = null;
        if (indexSlice != null) {
            indexBufferOffset = indexSlice.getOffsetFromHeap();
            indexBuffer = uberBuffers.indexBuffer.getGpuBuffer(indexSlice);
        }
        return new SectionRenderDispatcher.RenderSectionBufferSlice(uberBuffers.vertexBuffer.getGpuBuffer(vertexSlice), vertexBufferOffset, indexBuffer, indexBufferOffset);
    }

    @Overwrite
    public void lock() {
        this.copyLock.lock();
    }

    @Overwrite
    public void unlock() {
        this.copyLock.unlock();
    }

    @Overwrite
    public void uploadTerrainBuffersToGpu() {
        GpuDevice device = RenderSystem.getDevice();
        try (StagingBuffer.Uploader uploader = this.stagingBuffer.startUploading(device.createCommandEncoder());){
            for (SectionRenderDispatcher.SectionUberBuffers buffers : this.chunkUberBuffers.values()) {
                boolean performedBufferResize = buffers.vertexBuffer.uploadStagedAllocations(device, uploader);
                buffers.indexBuffer.uploadStagedAllocations(device, uploader);
                if (!performedBufferResize) continue;
                break;
            }
        }
    }

    @Overwrite
    public void schedule(SectionRenderDispatcher.RenderSection.SectionTask task) {
        if (!this.closed) {
            this.queue.add(task);
            this.executor.execute(this::runTask);
        }
    }

    @Overwrite
    public void clearCompileQueue() {
        this.queue.clear();
    }

    @Overwrite
    public boolean isQueueEmpty() {
        return this.queue.size() == 0;
    }

    @Overwrite
    public void dispose() {
        this.closed = true;
        this.clearCompileQueue();
        this.copyLock.lock();
        try {
            for (SectionRenderDispatcher.SectionUberBuffers buffers : this.chunkUberBuffers.values()) {
                buffers.vertexBuffer.close();
                buffers.indexBuffer.close();
            }
            this.stagingBuffer.close();
        }
        finally {
            this.copyLock.unlock();
        }
    }

    @Overwrite
    @VisibleForDebug
    public String getStats() {
        return String.format(Locale.ROOT, "pC: %03d, aB: %02d", this.queue.size(), this.bufferPool.getFreeBufferCount());
    }

    @Overwrite
    @VisibleForDebug
    public int getCompileQueueSize() {
        return this.queue.size();
    }

    @Overwrite
    @VisibleForDebug
    public int getFreeBufferCount() {
        return this.bufferPool.getFreeBufferCount();
    }
}


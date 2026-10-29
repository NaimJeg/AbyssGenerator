/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.util.StaticCache2D
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.status.ChunkStatusTasks
 *  net.minecraft.world.level.chunk.status.ChunkStep
 *  net.minecraft.world.level.chunk.status.WorldGenContext
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.mirror.net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={net.minecraft.world.level.chunk.status.ChunkStatusTasks.class})
public class ChunkStatusTasksMixin {
    @Overwrite
    public static CompletableFuture<ChunkAccess> passThrough(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.passThrough(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateStructureStarts(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateStructureStarts(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> loadStructureStarts(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.loadStructureStarts(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateStructureReferences(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateStructureReferences(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateBiomes(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateBiomes(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateNoise(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateNoise(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateSurface(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateSurface(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateCarvers(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateCarvers(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateFeatures(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateFeatures(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> initializeLight(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.initializeLight(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> light(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.light(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> generateSpawn(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.generateSpawn(context, step, chunks, chunk);
    }

    @Overwrite
    public static CompletableFuture<ChunkAccess> full(WorldGenContext context, ChunkStep step, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk) {
        return ChunkStatusTasks.full(context, step, chunks, chunk);
    }
}


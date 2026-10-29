/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.StructureManager
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
 *  net.minecraft.world.level.levelgen.NoiseChunk
 *  net.minecraft.world.level.levelgen.RandomState
 *  net.minecraft.world.level.levelgen.blending.Blender
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.jfyx2025.chunkcutter.mixin;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={NoiseBasedChunkGenerator.class})
public interface NoiseBasedChunkGeneratorAccessor {
    @Invoker(value="createNoiseChunk")
    public NoiseChunk chunkcutter$createNoiseChunk(ChunkAccess var1, StructureManager var2, Blender var3, RandomState var4);
}


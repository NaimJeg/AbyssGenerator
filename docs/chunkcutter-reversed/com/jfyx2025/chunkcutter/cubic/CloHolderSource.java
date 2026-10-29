/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public interface CloHolderSource {
    public @Nullable ChunkHolder chunkcutter$getHolder(CloPos var1);

    public ChunkHolder chunkcutter$getOrCreateCubeHolder(CloPos var1);

    public void chunkcutter$removeCubeHolder(long var1);

    public @Nullable LevelChunk chunkcutter$getLevelCube(CloPos var1);
}


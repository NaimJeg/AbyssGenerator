/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LevelChunk
 */
package com.jfyx2025.chunkcutter.client;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public interface ClientChunkSink {
    public LevelChunk chunkcutter$ensureChunk(Level var1, int var2, int var3);

    public void chunkcutter$insertChunk(int var1, int var2, LevelChunk var3);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.ProtoChunk
 *  net.minecraft.world.level.chunk.UpgradeData
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import com.jfyx2025.chunkcutter.cubic.SectionChunkFactory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;

final class NoiseTerrainChunk
extends ProtoChunk
implements SectionChunkFactory {
    private final PalettedContainerFactory factory;

    NoiseTerrainChunk(ChunkPos pos, LevelHeightAccessor bounds, PalettedContainerFactory factory) {
        super(pos, UpgradeData.EMPTY, bounds, factory, null);
        this.factory = factory;
    }

    @Override
    public PalettedContainerFactory chunkcutter$factory() {
        return this.factory;
    }
}


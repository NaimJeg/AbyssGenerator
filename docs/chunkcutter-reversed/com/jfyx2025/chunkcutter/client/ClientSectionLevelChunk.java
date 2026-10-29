/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 */
package com.jfyx2025.chunkcutter.client;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;

public final class ClientSectionLevelChunk
extends LevelChunk {
    private static volatile LevelChunkSection chunkcutter$emptyRef;
    private final Level chunkcutter$level;
    private volatile LevelChunkSection[] chunkcutter$sectionsCache;

    public ClientSectionLevelChunk(ClientLevel level, ChunkPos pos) {
        super((Level)level, pos);
        this.chunkcutter$level = level;
    }

    public void chunkcutter$invalidateSections() {
        this.chunkcutter$sectionsCache = null;
    }

    public void chunkcutter$setSection(int sectionIndex, LevelChunkSection section) {
        LevelChunkSection[] cache = this.chunkcutter$sectionsCache;
        if (cache != null && sectionIndex >= 0 && sectionIndex < cache.length) {
            cache[sectionIndex] = section != null ? section : ClientSectionLevelChunk.chunkcutter$empty(this.chunkcutter$level.palettedContainerFactory());
        }
    }

    public LevelChunkSection[] getSections() {
        LevelChunkSection[] cache = this.chunkcutter$sectionsCache;
        if (cache != null) {
            return cache;
        }
        LevelChunkSection[] arr = new LevelChunkSection[this.chunkcutter$level.getSectionsCount()];
        CubeMap map = ((CubeSource)this.chunkcutter$level.getChunkSource()).chunkcutter$cubeMap();
        int minSectionY = this.chunkcutter$level.getMinSectionY();
        LevelChunkSection empty = ClientSectionLevelChunk.chunkcutter$empty(this.chunkcutter$level.palettedContainerFactory());
        ChunkPos pos = this.getPos();
        for (int i = 0; i < arr.length; ++i) {
            CubeChunk cube = map.get(CloPos.cube(pos.x(), minSectionY + i, pos.z()));
            arr[i] = cube != null && cube.section() != null ? cube.section() : empty;
        }
        this.chunkcutter$sectionsCache = arr;
        return arr;
    }

    public LevelChunkSection getSection(int sectionIndex) {
        LevelChunkSection[] arr = this.getSections();
        return sectionIndex >= 0 && sectionIndex < arr.length ? arr[sectionIndex] : ClientSectionLevelChunk.chunkcutter$empty(this.chunkcutter$level.palettedContainerFactory());
    }

    public BlockState getBlockState(BlockPos pos) {
        CubeSource source;
        CubeChunk cube;
        ChunkSource chunkSource = this.chunkcutter$level.getChunkSource();
        if (chunkSource instanceof CubeSource && (cube = (source = (CubeSource)chunkSource).chunkcutter$cubeMap().get(CloPos.cube(this.getPos().x(), pos.getY() >> 4, this.getPos().z()))) != null && cube.section() != null) {
            return cube.section().getBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
        }
        return Blocks.AIR.defaultBlockState();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static LevelChunkSection chunkcutter$empty(PalettedContainerFactory factory) {
        LevelChunkSection empty = chunkcutter$emptyRef;
        if (empty != null) return empty;
        Class<ClientSectionLevelChunk> clazz = ClientSectionLevelChunk.class;
        synchronized (ClientSectionLevelChunk.class) {
            empty = chunkcutter$emptyRef;
            if (empty != null) return empty;
            chunkcutter$emptyRef = empty = new LevelChunkSection(factory);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return empty;
        }
    }
}


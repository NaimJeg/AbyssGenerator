/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.ProtoChunk
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import java.util.Arrays;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;

public final class SectionLevelChunk
extends LevelChunk {
    private static volatile LevelChunkSection chunkcutter$emptyRef;
    private final ServerLevel chunkcutter$level;
    private volatile LevelChunkSection[] chunkcutter$sectionsCache;

    public SectionLevelChunk(ServerLevel level, ProtoChunk proto) {
        super(level, proto, null);
        this.chunkcutter$level = level;
    }

    public void chunkcutter$invalidateSections() {
        this.chunkcutter$sectionsCache = null;
    }

    public void chunkcutter$setSection(int sectionIndex, LevelChunkSection section) {
        LevelChunkSection[] cache = this.chunkcutter$sectionsCache;
        if (cache != null && sectionIndex >= 0 && sectionIndex < cache.length) {
            cache[sectionIndex] = section != null ? section : SectionLevelChunk.chunkcutter$empty(this.chunkcutter$level.palettedContainerFactory());
        }
    }

    public LevelChunkSection[] getSections() {
        LevelChunkSection[] cache = this.chunkcutter$sectionsCache;
        if (cache != null) {
            return cache;
        }
        Object[] arr = new LevelChunkSection[this.chunkcutter$level.getSectionsCount()];
        CubeMap map = ((CubeSource)this.chunkcutter$level.getChunkSource()).chunkcutter$cubeMap();
        LevelChunkSection empty = SectionLevelChunk.chunkcutter$empty(this.chunkcutter$level.palettedContainerFactory());
        if (map == null) {
            Arrays.fill(arr, empty);
            return arr;
        }
        int minSectionY = this.chunkcutter$level.getMinSectionY();
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
        return sectionIndex >= 0 && sectionIndex < arr.length ? arr[sectionIndex] : SectionLevelChunk.chunkcutter$empty(this.chunkcutter$level.palettedContainerFactory());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static LevelChunkSection chunkcutter$empty(PalettedContainerFactory factory) {
        LevelChunkSection empty = chunkcutter$emptyRef;
        if (empty != null) return empty;
        Class<SectionLevelChunk> clazz = SectionLevelChunk.class;
        synchronized (SectionLevelChunk.class) {
            empty = chunkcutter$emptyRef;
            if (empty != null) return empty;
            chunkcutter$emptyRef = empty = new LevelChunkSection(factory);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return empty;
        }
    }
}


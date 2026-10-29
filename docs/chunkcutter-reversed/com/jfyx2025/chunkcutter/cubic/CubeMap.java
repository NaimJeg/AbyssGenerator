/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.jspecify.annotations.Nullable;

public final class CubeMap {
    private final Long2ObjectMap<CubeChunk> loaded = new Long2ObjectOpenHashMap();

    public @Nullable CubeChunk get(long key) {
        return (CubeChunk)((Object)this.loaded.get(key));
    }

    public @Nullable CubeChunk get(CloPos pos) {
        return (CubeChunk)((Object)this.loaded.get(pos.asLong()));
    }

    public void put(CubeChunk cube) {
        this.loaded.put(cube.chunkcutter$key(), (Object)cube);
    }

    public void remove(long key) {
        this.loaded.remove(key);
    }

    public boolean contains(long key) {
        return this.loaded.containsKey(key);
    }

    public int size() {
        return this.loaded.size();
    }

    public LongSet keys() {
        return this.loaded.keySet();
    }

    public void removeColumn(int cx, int cz) {
        LongIterator it = this.loaded.keySet().iterator();
        while (it.hasNext()) {
            long key = it.nextLong();
            CloPos pos = CloPos.cubeOf(key);
            if (pos.x() != cx || pos.z() != cz) continue;
            it.remove();
        }
    }
}


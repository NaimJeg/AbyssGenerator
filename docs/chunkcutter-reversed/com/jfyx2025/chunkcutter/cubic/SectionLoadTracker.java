/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.server.level.SectionTracker
 */
package com.jfyx2025.chunkcutter.cubic;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.SectionTracker;

public final class SectionLoadTracker
extends SectionTracker {
    private final Long2ByteMap levels = new Long2ByteOpenHashMap();
    private final LongSet sources = new LongOpenHashSet();
    private final LongLinkedOpenHashSet changed = new LongLinkedOpenHashSet();

    public SectionLoadTracker(int maxDistance) {
        super(maxDistance + 2, 16, 256);
        this.levels.defaultReturnValue((byte)(maxDistance + 1));
    }

    public void setSource(long node, boolean source) {
        if (source) {
            if (this.sources.add(node)) {
                this.update(node, 0, false);
            }
        } else if (this.sources.remove(node)) {
            this.update(node, this.levelCount - 1, false);
        }
    }

    protected int getLevelFromSource(long node) {
        return this.sources.contains(node) ? 0 : this.levelCount - 1;
    }

    protected int getLevel(long node) {
        return this.levels.get(node);
    }

    protected void setLevel(long node, int level) {
        if (level >= this.levelCount - 1) {
            this.levels.remove(node);
        } else {
            this.levels.put(node, (byte)level);
        }
        this.changed.add(node);
    }

    public LongLinkedOpenHashSet changed() {
        return this.changed;
    }

    public void runAllUpdates() {
        this.runUpdates(Integer.MAX_VALUE);
    }

    public int distance(long sectionNode) {
        byte level = this.levels.get(sectionNode);
        return level >= this.levelCount - 1 ? Integer.MAX_VALUE : (int)level;
    }

    public LongSet nodes() {
        return this.levels.keySet();
    }
}


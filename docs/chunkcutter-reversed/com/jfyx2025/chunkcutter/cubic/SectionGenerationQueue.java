/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.cubic.SectionLoadMetrics;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.PalettedContainerFactory;

public final class SectionGenerationQueue {
    private final PriorityQueue<Entry> queue = new PriorityQueue();
    private final LongOpenHashSet queued = new LongOpenHashSet();
    private final Map<Long, Long> addedAt = new HashMap<Long, Long>();

    private synchronized void addEntry(long node, Priority priority) {
        if (this.queued.add(node)) {
            long stamp = System.nanoTime();
            this.addedAt.put(node, stamp);
            this.queue.add(new Entry(node, priority.horizontal(), priority.vertical(), stamp));
        }
    }

    public synchronized void add(ServerLevel level, long node) {
        CubeMap map = ((CubeSource)level.getChunkSource()).chunkcutter$cubeMap();
        if (map == null || map.contains(node)) {
            return;
        }
        this.addEntry(node, SectionGenerationQueue.priority(level, CloPos.cubeOf(node)));
    }

    public synchronized void refresh(ServerLevel level) {
        if (this.queued.isEmpty()) {
            return;
        }
        ArrayList nodes = new ArrayList(this.queued);
        this.queue.clear();
        Iterator iterator = nodes.iterator();
        while (iterator.hasNext()) {
            long node = (Long)iterator.next();
            Priority p = SectionGenerationQueue.priority(level, CloPos.cubeOf(node));
            this.queue.add(new Entry(node, p.horizontal(), p.vertical(), this.addedAt.getOrDefault(node, 0L)));
        }
    }

    private static Priority priority(ServerLevel level, CloPos clo) {
        double vertical = -clo.y();
        if (level.players().isEmpty()) {
            return new Priority(0.0, vertical);
        }
        double bestHorizontal = Double.MAX_VALUE;
        for (ServerPlayer player : level.players()) {
            double dx = (player.getBlockX() >> 4) - clo.x();
            double dz = (player.getBlockZ() >> 4) - clo.z();
            bestHorizontal = Math.min(bestHorizontal, Math.max(Math.abs(dx), Math.abs(dz)));
        }
        return new Priority(bestHorizontal, vertical);
    }

    public synchronized boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public synchronized int size() {
        return this.queue.size();
    }

    public synchronized boolean contains(long node) {
        return this.queued.contains(node);
    }

    public synchronized long[] queuedNodes() {
        return this.queued.toLongArray();
    }

    public synchronized long oldestAgeMs() {
        long oldest = Long.MAX_VALUE;
        for (Entry entry : this.queue) {
            if (entry.addedAt() >= oldest) continue;
            oldest = entry.addedAt();
        }
        return oldest == Long.MAX_VALUE ? 0L : (System.nanoTime() - oldest) / 1000000L;
    }

    public synchronized long oldestNode() {
        long node = Long.MIN_VALUE;
        long oldest = Long.MAX_VALUE;
        for (Entry entry : this.queue) {
            if (entry.addedAt() >= oldest) continue;
            oldest = entry.addedAt();
            node = entry.node();
        }
        return node;
    }

    public synchronized void clear() {
        this.queue.clear();
        this.queued.clear();
        this.addedAt.clear();
    }

    public synchronized void remove(long node) {
        if (this.queued.remove(node)) {
            this.addedAt.remove(node);
            this.queue.removeIf(entry -> entry.node() == node);
        }
    }

    private void removeColumnEntries(int cx, int cz) {
        this.queue.removeIf(entry -> {
            CloPos pos = CloPos.cubeOf(entry.node());
            if (pos.x() == cx && pos.z() == cz) {
                this.queued.remove(entry.node());
                this.addedAt.remove(entry.node());
                return true;
            }
            return false;
        });
    }

    public synchronized void tick(ServerLevel level) {
        long started = System.nanoTime();
        CubeMap map = ((CubeSource)level.getChunkSource()).chunkcutter$cubeMap();
        if (map == null) {
            return;
        }
        PalettedContainerFactory factory = level.palettedContainerFactory();
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
        int columns = 0;
        int sections = 0;
        while (!this.queue.isEmpty()) {
            Entry entry = this.queue.poll();
            long node = entry.node();
            if (!this.queued.remove(node)) continue;
            this.addedAt.remove(node);
            CloPos column = CloPos.cubeOf(node);
            int cx = column.x();
            int cz = column.z();
            ArrayList<Integer> wantedY = new ArrayList<Integer>();
            for (int sy2 = level.getMinSectionY(); sy2 <= level.getMaxSectionY(); ++sy2) {
                if (!clm.wants(cx, sy2, cz)) continue;
                wantedY.add(sy2);
            }
            wantedY.sort(Comparator.comparingInt(sy -> sy).reversed());
            int processed = 0;
            Iterator iterator = wantedY.iterator();
            while (iterator.hasNext()) {
                int sy3 = (Integer)iterator.next();
                CloPos clo = CloPos.cube(cx, sy3, cz);
                long sectionNode = clo.asLong();
                if (map.contains(sectionNode) || CubeProvider.isPending(level, clo)) continue;
                CubeProvider.requestLoad(level, clo, factory, map);
                ++processed;
            }
            ++columns;
            sections += processed;
            this.removeColumnEntries(cx, cz);
        }
        SectionLoadMetrics.recordQueue(columns, sections, System.nanoTime() - started);
    }

    private record Entry(long node, double horizontal, double vertical, long addedAt) implements Comparable<Entry>
    {
        @Override
        public int compareTo(Entry other) {
            int byHorizontal = Double.compare(this.horizontal, other.horizontal);
            if (byHorizontal != 0) {
                return byHorizontal;
            }
            int byVertical = Double.compare(this.vertical, other.vertical);
            if (byVertical != 0) {
                return byVertical;
            }
            return Long.compare(this.addedAt, other.addedAt);
        }
    }

    private record Priority(double horizontal, double vertical) {
    }
}


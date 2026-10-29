/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.TicketStorage
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.SectionLoadTracker;
import com.jfyx2025.chunkcutter.cubic.TicketSource;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainSources;
import com.jfyx2025.chunkcutter.mixin.ServerChunkCacheAccessor;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public final class CubeLoadManager {
    public static final long UNLOAD_DELAY_TICKS = 20L;
    private static final int MAX_DISTANCE = 16;
    private final SectionLoadTracker tracker = new SectionLoadTracker(16);
    private final LongSet activeSources = new LongOpenHashSet();
    private final LongSet desiredSources = new LongOpenHashSet();
    private final Map<Long, Long> unloadSince = new ConcurrentHashMap<Long, Long>();
    private int viewDistance = 8;
    private int simulationDistance = 5;
    private int maxKeepSections = 10;
    private long lastSig = Long.MIN_VALUE;
    private long spawnSource = Long.MIN_VALUE;
    private final LongSet columnSources = new LongOpenHashSet();
    private final Map<Long, Integer> ticketCenterSectionY = new ConcurrentHashMap<Long, Integer>();
    private final Map<Long, Integer> ticketSectionRadius = new ConcurrentHashMap<Long, Integer>();
    private final Map<UUID, Long> playerSources = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Long> lastPlayerSections = new ConcurrentHashMap<UUID, Long>();

    public int maxKeepSections() {
        return this.maxKeepSections;
    }

    public void setSpawnSource(long node) {
        if (this.spawnSource != node) {
            this.spawnSource = node;
            this.lastSig = Long.MIN_VALUE;
        }
    }

    public void setColumnSources(LongSet keys) {
        if (!this.columnSources.equals((Object)keys)) {
            this.columnSources.clear();
            this.columnSources.addAll((LongCollection)keys);
            this.lastSig = Long.MIN_VALUE;
        }
    }

    public void setTicketCenterY(ChunkPos pos, int sectionY, int radiusSections) {
        long key = pos.pack();
        this.ticketCenterSectionY.put(key, sectionY);
        this.ticketSectionRadius.put(key, Math.max(0, radiusSections));
    }

    private boolean inTicketBand(int sectionX, int sectionY, int sectionZ) {
        long key = ChunkPos.pack((int)sectionX, (int)sectionZ);
        Integer center = this.ticketCenterSectionY.get(key);
        if (center == null) {
            return false;
        }
        int radius = this.ticketSectionRadius.getOrDefault(key, 2);
        return sectionY >= center - radius && sectionY <= center + radius;
    }

    private void syncTicketSources(ServerLevel level) {
        ServerChunkCacheAccessor acc;
        TicketStorage storage;
        LongOpenHashSet fullColumns = new LongOpenHashSet();
        LongOpenHashSet localSeen = new LongOpenHashSet();
        ServerChunkCache serverChunkCache = level.getChunkSource();
        if (serverChunkCache instanceof ServerChunkCacheAccessor && (storage = (acc = (ServerChunkCacheAccessor)serverChunkCache).chunkcutter$ticketStorage()) instanceof TicketSource) {
            TicketSource src = (TicketSource)storage;
            src.chunkcutter$forEachTicket((pos, ticket) -> {
                boolean localized;
                TicketType type = ticket.getType();
                if (!type.doesLoad() || type == TicketType.PLAYER_SPAWN || type == TicketType.PLAYER_LOADING || type == TicketType.PLAYER_SIMULATION || type == TicketType.SPAWN_SEARCH || type == TicketType.UNKNOWN) {
                    return;
                }
                long key = pos.pack();
                boolean bl = localized = (type == TicketType.PORTAL || type == TicketType.ENDER_PEARL) && this.ticketCenterSectionY.containsKey(key);
                if (localized) {
                    localSeen.add(key);
                } else {
                    fullColumns.add(key);
                }
            });
        }
        this.ticketCenterSectionY.keySet().retainAll((Collection<?>)localSeen);
        this.ticketSectionRadius.keySet().retainAll((Collection<?>)localSeen);
        this.setColumnSources((LongSet)fullColumns);
    }

    public void setPlayerSource(ServerPlayer player, SectionPos pos) {
        long node = SectionPos.asLong((int)pos.x(), (int)pos.y(), (int)pos.z());
        this.playerSources.put(player.getUUID(), node);
        if (this.activeSources.add(node)) {
            this.tracker.setSource(node, true);
        }
        this.viewDistance = Math.max(1, player.requestedViewDistance());
        this.maxKeepSections = this.viewDistance + 2;
        this.tracker.runAllUpdates();
        this.lastSig = Long.MIN_VALUE;
    }

    public void removePlayerSource(ServerPlayer player) {
        this.playerSources.remove(player.getUUID());
        this.lastPlayerSections.remove(player.getUUID());
        this.lastSig = Long.MIN_VALUE;
    }

    public SectionLoadTracker tracker() {
        return this.tracker;
    }

    public int viewDistance() {
        return this.viewDistance;
    }

    public int simulationDistance() {
        return this.simulationDistance;
    }

    public void tick(ServerLevel level) {
        int effectiveView;
        this.syncTicketSources(level);
        this.simulationDistance = Math.max(1, level.getServer().getPlayerList().getSimulationDistance());
        for (ServerPlayer player : level.players()) {
            int sx = SectionPos.blockToSectionCoord((int)player.getBlockX());
            int sy = SectionPos.blockToSectionCoord((int)player.getBlockY());
            int sz = SectionPos.blockToSectionCoord((int)player.getBlockZ());
            long node = SectionPos.asLong((int)sx, (int)sy, (int)sz);
            this.playerSources.put(player.getUUID(), node);
            Long previous = this.lastPlayerSections.put(player.getUUID(), node);
            if (previous != null && previous == node) continue;
            CubeProvider.sendLoadedSections(player);
        }
        BlockPos spawn = level.getRespawnData().pos();
        int spawnSectionY = SectionPos.blockToSectionCoord((int)spawn.getY());
        ChunkGenerator sy = level.getChunkSource().getGenerator();
        if (sy instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator noiseGen = (NoiseBasedChunkGenerator)sy;
            try {
                int surfaceY = ChunkCutterConfig.isEnabled() ? TerrainSources.forLevel(level).heightAt(spawn.getX(), spawn.getZ()) : noiseGen.getBaseHeight(spawn.getX(), spawn.getZ(), Heightmap.Types.WORLD_SURFACE_WG, (LevelHeightAccessor)level, level.getChunkSource().randomState());
                spawnSectionY = SectionPos.blockToSectionCoord((int)surfaceY);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.setSpawnSource(SectionPos.asLong((int)SectionPos.blockToSectionCoord((int)spawn.getX()), (int)spawnSectionY, (int)SectionPos.blockToSectionCoord((int)spawn.getZ())));
        long sig = this.spawnSource;
        for (ServerPlayer player : level.players()) {
            sig = sig * 31L + SectionPos.asLong((int)SectionPos.blockToSectionCoord((int)player.getBlockX()), (int)SectionPos.blockToSectionCoord((int)player.getBlockY()), (int)SectionPos.blockToSectionCoord((int)player.getBlockZ()));
            sig = sig * 31L + (long)player.requestedViewDistance();
        }
        if ((sig = sig * 31L + (long)this.simulationDistance) == this.lastSig) {
            return;
        }
        this.lastSig = sig;
        ChunkCutterMod.sectionGenerationQueue((ResourceKey<Level>)level.dimension()).clear();
        this.desiredSources.clear();
        int maxView = 0;
        for (ServerPlayer player : level.players()) {
            int view = Math.max(1, player.requestedViewDistance());
            if (view <= maxView) continue;
            maxView = view;
        }
        Iterator<Object> player = this.playerSources.values().iterator();
        while (player.hasNext()) {
            long node = (Long)player.next();
            this.desiredSources.add(node);
        }
        if (this.spawnSource != Long.MIN_VALUE && level.players().isEmpty()) {
            this.desiredSources.add(this.spawnSource);
        }
        this.viewDistance = effectiveView = maxView > 0 ? maxView : Math.max(1, this.simulationDistance);
        this.maxKeepSections = effectiveView + 2;
        LongIterator node = this.desiredSources.iterator();
        while (node.hasNext()) {
            long node2 = (Long)node.next();
            if (this.activeSources.contains(node2)) continue;
            this.tracker.setSource(node2, true);
        }
        LongIterator it = this.activeSources.iterator();
        while (it.hasNext()) {
            long node3 = it.nextLong();
            if (this.desiredSources.contains(node3)) continue;
            this.tracker.setSource(node3, false);
        }
        this.activeSources.clear();
        this.activeSources.addAll((LongCollection)this.desiredSources);
        this.tracker.runAllUpdates();
        ChunkCutterMod.sectionGenerationQueue((ResourceKey<Level>)level.dimension()).refresh(level);
    }

    public boolean wants(int sectionX, int sectionY, int sectionZ) {
        return this.tracker.distance(SectionPos.asLong((int)sectionX, (int)sectionY, (int)sectionZ)) <= this.viewDistance;
    }

    public boolean keep(int sectionX, int sectionY, int sectionZ) {
        return this.tracker.distance(SectionPos.asLong((int)sectionX, (int)sectionY, (int)sectionZ)) <= this.viewDistance + 1;
    }

    public boolean ticking(int sectionX, int sectionY, int sectionZ) {
        return this.tracker.distance(SectionPos.asLong((int)sectionX, (int)sectionY, (int)sectionZ)) <= this.simulationDistance;
    }

    public boolean shouldUnload(long key, long now) {
        Long since = this.unloadSince.get(key);
        if (since == null) {
            this.unloadSince.put(key, now);
            return false;
        }
        return now - since >= 20L;
    }

    public void cancelUnload(long key) {
        this.unloadSince.remove(key);
    }
}


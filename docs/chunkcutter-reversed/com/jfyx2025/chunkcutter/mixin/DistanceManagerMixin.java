/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ByteMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.LongConsumer
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkLevel
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.server.level.DistanceManager$FixedPlayerDistanceChunkTracker
 *  net.minecraft.server.level.DistanceManager$PlayerTicketTracker
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.LoadingChunkTracker
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.SimulationChunkTracker
 *  net.minecraft.server.level.ThrottlingChunkTaskDispatcher
 *  net.minecraft.server.level.Ticket
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.util.TriState
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.NaturalSpawner
 *  net.minecraft.world.level.TicketStorage
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.SharedConstants;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.LoadingChunkTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.SimulationChunkTracker;
import net.minecraft.server.level.ThrottlingChunkTaskDispatcher;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.TriState;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.TicketStorage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={DistanceManager.class})
public abstract class DistanceManagerMixin {
    @Shadow
    private static int PLAYER_TICKET_LEVEL;
    @Shadow
    private Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk;
    @Shadow
    private LoadingChunkTracker loadingChunkTracker;
    @Shadow
    private SimulationChunkTracker simulationChunkTracker;
    @Shadow
    private TicketStorage ticketStorage;
    @Shadow
    private DistanceManager.FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter;
    @Shadow
    private DistanceManager.PlayerTicketTracker playerTicketManager;
    @Shadow
    protected Set<ChunkHolder> chunksToUpdateFutures;
    @Shadow
    private ThrottlingChunkTaskDispatcher ticketDispatcher;
    @Shadow
    private LongSet ticketsToRelease;
    @Shadow
    private Executor mainThreadExecutor;
    @Shadow
    private int simulationDistance;

    @Shadow
    public abstract boolean isChunkToRemove(long var1);

    @Shadow
    public abstract @Nullable ChunkHolder getChunk(long var1);

    @Shadow
    public abstract @Nullable ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    @Overwrite
    public boolean runAllUpdates(ChunkMap scheduler) {
        boolean updated;
        this.naturalSpawnChunkCounter.runAllUpdates();
        this.simulationChunkTracker.runAllUpdates();
        this.playerTicketManager.runAllUpdates();
        int updates = Integer.MAX_VALUE - this.loadingChunkTracker.runDistanceUpdates(Integer.MAX_VALUE);
        boolean bl = updated = updates != 0;
        if (!updated || SharedConstants.DEBUG_VERBOSE_SERVER_EVENTS) {
            // empty if block
        }
        if (!this.chunksToUpdateFutures.isEmpty()) {
            for (ChunkHolder chunksToUpdateFuture : this.chunksToUpdateFutures) {
                chunksToUpdateFuture.updateHighestAllowedStatus(scheduler);
            }
            for (ChunkHolder chunkHolder : this.chunksToUpdateFutures) {
                chunkHolder.updateFutures(scheduler, this.mainThreadExecutor);
            }
            this.chunksToUpdateFutures.clear();
            return true;
        }
        if (!this.ticketsToRelease.isEmpty()) {
            LongIterator iterator = this.ticketsToRelease.iterator();
            while (iterator.hasNext()) {
                long pos = iterator.nextLong();
                if (!this.ticketStorage.getTickets(pos).stream().anyMatch(t -> t.getType() == TicketType.PLAYER_LOADING)) continue;
                ChunkHolder chunk = scheduler.getUpdatingChunkIfPresent(pos);
                if (chunk == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture future = chunk.getEntityTickingChunkFuture();
                future.thenAccept(c -> this.mainThreadExecutor.execute(() -> this.ticketDispatcher.release(pos, () -> {}, false)));
            }
            this.ticketsToRelease.clear();
        }
        return updated;
    }

    @Overwrite
    public void addPlayer(SectionPos pos, ServerPlayer player) {
        ChunkPos chunk = pos.chunk();
        long chunkPos = chunk.pack();
        ((ObjectSet)this.playersPerChunk.computeIfAbsent(chunkPos, k -> new ObjectOpenHashSet())).add((Object)player);
        this.naturalSpawnChunkCounter.update(chunkPos, 0, true);
        this.playerTicketManager.update(chunkPos, 0, true);
        this.ticketStorage.addTicket(new Ticket(TicketType.PLAYER_SIMULATION, this.getPlayerTicketLevel()), chunk);
        ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)player.level().dimension()).setPlayerSource(player, pos);
        CubeProvider.preloadPlayer(player.level(), player);
    }

    @Overwrite
    public void removePlayer(SectionPos pos, ServerPlayer player) {
        ChunkPos chunk = pos.chunk();
        long chunkPos = chunk.pack();
        ObjectSet chunkPlayers = (ObjectSet)this.playersPerChunk.get(chunkPos);
        chunkPlayers.remove((Object)player);
        if (chunkPlayers.isEmpty()) {
            this.playersPerChunk.remove(chunkPos);
            this.naturalSpawnChunkCounter.update(chunkPos, Integer.MAX_VALUE, false);
            this.playerTicketManager.update(chunkPos, Integer.MAX_VALUE, false);
            this.ticketStorage.removeTicket(new Ticket(TicketType.PLAYER_SIMULATION, this.getPlayerTicketLevel()), chunk);
        }
        ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)player.level().dimension()).removePlayerSource(player);
    }

    @Overwrite
    public int getPlayerTicketLevel() {
        return Math.max(0, ChunkLevel.byStatus((FullChunkStatus)FullChunkStatus.ENTITY_TICKING) - this.simulationDistance);
    }

    @Overwrite
    public boolean inEntityTickingRange(long key) {
        return ChunkLevel.isEntityTicking((int)this.simulationChunkTracker.getLevel(key));
    }

    @Overwrite
    public boolean inBlockTickingRange(long key) {
        return ChunkLevel.isBlockTicking((int)this.simulationChunkTracker.getLevel(key));
    }

    @Overwrite
    public int getChunkLevel(long key, boolean simulation) {
        return simulation ? this.simulationChunkTracker.getLevel(key) : this.loadingChunkTracker.getLevel(key);
    }

    @Overwrite
    public void updatePlayerTickets(int viewDistance) {
        this.playerTicketManager.updateViewDistance(viewDistance);
    }

    @Overwrite
    public void updateSimulationDistance(int newDistance) {
        if (newDistance != this.simulationDistance) {
            this.simulationDistance = newDistance;
            this.ticketStorage.replaceTicketLevelOfType(this.getPlayerTicketLevel(), TicketType.PLAYER_SIMULATION);
        }
    }

    @Overwrite
    public int getNaturalSpawnChunkCount() {
        this.naturalSpawnChunkCounter.runAllUpdates();
        return this.naturalSpawnChunkCounter.chunks.size();
    }

    @Overwrite
    public TriState hasPlayersNearby(long pos) {
        this.naturalSpawnChunkCounter.runAllUpdates();
        int distance = this.naturalSpawnChunkCounter.getLevel(pos);
        if (distance <= NaturalSpawner.INSCRIBED_SQUARE_SPAWN_DISTANCE_CHUNK) {
            return TriState.TRUE;
        }
        return distance > 8 ? TriState.FALSE : TriState.DEFAULT;
    }

    @Overwrite
    public void forEachEntityTickingChunk(LongConsumer consumer) {
        for (Long2ByteMap.Entry entry : Long2ByteMaps.fastIterable((Long2ByteMap)this.simulationChunkTracker.chunks)) {
            byte level = entry.getByteValue();
            long key = entry.getLongKey();
            if (!ChunkLevel.isEntityTicking((int)level)) continue;
            consumer.accept(key);
        }
    }

    @Overwrite
    public LongIterator getSpawnCandidateChunks() {
        this.naturalSpawnChunkCounter.runAllUpdates();
        return this.naturalSpawnChunkCounter.chunks.keySet().iterator();
    }

    @Overwrite
    public String getDebugStatus() {
        return this.ticketDispatcher.getDebugStatus();
    }

    @Overwrite
    public boolean hasTickets() {
        return this.ticketStorage.hasTickets();
    }
}


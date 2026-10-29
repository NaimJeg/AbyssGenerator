/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.SharedConstants
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkLevel
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.Ticket
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.TicketStorage
 *  net.minecraft.world.level.TicketStorage$TicketPredicate
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedDataType
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.cubic.TicketSource;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={TicketStorage.class})
public abstract class TicketStorageMixin
extends SavedData
implements TicketSource {
    @Shadow
    private static int INITIAL_TICKET_LIST_CAPACITY;
    @Shadow
    private static Codec<Pair<ChunkPos, Ticket>> TICKET_ENTRY;
    @Shadow
    public static Codec<TicketStorage> CODEC;
    @Shadow
    public static SavedDataType<TicketStorage> TYPE;
    @Shadow
    private Long2ObjectOpenHashMap<List<Ticket>> tickets;
    @Shadow
    private Long2ObjectOpenHashMap<List<Ticket>> deactivatedTickets;
    @Shadow
    private LongSet chunksWithForcedTickets;
    @Shadow
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable TicketStorage.ChunkUpdated loadingChunkUpdatedListener;
    @Shadow
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable TicketStorage.ChunkUpdated simulationChunkUpdatedListener;

    @Override
    public void chunkcutter$forEachTicket(BiConsumer<ChunkPos, Ticket> output) {
        this.forEachTicket(output);
    }

    @Overwrite
    public static TicketStorage fromPacked(List<Pair<ChunkPos, Ticket>> tickets) {
        Long2ObjectOpenHashMap ticketsToLoad = new Long2ObjectOpenHashMap();
        for (Pair<ChunkPos, Ticket> ticket : tickets) {
            ChunkPos pos = (ChunkPos)ticket.getFirst();
            List ticketsInChunk = (List)ticketsToLoad.computeIfAbsent(pos.pack(), k -> new ObjectArrayList(4));
            ticketsInChunk.add((Ticket)ticket.getSecond());
        }
        return new TicketStorage(new Long2ObjectOpenHashMap(4), ticketsToLoad);
    }

    @Overwrite
    public List<Pair<ChunkPos, Ticket>> packTickets() {
        ArrayList<Pair<ChunkPos, Ticket>> tickets = new ArrayList<Pair<ChunkPos, Ticket>>();
        this.forEachTicket((pos, ticket) -> {
            if (ticket.getType().persist()) {
                tickets.add(new Pair(pos, ticket));
            }
        });
        return tickets;
    }

    @Overwrite
    public void forEachTicket(BiConsumer<ChunkPos, Ticket> output) {
        TicketStorageMixin.forEachTicket(output, this.tickets);
        TicketStorageMixin.forEachTicket(output, this.deactivatedTickets);
    }

    @Overwrite
    public static void forEachTicket(BiConsumer<ChunkPos, Ticket> output, Long2ObjectOpenHashMap<List<Ticket>> tickets) {
        for (Long2ObjectMap.Entry entry : Long2ObjectMaps.fastIterable(tickets)) {
            ChunkPos chunkPos = ChunkPos.unpack((long)entry.getLongKey());
            for (Ticket ticket : (List)entry.getValue()) {
                output.accept(chunkPos, ticket);
            }
        }
    }

    @Overwrite
    public void activateAllDeactivatedTickets() {
        for (Long2ObjectMap.Entry entry : Long2ObjectMaps.fastIterable(this.deactivatedTickets)) {
            for (Ticket ticket : (List)entry.getValue()) {
                this.addTicket(entry.getLongKey(), ticket);
            }
        }
        this.deactivatedTickets.clear();
    }

    @Overwrite
    public void setLoadingChunkUpdatedListener(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable TicketStorage.ChunkUpdated loadingChunkUpdatedListener) {
        this.loadingChunkUpdatedListener = loadingChunkUpdatedListener;
    }

    @Overwrite
    public void setSimulationChunkUpdatedListener(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable TicketStorage.ChunkUpdated simulationChunkUpdatedListener) {
        this.simulationChunkUpdatedListener = simulationChunkUpdatedListener;
    }

    @Overwrite
    public boolean hasTickets() {
        return !this.tickets.isEmpty();
    }

    @Overwrite
    public boolean shouldKeepDimensionActive() {
        for (List group : this.tickets.values()) {
            for (Ticket ticket : group) {
                if (!ticket.getType().shouldKeepDimensionActive()) continue;
                return true;
            }
        }
        return false;
    }

    @Overwrite
    public List<Ticket> getTickets(long key) {
        return (List)this.tickets.getOrDefault(key, List.of());
    }

    @Overwrite
    public List<Ticket> getOrCreateTickets(long key) {
        return (List)this.tickets.computeIfAbsent(key, k -> new ObjectArrayList(4));
    }

    @Overwrite
    public void addTicketWithRadius(TicketType type, ChunkPos chunkPos, int radius) {
        Ticket ticket = new Ticket(type, ChunkLevel.byStatus((FullChunkStatus)FullChunkStatus.FULL) - radius);
        this.addTicket(chunkPos.pack(), ticket);
    }

    @Overwrite
    public void addTicket(Ticket ticket, ChunkPos chunkPos) {
        this.addTicket(chunkPos.pack(), ticket);
    }

    @Overwrite
    public boolean addTicket(long key, Ticket ticket) {
        List<Ticket> tickets = this.getOrCreateTickets(key);
        for (Ticket t : tickets) {
            if (!TicketStorageMixin.isTicketSameTypeAndLevel(ticket, t)) continue;
            t.resetTicksLeft();
            this.setDirty();
            return false;
        }
        int oldSimulationTicketLevel = TicketStorageMixin.getTicketLevelAt(tickets, true);
        int oldLoadingTicketLevel = TicketStorageMixin.getTicketLevelAt(tickets, false);
        tickets.add(ticket);
        if (SharedConstants.DEBUG_VERBOSE_SERVER_EVENTS) {
            // empty if block
        }
        if (ticket.getType().doesSimulate() && ticket.getTicketLevel() < oldSimulationTicketLevel && this.simulationChunkUpdatedListener != null) {
            this.simulationChunkUpdatedListener.update(key, ticket.getTicketLevel(), true);
        }
        if (ticket.getType().doesLoad() && ticket.getTicketLevel() < oldLoadingTicketLevel && this.loadingChunkUpdatedListener != null) {
            this.loadingChunkUpdatedListener.update(key, ticket.getTicketLevel(), true);
        }
        if (ticket.getType().equals((Object)TicketType.FORCED)) {
            this.chunksWithForcedTickets.add(key);
        }
        this.setDirty();
        return true;
    }

    @Overwrite
    public static boolean isTicketSameTypeAndLevel(Ticket ticket, Ticket t) {
        return t.getType() == ticket.getType() && t.getTicketLevel() == ticket.getTicketLevel();
    }

    @Overwrite
    public int getTicketLevelAt(long key, boolean simulation) {
        return TicketStorageMixin.getTicketLevelAt(this.getTickets(key), simulation);
    }

    @Overwrite
    public static int getTicketLevelAt(List<Ticket> tickets, boolean simulation) {
        Ticket lowestTicket = TicketStorageMixin.getLowestTicket(tickets, simulation);
        return lowestTicket == null ? ChunkLevel.MAX_LEVEL + 1 : lowestTicket.getTicketLevel();
    }

    @Overwrite
    public static @Nullable Ticket getLowestTicket(@Nullable List<Ticket> tickets, boolean simulation) {
        if (tickets == null) {
            return null;
        }
        Ticket t = null;
        for (Ticket ticket : tickets) {
            if (t != null && ticket.getTicketLevel() >= t.getTicketLevel()) continue;
            if (simulation && ticket.getType().doesSimulate()) {
                t = ticket;
                continue;
            }
            if (simulation || !ticket.getType().doesLoad()) continue;
            t = ticket;
        }
        return t;
    }

    @Overwrite
    public void removeTicketWithRadius(TicketType type, ChunkPos chunkPos, int radius) {
        Ticket ticket = new Ticket(type, ChunkLevel.byStatus((FullChunkStatus)FullChunkStatus.FULL) - radius);
        this.removeTicket(chunkPos.pack(), ticket);
    }

    @Overwrite
    public void removeTicket(Ticket ticket, ChunkPos chunkPos) {
        this.removeTicket(chunkPos.pack(), ticket);
    }

    @Overwrite
    public boolean removeTicket(long key, Ticket ticket) {
        List tickets = (List)this.tickets.get(key);
        if (tickets == null) {
            return false;
        }
        boolean found = false;
        Iterator iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket t = (Ticket)iterator.next();
            if (!TicketStorageMixin.isTicketSameTypeAndLevel(ticket, t)) continue;
            iterator.remove();
            if (SharedConstants.DEBUG_VERBOSE_SERVER_EVENTS) {
                // empty if block
            }
            found = true;
            break;
        }
        if (!found) {
            return false;
        }
        if (tickets.isEmpty()) {
            this.tickets.remove(key);
        }
        if (ticket.getType().doesSimulate() && this.simulationChunkUpdatedListener != null) {
            this.simulationChunkUpdatedListener.update(key, TicketStorageMixin.getTicketLevelAt(tickets, true), false);
        }
        if (ticket.getType().doesLoad() && this.loadingChunkUpdatedListener != null) {
            this.loadingChunkUpdatedListener.update(key, TicketStorageMixin.getTicketLevelAt(tickets, false), false);
        }
        if (ticket.getType().equals((Object)TicketType.FORCED)) {
            this.updateForcedChunks();
        }
        this.setDirty();
        return true;
    }

    @Overwrite
    public void updateForcedChunks() {
        this.chunksWithForcedTickets = this.getAllChunksWithTicketThat(t -> t.getType().equals((Object)TicketType.FORCED));
    }

    @Overwrite
    public String getTicketDebugString(long key, boolean simulation) {
        List<Ticket> tickets = this.getTickets(key);
        Ticket lowestTicket = TicketStorageMixin.getLowestTicket(tickets, simulation);
        return lowestTicket == null ? "no_ticket" : lowestTicket.toString();
    }

    @Overwrite
    public void purgeStaleTickets(ChunkMap chunkMap) {
        this.removeTicketIf((ticket, chunkPos) -> {
            if (this.canTicketExpire(chunkMap, ticket, chunkPos)) {
                ticket.decreaseTicksLeft();
                return ticket.isTimedOut();
            }
            return false;
        }, null);
        this.setDirty();
    }

    @Overwrite
    public boolean canTicketExpire(ChunkMap chunkMap, Ticket ticket, long chunkPos) {
        if (!ticket.getType().hasTimeout()) {
            return false;
        }
        if (ticket.getType().canExpireIfUnloaded()) {
            return true;
        }
        ChunkHolder updatingChunk = chunkMap.getUpdatingChunkIfPresent(chunkPos);
        return updatingChunk == null || updatingChunk.isReadyForSaving();
    }

    @Overwrite
    public void deactivateTicketsOnClosing() {
        this.removeTicketIf((ticket, chunkPos) -> ticket.getType() != TicketType.UNKNOWN, this.deactivatedTickets);
    }

    @Overwrite
    public void removeTicketIf(TicketStorage.TicketPredicate predicate, @Nullable Long2ObjectOpenHashMap<List<Ticket>> removedTickets) {
        ObjectIterator ticketsPerChunkIterator = this.tickets.long2ObjectEntrySet().fastIterator();
        boolean removedForced = false;
        while (ticketsPerChunkIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)ticketsPerChunkIterator.next();
            Iterator chunkTicketsIterator = ((List)entry.getValue()).iterator();
            long chunkPos = entry.getLongKey();
            boolean removedSimulation = false;
            boolean removedLoading = false;
            while (chunkTicketsIterator.hasNext()) {
                Ticket ticket = (Ticket)chunkTicketsIterator.next();
                if (!predicate.test(ticket, chunkPos)) continue;
                if (removedTickets != null) {
                    List tickets = (List)removedTickets.computeIfAbsent(chunkPos, k -> new ObjectArrayList(((List)entry.getValue()).size()));
                    tickets.add(ticket);
                }
                chunkTicketsIterator.remove();
                if (ticket.getType().doesLoad()) {
                    removedLoading = true;
                }
                if (ticket.getType().doesSimulate()) {
                    removedSimulation = true;
                }
                if (!ticket.getType().equals((Object)TicketType.FORCED)) continue;
                removedForced = true;
            }
            if (!removedLoading && !removedSimulation) continue;
            if (removedLoading && this.loadingChunkUpdatedListener != null) {
                this.loadingChunkUpdatedListener.update(chunkPos, TicketStorageMixin.getTicketLevelAt((List)entry.getValue(), false), false);
            }
            if (removedSimulation && this.simulationChunkUpdatedListener != null) {
                this.simulationChunkUpdatedListener.update(chunkPos, TicketStorageMixin.getTicketLevelAt((List)entry.getValue(), true), false);
            }
            this.setDirty();
            if (!((List)entry.getValue()).isEmpty()) continue;
            ticketsPerChunkIterator.remove();
        }
        if (removedForced) {
            this.updateForcedChunks();
        }
    }

    @Overwrite
    public void replaceTicketLevelOfType(int newLevel, TicketType ticketType) {
        ArrayList<Pair> affectedTickets = new ArrayList<Pair>();
        for (Long2ObjectMap.Entry entry : this.tickets.long2ObjectEntrySet()) {
            for (Ticket ticket : (List)entry.getValue()) {
                if (ticket.getType() != ticketType) continue;
                affectedTickets.add(Pair.of((Object)ticket, (Object)entry.getLongKey()));
            }
        }
        for (Pair pair : affectedTickets) {
            Ticket ticket;
            Long key = (Long)pair.getSecond();
            ticket = (Ticket)pair.getFirst();
            this.removeTicket(key, ticket);
            TicketType type = ticket.getType();
            this.addTicket(key, new Ticket(type, newLevel));
        }
    }

    @Overwrite
    public boolean updateChunkForced(ChunkPos chunkPos, boolean forced) {
        Ticket ticket = new Ticket(TicketType.FORCED, ChunkMap.FORCED_TICKET_LEVEL);
        return forced ? this.addTicket(chunkPos.pack(), ticket) : this.removeTicket(chunkPos.pack(), ticket);
    }

    @Overwrite
    public LongSet getForceLoadedChunks() {
        return this.chunksWithForcedTickets;
    }

    @Overwrite
    public LongSet getAllChunksWithTicketThat(Predicate<Ticket> ticketCheck) {
        LongOpenHashSet chunks = new LongOpenHashSet();
        block0: for (Long2ObjectMap.Entry entry : Long2ObjectMaps.fastIterable(this.tickets)) {
            for (Ticket ticket : (List)entry.getValue()) {
                if (!ticketCheck.test(ticket)) continue;
                chunks.add(entry.getLongKey());
                continue block0;
            }
        }
        return chunks;
    }
}


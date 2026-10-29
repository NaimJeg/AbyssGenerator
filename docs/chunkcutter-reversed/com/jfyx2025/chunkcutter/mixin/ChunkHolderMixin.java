/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundLightUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkHolder$LevelChangeListener
 *  net.minecraft.server.level.ChunkHolder$PlayerProvider
 *  net.minecraft.server.level.ChunkLevel
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkResult
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.GenerationChunkHolder
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.cubic.CubeHolder;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkHolder.class})
public abstract class ChunkHolderMixin
implements CubeHolder {
    private long chunkcutter$cubeKey = Long.MIN_VALUE;
    @Shadow
    public static ChunkResult<LevelChunk> UNLOADED_LEVEL_CHUNK;
    @Shadow
    private static CompletableFuture<ChunkResult<LevelChunk>> UNLOADED_LEVEL_CHUNK_FUTURE;
    @Shadow
    private LevelHeightAccessor levelHeightAccessor;
    @Shadow
    private volatile CompletableFuture<ChunkResult<LevelChunk>> fullChunkFuture;
    @Shadow
    private volatile CompletableFuture<ChunkResult<LevelChunk>> tickingChunkFuture;
    @Shadow
    private volatile CompletableFuture<ChunkResult<LevelChunk>> entityTickingChunkFuture;
    @Shadow
    private int oldTicketLevel;
    @Shadow
    private int ticketLevel;
    @Shadow
    private int queueLevel;
    @Shadow
    private boolean hasChangedSections;
    @Shadow
    private @Nullable ShortSet[] changedBlocksPerSection;
    @Shadow
    private BitSet blockChangedLightSectionFilter;
    @Shadow
    private BitSet skyChangedLightSectionFilter;
    @Shadow
    private LevelLightEngine lightEngine;
    @Shadow
    private ChunkHolder.LevelChangeListener onLevelChange;
    @Shadow
    private ChunkHolder.PlayerProvider playerProvider;
    @Shadow
    private boolean wasAccessibleSinceLastSave;
    @Shadow
    private CompletableFuture<?> pendingFullStateConfirmation;
    @Shadow
    private CompletableFuture<?> sendSync;
    @Shadow
    private CompletableFuture<?> saveSync;

    @Override
    public long chunkcutter$cubeKey() {
        return this.chunkcutter$cubeKey;
    }

    @Override
    public void chunkcutter$setCubeKey(long key) {
        this.chunkcutter$cubeKey = key;
    }

    @Overwrite
    public CompletableFuture<ChunkResult<LevelChunk>> getTickingChunkFuture() {
        return this.tickingChunkFuture;
    }

    @Overwrite
    public CompletableFuture<ChunkResult<LevelChunk>> getEntityTickingChunkFuture() {
        return this.entityTickingChunkFuture;
    }

    @Overwrite
    public CompletableFuture<ChunkResult<LevelChunk>> getFullChunkFuture() {
        return this.fullChunkFuture;
    }

    @Overwrite
    public @Nullable LevelChunk getTickingChunk() {
        return (LevelChunk)this.getTickingChunkFuture().getNow(UNLOADED_LEVEL_CHUNK).orElse(null);
    }

    @Overwrite
    public @Nullable LevelChunk getChunkToSend() {
        return !this.sendSync.isDone() ? null : this.getTickingChunk();
    }

    @Overwrite
    public CompletableFuture<?> getSendSyncFuture() {
        return this.sendSync;
    }

    @Overwrite
    public void addSendDependency(CompletableFuture<?> sync) {
        this.sendSync = this.sendSync.isDone() ? sync : this.sendSync.thenCombine(sync, (a, b) -> null);
    }

    @Overwrite
    public CompletableFuture<?> getSaveSyncFuture() {
        return this.saveSync;
    }

    @Overwrite
    public boolean isReadyForSaving() {
        return this.saveSync.isDone();
    }

    @Overwrite
    public void addSaveDependency(CompletableFuture<?> sync) {
        this.saveSync = this.saveSync.isDone() ? sync : this.saveSync.thenCombine(sync, (a, b) -> null);
    }

    @Overwrite
    public boolean blockChanged(BlockPos pos) {
        LevelChunk chunk = this.getTickingChunk();
        if (chunk == null) {
            return false;
        }
        boolean hadChangedSections = this.hasChangedSections;
        int sectionIndex = this.levelHeightAccessor.getSectionIndex(pos.getY());
        ShortSet changedBlocksInSection = this.changedBlocksPerSection[sectionIndex];
        if (changedBlocksInSection == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[sectionIndex] = changedBlocksInSection = new ShortOpenHashSet();
        }
        changedBlocksInSection.add(SectionPos.sectionRelativePos((BlockPos)pos));
        return !hadChangedSections;
    }

    @Overwrite
    public boolean sectionLightChanged(LightLayer layer, int chunkY) {
        ChunkAccess chunk = ((GenerationChunkHolder)this).getChunkIfPresent(ChunkStatus.INITIALIZE_LIGHT);
        if (chunk == null) {
            return false;
        }
        chunk.markUnsaved();
        LevelChunk tickingChunk = this.getTickingChunk();
        if (tickingChunk == null) {
            return false;
        }
        int minLightSection = this.lightEngine.getMinLightSection();
        int maxLightSection = this.lightEngine.getMaxLightSection();
        if (chunkY >= minLightSection && chunkY <= maxLightSection) {
            int index;
            BitSet filter = layer == LightLayer.SKY ? this.skyChangedLightSectionFilter : this.blockChangedLightSectionFilter;
            if (!filter.get(index = chunkY - minLightSection)) {
                filter.set(index);
                return true;
            }
            return false;
        }
        return false;
    }

    @Overwrite
    public boolean hasChangesToBroadcast() {
        return this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty();
    }

    @Overwrite
    public void broadcastChanges(LevelChunk chunk) {
        if (this.hasChangesToBroadcast()) {
            Level level = chunk.getLevel();
            if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
                List borderPlayers = this.playerProvider.getPlayers(((GenerationChunkHolder)this).pos, true);
                if (!borderPlayers.isEmpty()) {
                    ClientboundLightUpdatePacket lightPacket = new ClientboundLightUpdatePacket(chunk.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter);
                    this.broadcast(borderPlayers, (Packet<?>)lightPacket);
                }
                this.skyChangedLightSectionFilter.clear();
                this.blockChangedLightSectionFilter.clear();
            }
            if (this.hasChangedSections) {
                List players = this.playerProvider.getPlayers(((GenerationChunkHolder)this).pos, false);
                for (int sectionIndex = 0; sectionIndex < this.changedBlocksPerSection.length; ++sectionIndex) {
                    ShortSet changedBlocks = this.changedBlocksPerSection[sectionIndex];
                    if (changedBlocks == null) continue;
                    this.changedBlocksPerSection[sectionIndex] = null;
                    if (players.isEmpty()) continue;
                    int sectionY = this.levelHeightAccessor.getSectionYFromSectionIndex(sectionIndex);
                    SectionPos sectionPos = SectionPos.of((ChunkPos)chunk.getPos(), (int)sectionY);
                    if (changedBlocks.size() == 1) {
                        BlockPos pos2 = sectionPos.relativeToBlockPos(changedBlocks.iterator().nextShort());
                        BlockState state2 = level.getBlockState(pos2);
                        this.broadcast(players, (Packet<?>)new ClientboundBlockUpdatePacket(pos2, state2));
                        this.broadcastBlockEntityIfNeeded(players, level, pos2, state2);
                        continue;
                    }
                    LevelChunkSection section = chunk.getSection(sectionIndex);
                    ClientboundSectionBlocksUpdatePacket packet = new ClientboundSectionBlocksUpdatePacket(sectionPos, changedBlocks, section);
                    this.broadcast(players, (Packet<?>)packet);
                    packet.runUpdates((pos, state) -> this.broadcastBlockEntityIfNeeded(players, level, (BlockPos)pos, (BlockState)state));
                }
                this.hasChangedSections = false;
            }
        }
    }

    @Overwrite
    public void broadcastBlockEntityIfNeeded(List<ServerPlayer> players, Level level, BlockPos pos, BlockState state) {
        if (state.hasBlockEntity()) {
            this.broadcastBlockEntity(players, level, pos);
        }
    }

    @Overwrite
    public void broadcastBlockEntity(List<ServerPlayer> players, Level level, BlockPos blockPos) {
        Packet packet;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity != null && (packet = blockEntity.getUpdatePacket()) != null) {
            this.broadcast(players, packet);
        }
    }

    @Overwrite
    public void broadcast(List<ServerPlayer> players, Packet<?> packet) {
        players.forEach(player -> player.connection.send(packet));
    }

    @Overwrite
    public int getTicketLevel() {
        return this.ticketLevel;
    }

    @Overwrite
    public int getQueueLevel() {
        return this.queueLevel;
    }

    @Overwrite
    public void setQueueLevel(int queueLevel) {
        this.queueLevel = queueLevel;
    }

    @Overwrite
    public void setTicketLevel(int ticketLevel) {
        this.ticketLevel = ticketLevel;
    }

    @Overwrite
    public void scheduleFullChunkPromotion(ChunkMap scheduler, CompletableFuture<ChunkResult<LevelChunk>> task, Executor mainThreadExecutor, FullChunkStatus status) {
        this.pendingFullStateConfirmation.cancel(false);
        CompletableFuture confirmation = new CompletableFuture();
        confirmation.thenRunAsync(() -> scheduler.onFullChunkStatusChange(((GenerationChunkHolder)this).pos, status), mainThreadExecutor);
        this.pendingFullStateConfirmation = confirmation;
        task.thenAccept(r -> r.ifSuccess(l -> confirmation.complete(null)));
    }

    @Overwrite
    public void demoteFullChunk(ChunkMap scheduler, FullChunkStatus status) {
        this.pendingFullStateConfirmation.cancel(false);
        scheduler.onFullChunkStatusChange(((GenerationChunkHolder)this).pos, status);
    }

    @Overwrite
    public boolean wasAccessibleSinceLastSave() {
        return this.wasAccessibleSinceLastSave;
    }

    @Overwrite
    public void refreshAccessibility() {
        this.wasAccessibleSinceLastSave = ChunkLevel.fullStatus((int)this.ticketLevel).isOrAfter(FullChunkStatus.FULL);
    }
}


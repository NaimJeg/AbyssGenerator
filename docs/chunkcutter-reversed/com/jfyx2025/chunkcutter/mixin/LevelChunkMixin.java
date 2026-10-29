/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityTagOutput
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.ProblemReporter
 *  net.minecraft.util.ProblemReporter$ScopedCollector
 *  net.minecraft.util.debug.DebugStructureInfo
 *  net.minecraft.util.debug.DebugStructureInfo$Piece
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueSource
 *  net.minecraft.util.debug.DebugValueSource$Registration
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Block$UpdateFlags
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.TickingBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkAccess$PackedTicks
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunk$BoundTickingBlockEntity
 *  net.minecraft.world.level.chunk.LevelChunk$EntityCreationType
 *  net.minecraft.world.level.chunk.LevelChunk$RebindableTickingBlockEntityWrapper
 *  net.minecraft.world.level.chunk.LevelChunk$UnsavedListener
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.ProtoChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry
 *  net.minecraft.world.level.gameevent.GameEventListener
 *  net.minecraft.world.level.gameevent.GameEventListenerRegistry
 *  net.minecraft.world.level.levelgen.DebugLevelSource
 *  net.minecraft.world.level.levelgen.Heightmap
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.StructurePiece
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.lighting.LightEngine
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.storage.TagValueInput
 *  net.minecraft.world.ticks.LevelChunkTicks
 *  net.minecraft.world.ticks.TickContainerAccess
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.collect.ImmutableList;
import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.cubic.ChunkCutterSectionWrite;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.cubic.SectionDirtySink;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.debug.DebugStructureInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LevelChunk.class})
public abstract class LevelChunkMixin
extends ChunkAccess
implements DebugValueSource {
    @Shadow
    private static TickingBlockEntity NULL_TICKER;
    @Shadow
    private Map<BlockPos, LevelChunk.RebindableTickingBlockEntityWrapper> tickersInLevel;
    @Shadow
    private boolean loaded;
    @Shadow
    private Level level;
    @Shadow
    private @Nullable Supplier<FullChunkStatus> fullStatus;
    @Shadow
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable LevelChunk.PostLoadProcessor postLoad;
    @Shadow
    private Int2ObjectMap<GameEventListenerRegistry> gameEventListenerRegistrySections;
    @Shadow
    private LevelChunkTicks<Block> blockTicks;
    @Shadow
    private LevelChunkTicks<Fluid> fluidTicks;
    @Shadow
    private LevelChunk.UnsavedListener unsavedListener;

    @Shadow
    public abstract BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2);

    @Shadow
    public abstract void removeBlockEntity(BlockPos var1);

    public LevelChunkMixin() {
        super(null, null, null, null, 0L, null, null);
    }

    @Overwrite
    public void setUnsavedListener(LevelChunk.UnsavedListener unsavedListener) {
        this.unsavedListener = unsavedListener;
        if (this.isUnsaved()) {
            unsavedListener.setUnsaved(this.chunkPos);
        }
    }

    @Overwrite
    public void markUnsaved() {
        boolean wasUnsaved = this.isUnsaved();
        super.markUnsaved();
        if (!wasUnsaved) {
            this.unsavedListener.setUnsaved(this.chunkPos);
        }
    }

    @Overwrite
    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Overwrite
    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    @Overwrite
    public ChunkAccess.PackedTicks getTicksForSerialization(long currentTick) {
        return new ChunkAccess.PackedTicks(this.blockTicks.pack(currentTick), this.fluidTicks.pack(currentTick));
    }

    @Overwrite
    public GameEventListenerRegistry getListenerRegistry(int section) {
        GameEventListenerRegistry gameEventListenerRegistry;
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            gameEventListenerRegistry = (GameEventListenerRegistry)this.gameEventListenerRegistrySections.computeIfAbsent(section, key -> new EuclideanGameEventListenerRegistry(serverLevel, section, this::removeGameEventListenerRegistry));
        } else {
            gameEventListenerRegistry = super.getListenerRegistry(section);
        }
        return gameEventListenerRegistry;
    }

    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        CubeSource source;
        CubeChunk cube;
        ChunkSource chunkSource;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (ChunkCutterConfig.isEnabled() && (chunkSource = this.level.getChunkSource()) instanceof CubeSource && (cube = (source = (CubeSource)chunkSource).chunkcutter$cubeMap().get(CloPos.cube(this.chunkPos.x(), y >> 4, this.chunkPos.z()))) != null && cube.section() != null) {
            return cube.section().getBlockState(x & 0xF, y & 0xF, z & 0xF);
        }
        if (this.level.isDebug()) {
            BlockState blockState = null;
            if (y == 60) {
                blockState = Blocks.BARRIER.defaultBlockState();
            }
            if (y == 70) {
                blockState = DebugLevelSource.getBlockStateFor((int)x, (int)z);
            }
            return blockState == null ? Blocks.AIR.defaultBlockState() : blockState;
        }
        try {
            LevelChunkSection currentSection;
            int sectionIndex = this.getSectionIndex(y);
            if (sectionIndex >= 0 && sectionIndex < this.sections.length && (currentSection = this.sections[sectionIndex]) != null && !currentSection.hasOnlyAir()) {
                return currentSection.getBlockState(x & 0xF, y & 0xF, z & 0xF);
            }
            return Blocks.AIR.defaultBlockState();
        }
        catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable((Throwable)t, (String)"Getting block state");
            CrashReportCategory category = report.addCategory("Block being got");
            category.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, (int)x, (int)y, (int)z));
            throw new ReportedException(report);
        }
    }

    @Overwrite
    public FluidState getFluidState(BlockPos pos) {
        return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    @Overwrite
    public FluidState getFluidState(int x, int y, int z) {
        try {
            LevelChunkSection currentSection;
            int sectionIndex = this.getSectionIndex(y);
            if (sectionIndex >= 0 && sectionIndex < this.sections.length && (currentSection = this.sections[sectionIndex]) != null && !currentSection.hasOnlyAir()) {
                return currentSection.getFluidState(x & 0xF, y & 0xF, z & 0xF);
            }
            return Fluids.EMPTY.defaultFluidState();
        }
        catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable((Throwable)t, (String)"Getting fluid state");
            CrashReportCategory category = report.addCategory("Block being got");
            category.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, (int)x, (int)y, (int)z));
            throw new ReportedException(report);
        }
    }

    @Overwrite
    public @Nullable BlockState setBlockState(BlockPos pos, BlockState state, @Block.UpdateFlags int flags) {
        ServerLevel serverLevel;
        Level level;
        BlockEntity blockEntity;
        boolean sideEffects;
        int localZ;
        int localY;
        boolean wasEmpty;
        ServerLevel serverLevel2;
        Level level2;
        int y = pos.getY();
        LevelChunkSection section = null;
        if (ChunkCutterConfig.isEnabled() && (level2 = this.level) instanceof ServerLevel && (level2 = (serverLevel2 = (ServerLevel)level2).getChunkSource()) instanceof CubeSource) {
            CubeSource source = (CubeSource)level2;
            int sy = SectionPos.blockToSectionCoord((int)y);
            CubeProvider.ensureSectionForWrite(serverLevel2, this.chunkPos.x(), sy, this.chunkPos.z(), (LevelChunk)this);
            CubeChunk cube = source.chunkcutter$cubeMap().get(CloPos.cube(this.chunkPos.x(), sy, this.chunkPos.z()));
            if (cube != null && cube.section() != null) {
                section = cube.section();
            }
        }
        if (section == null) {
            section = ((ChunkCutterSectionWrite)((Object)this)).chunkcutter$getSectionForWrite(this.getSectionIndex(y));
        }
        if ((wasEmpty = section.hasOnlyAir()) && state.isAir()) {
            return null;
        }
        int localX = pos.getX() & 0xF;
        BlockState oldState = section.setBlockState(localX, localY = y & 0xF, localZ = pos.getZ() & 0xF, state);
        if (oldState == state) {
            return null;
        }
        Block newBlock = state.getBlock();
        ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update(localX, y, localZ, state);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(localX, y, localZ, state);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update(localX, y, localZ, state);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update(localX, y, localZ, state);
        boolean isEmpty = section.hasOnlyAir();
        if (wasEmpty != isEmpty) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(pos, isEmpty);
            this.level.getChunkSource().onSectionEmptinessChanged(this.chunkPos.x(), SectionPos.blockToSectionCoord((int)y), this.chunkPos.z(), isEmpty);
        }
        if (LightEngine.hasDifferentLightProperties((BlockState)oldState, (BlockState)state)) {
            ProfilerFiller profiler = Profiler.get();
            profiler.push("updateSkyLightSources");
            this.skyLightSources.update((BlockGetter)this, localX, y, localZ);
            profiler.popPush("queueCheckLight");
            this.level.getChunkSource().getLightEngine().checkBlock(pos);
            profiler.pop();
        }
        boolean blockChanged = !oldState.is((Object)newBlock);
        boolean movedByPiston = (flags & 0x40) != 0;
        boolean bl = sideEffects = (flags & 0x100) == 0;
        if (blockChanged && oldState.hasBlockEntity() && !state.shouldChangedStateKeepBlockEntity(oldState)) {
            if (!this.level.isClientSide() && sideEffects && (blockEntity = this.level.getBlockEntity(pos)) != null) {
                blockEntity.preRemoveSideEffects(pos, oldState);
            }
            this.removeBlockEntity(pos);
        }
        if ((blockChanged || newBlock instanceof BaseRailBlock) && (level = this.level) instanceof ServerLevel) {
            serverLevel = (ServerLevel)level;
            if ((flags & 1) != 0 || movedByPiston) {
                oldState.affectNeighborsAfterRemoval(serverLevel, pos, movedByPiston);
            }
        }
        if (!section.getBlockState(localX, localY, localZ).is((Object)newBlock)) {
            return null;
        }
        if (!this.level.isClientSide() && (flags & 0x200) == 0) {
            state.onPlace(this.level, pos, oldState, movedByPiston);
        }
        if (state.hasBlockEntity() && section.getBlockState(localX, localY, localZ).is((Object)newBlock)) {
            blockEntity = this.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
            if (blockEntity != null && !blockEntity.isValidBlockState(state)) {
                this.removeBlockEntity(pos);
                blockEntity = null;
            }
            if (blockEntity == null) {
                blockEntity = ((EntityBlock)newBlock).newBlockEntity(pos, state);
                if (blockEntity != null) {
                    this.addAndRegisterBlockEntity(blockEntity);
                }
            } else {
                blockEntity.setBlockState(state);
                this.updateBlockEntityTicker(blockEntity);
            }
        }
        if (!this.level.isClientSide() && (level = this.level) instanceof ServerLevel) {
            serverLevel = (ServerLevel)level;
            int sx = this.chunkPos.x();
            int sy = SectionPos.blockToSectionCoord((int)y);
            int sz = this.chunkPos.z();
            for (ServerPlayer player : serverLevel.players()) {
                int view = Math.max(1, player.requestedViewDistance());
                if (Math.abs(sx - SectionPos.blockToSectionCoord((int)player.getBlockX())) > view || Math.abs(sy - SectionPos.blockToSectionCoord((int)player.getBlockY())) > view || Math.abs(sz - SectionPos.blockToSectionCoord((int)player.getBlockZ())) > view) continue;
                player.connection.send((Packet)new ClientboundBlockUpdatePacket(pos, state));
            }
        }
        this.markUnsaved();
        Level level3 = this.level;
        if (level3 instanceof SectionDirtySink) {
            SectionDirtySink sink = (SectionDirtySink)level3;
            sink.chunkcutter$setSectionDirty(this.chunkPos.x(), SectionPos.blockToSectionCoord((int)y), this.chunkPos.z());
        }
        return oldState;
    }

    @Deprecated
    @Overwrite
    public void addEntity(Entity entity) {
    }

    @Overwrite
    public @Nullable BlockEntity createBlockEntity(BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        return !state.hasBlockEntity() ? null : ((EntityBlock)state.getBlock()).newBlockEntity(pos, state);
    }

    @Overwrite
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
    }

    @Overwrite
    public void addAndRegisterBlockEntity(BlockEntity blockEntity) {
        this.setBlockEntity(blockEntity);
        if (this.isInLevel()) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                this.addGameEventListener(blockEntity, serverLevel);
            }
            this.level.onBlockEntityAdded(blockEntity);
            this.updateBlockEntityTicker(blockEntity);
        }
    }

    @Overwrite
    public boolean isInLevel() {
        return this.loaded || this.level.isClientSide();
    }

    @Overwrite
    public boolean isTicking(BlockPos pos) {
        boolean bl;
        if (!this.level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            bl = true;
        } else {
            ServerLevel serverLevel = (ServerLevel)level;
            bl = this.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING) && serverLevel.areEntitiesLoaded(ChunkPos.pack((BlockPos)pos));
        }
        return bl;
    }

    @Overwrite
    public @Nullable CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos, HolderLookup.Provider registryAccess) {
        BlockEntity blockEntity = this.getBlockEntity(blockPos);
        if (blockEntity != null && !blockEntity.isRemoved()) {
            CompoundTag result = blockEntity.saveWithFullMetadata((HolderLookup.Provider)this.level.registryAccess());
            result.putBoolean("keepPacked", false);
            return result;
        }
        CompoundTag result = (CompoundTag)this.pendingBlockEntities.get(blockPos);
        if (result != null) {
            result = result.copy();
            result.putBoolean("keepPacked", true);
        }
        return result;
    }

    @Overwrite
    public <T extends BlockEntity> void removeGameEventListener(T blockEntity, ServerLevel level) {
        EntityBlock entityBlock;
        GameEventListener listener;
        Block block = blockEntity.getBlockState().getBlock();
        if (block instanceof EntityBlock && (listener = (entityBlock = (EntityBlock)block).getListener(level, blockEntity)) != null) {
            int section = SectionPos.blockToSectionCoord((int)blockEntity.getBlockPos().getY());
            GameEventListenerRegistry listenerRegistry = this.getListenerRegistry(section);
            listenerRegistry.unregister(listener);
        }
    }

    @Overwrite
    public void removeGameEventListenerRegistry(int sectionY) {
        this.gameEventListenerRegistrySections.remove(sectionY);
    }

    @Overwrite
    public void removeBlockEntityTicker(BlockPos pos) {
        LevelChunk.RebindableTickingBlockEntityWrapper ticker = this.tickersInLevel.remove(pos);
        if (ticker != null) {
            ticker.rebind(NULL_TICKER);
        }
    }

    @Overwrite
    public void runPostLoad() {
        if (this.postLoad != null) {
            this.postLoad.run((LevelChunk)this);
            this.postLoad = null;
        }
    }

    @Overwrite
    public boolean isEmpty() {
        return false;
    }

    @Overwrite
    public void replaceWithPacketData(FriendlyByteBuf buffer, Map<Heightmap.Types, long[]> heightmaps, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> blockEntities) {
        int i;
        this.clearAllBlockEntities();
        LevelChunkSection[] sections = this.sections;
        boolean[] present = new boolean[sections.length];
        for (i = 0; i < sections.length; ++i) {
            present[i] = buffer.readBoolean();
        }
        for (i = 0; i < sections.length; ++i) {
            if (!present[i]) continue;
            ((ChunkCutterSectionWrite)((Object)this)).chunkcutter$getSectionForWrite(i).read(buffer);
        }
        heightmaps.forEach((arg_0, arg_1) -> ((LevelChunkMixin)this).setHeightmap(arg_0, arg_1));
        this.initializeLightSources();
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LogUtils.getLogger());){
            blockEntities.accept((pos, type, tag) -> {
                BlockEntity blockEntity = this.getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
                if (blockEntity != null && tag != null && blockEntity.getType() == type) {
                    blockEntity.loadWithComponents(TagValueInput.create((ProblemReporter)reporter.forChild(blockEntity.problemPath()), (HolderLookup.Provider)this.level.registryAccess(), (CompoundTag)tag));
                }
            });
        }
    }

    @Overwrite
    public void replaceBiomes(FriendlyByteBuf buffer) {
        int i;
        LevelChunkSection[] sections = this.sections;
        boolean[] present = new boolean[sections.length];
        for (i = 0; i < sections.length; ++i) {
            present[i] = buffer.readBoolean();
        }
        for (i = 0; i < sections.length; ++i) {
            if (!present[i]) continue;
            ((ChunkCutterSectionWrite)((Object)this)).chunkcutter$getSectionForWrite(i).readBiomes(buffer);
        }
    }

    @Overwrite
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Overwrite
    public Level getLevel() {
        return this.level;
    }

    @Overwrite
    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    @Overwrite
    public void postProcessGeneration(ServerLevel level) {
        ChunkPos chunkPos = this.getPos();
        for (int sectionIndex = 0; sectionIndex < this.postProcessing.length; ++sectionIndex) {
            ShortList postProcessingSection = this.postProcessing[sectionIndex];
            if (postProcessingSection == null) continue;
            for (Short packedOffset : postProcessingSection) {
                BlockPos blockPos = ProtoChunk.unpackOffsetCoordinates((short)packedOffset, (int)this.getSectionYFromSectionIndex(sectionIndex), (ChunkPos)chunkPos);
                BlockState blockState = this.getBlockState(blockPos);
                FluidState fluidState = blockState.getFluidState();
                if (!fluidState.isEmpty()) {
                    fluidState.tick(level, blockPos, blockState);
                }
                if (blockState.getBlock() instanceof LiquidBlock) {
                    blockState.tick(level, blockPos, level.getRandom());
                    continue;
                }
                BlockState blockStateNew = Block.updateFromNeighbourShapes((BlockState)blockState, (LevelAccessor)level, (BlockPos)blockPos);
                if (blockStateNew == blockState) continue;
                level.setBlock(blockPos, blockStateNew, 276);
            }
            postProcessingSection.clear();
        }
        for (BlockPos pos : ImmutableList.copyOf(this.pendingBlockEntities.keySet())) {
            this.getBlockEntity(pos);
        }
        this.pendingBlockEntities.clear();
        ((ChunkAccess)this).upgradeData.upgrade((LevelChunk)this);
    }

    @Overwrite
    public @Nullable BlockEntity promotePendingBlockEntity(BlockPos pos, CompoundTag tag) {
        BlockState state = this.getBlockState(pos);
        BlockEntity blockEntity = "DUMMY".equals(tag.getStringOr("id", "")) ? (state.hasBlockEntity() ? ((EntityBlock)state.getBlock()).newBlockEntity(pos, state) : null) : BlockEntity.loadStatic((BlockPos)pos, (BlockState)state, (CompoundTag)tag, (HolderLookup.Provider)this.level.registryAccess());
        if (blockEntity != null) {
            blockEntity.setLevel(this.level);
            this.addAndRegisterBlockEntity(blockEntity);
        }
        return blockEntity;
    }

    @Overwrite
    public void unpackTicks(long currentTick) {
        this.blockTicks.unpack(currentTick);
        this.fluidTicks.unpack(currentTick);
    }

    @Overwrite
    public void registerTickContainerInLevel(ServerLevel level) {
        level.getBlockTicks().addContainer(this.chunkPos, this.blockTicks);
        level.getFluidTicks().addContainer(this.chunkPos, this.fluidTicks);
    }

    @Overwrite
    public void unregisterTickContainerFromLevel(ServerLevel level) {
        level.getBlockTicks().removeContainer(this.chunkPos);
        level.getFluidTicks().removeContainer(this.chunkPos);
    }

    @Overwrite
    public void registerDebugValues(ServerLevel level, DebugValueSource.Registration registration) {
        if (!this.getAllStarts().isEmpty()) {
            registration.register(DebugSubscriptions.STRUCTURES, () -> {
                ArrayList<DebugStructureInfo> structures = new ArrayList<DebugStructureInfo>();
                for (StructureStart start : this.getAllStarts().values()) {
                    BoundingBox boundingBox = start.getBoundingBox();
                    List pieces = start.getPieces();
                    ArrayList<DebugStructureInfo.Piece> pieceInfos = new ArrayList<DebugStructureInfo.Piece>(pieces.size());
                    for (int i = 0; i < pieces.size(); ++i) {
                        boolean isStart = i == 0;
                        pieceInfos.add(new DebugStructureInfo.Piece(((StructurePiece)pieces.get(i)).getBoundingBox(), isStart));
                    }
                    structures.add(new DebugStructureInfo(boundingBox, pieceInfos));
                }
                return structures;
            });
        }
        registration.register(DebugSubscriptions.RAIDS, () -> level.getRaids().getRaidCentersInChunk(this.chunkPos));
    }

    @Overwrite
    public ChunkStatus getPersistedStatus() {
        return ChunkStatus.FULL;
    }

    @Overwrite
    public FullChunkStatus getFullStatus() {
        return this.fullStatus == null ? FullChunkStatus.FULL : this.fullStatus.get();
    }

    @Overwrite
    public void setFullStatus(Supplier<FullChunkStatus> fullStatus) {
        this.fullStatus = fullStatus;
    }

    @Overwrite
    public void clearAllBlockEntities() {
        this.blockEntities.values().forEach(BlockEntity::setRemoved);
        this.blockEntities.clear();
        this.tickersInLevel.values().forEach(ticker -> ticker.rebind(NULL_TICKER));
        this.tickersInLevel.clear();
    }

    @Overwrite
    public void registerAllBlockEntitiesAfterLevelLoad() {
        this.blockEntities.values().forEach(blockEntity -> {
            Level patt0$temp = this.level;
            if (patt0$temp instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)patt0$temp;
                this.addGameEventListener(blockEntity, serverLevel);
            }
            this.level.onBlockEntityAdded(blockEntity);
            this.updateBlockEntityTicker(blockEntity);
        });
    }

    @Overwrite
    public <T extends BlockEntity> void addGameEventListener(T blockEntity, ServerLevel level) {
        EntityBlock entityBlock;
        GameEventListener listener;
        Block block = blockEntity.getBlockState().getBlock();
        if (block instanceof EntityBlock && (listener = (entityBlock = (EntityBlock)block).getListener(level, blockEntity)) != null) {
            this.getListenerRegistry(SectionPos.blockToSectionCoord((int)blockEntity.getBlockPos().getY())).register(listener);
        }
    }

    @Overwrite
    public <T extends BlockEntity> void updateBlockEntityTicker(T blockEntity) {
        BlockState state = blockEntity.getBlockState();
        BlockEntityTicker ticker = state.getTicker(this.level, blockEntity.getType());
        if (ticker == null) {
            this.removeBlockEntityTicker(blockEntity.getBlockPos());
        } else {
            this.tickersInLevel.compute(blockEntity.getBlockPos(), (blockPos, existingTicker) -> {
                TickingBlockEntity actualTicker = this.createTicker(blockEntity, ticker);
                if (existingTicker != null) {
                    existingTicker.rebind(actualTicker);
                    return existingTicker;
                }
                if (this.isInLevel()) {
                    LevelChunk.RebindableTickingBlockEntityWrapper result = new LevelChunk.RebindableTickingBlockEntityWrapper(actualTicker);
                    this.level.addBlockEntityTicker((TickingBlockEntity)result);
                    return result;
                }
                return null;
            });
        }
    }

    @Overwrite
    public <T extends BlockEntity> TickingBlockEntity createTicker(T blockEntity, BlockEntityTicker<T> ticker) {
        LevelChunk levelChunk = (LevelChunk)this;
        Objects.requireNonNull(levelChunk);
        return new LevelChunk.BoundTickingBlockEntity(levelChunk, blockEntity, ticker);
    }
}


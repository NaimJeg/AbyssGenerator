/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkAccess$PackedTicks
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.UpgradeData
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.ticks.TickContainerAccess
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.ChunkCutterSectionWrite;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.SingleSectionHeightAccessor;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.TickContainerAccess;

public class CubeChunk
extends ChunkAccess {
    private final long chunkcutter$cubeKey;
    private List<CompoundTag> chunkcutter$blockEntities = List.of();

    public List<CompoundTag> chunkcutter$blockEntities() {
        return this.chunkcutter$blockEntities;
    }

    public void chunkcutter$setBlockEntities(List<CompoundTag> tags) {
        this.chunkcutter$blockEntities = List.copyOf(tags);
    }

    public CubeChunk(CloPos pos, PalettedContainerFactory factory) {
        super(new ChunkPos(pos.x(), pos.z()), UpgradeData.EMPTY, (LevelHeightAccessor)new SingleSectionHeightAccessor(pos.y()), factory, 0L, (LevelChunkSection[])null, null);
        this.chunkcutter$cubeKey = pos.asLong();
    }

    public long chunkcutter$key() {
        return this.chunkcutter$cubeKey;
    }

    public LevelChunkSection section() {
        return this.getSections()[0];
    }

    public void setSection(LevelChunkSection section) {
        this.getSections()[0] = section;
    }

    public BlockState setBlockState(BlockPos pos, BlockState state, int flags) {
        LevelChunkSection section = ((ChunkCutterSectionWrite)((Object)this)).chunkcutter$getSectionForWrite(0);
        return section.setBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, state);
    }

    public void setBlockEntity(BlockEntity blockEntity) {
    }

    public BlockState getBlockState(BlockPos pos) {
        LevelChunkSection section = this.getSections()[0];
        return section == null ? Blocks.AIR.defaultBlockState() : section.getBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }

    public FluidState getFluidState(BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }

    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    public void addEntity(Entity entity) {
    }

    public ChunkStatus getPersistedStatus() {
        return ChunkStatus.FULL;
    }

    public void removeBlockEntity(BlockPos pos) {
    }

    public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos, HolderLookup.Provider registryAccess) {
        return null;
    }

    public TickContainerAccess<Block> getBlockTicks() {
        throw new UnsupportedOperationException("cube block ticks");
    }

    public TickContainerAccess<Fluid> getFluidTicks() {
        throw new UnsupportedOperationException("cube fluid ticks");
    }

    public ChunkAccess.PackedTicks getTicksForSerialization(long currentTick) {
        throw new UnsupportedOperationException("cube ticks serialization");
    }
}


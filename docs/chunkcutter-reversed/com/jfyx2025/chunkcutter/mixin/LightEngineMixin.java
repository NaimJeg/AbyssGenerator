/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LightChunk
 *  net.minecraft.world.level.chunk.LightChunkGetter
 *  net.minecraft.world.level.lighting.DataLayerStorageMap
 *  net.minecraft.world.level.lighting.LayerLightEventListener
 *  net.minecraft.world.level.lighting.LayerLightSectionStorage
 *  net.minecraft.world.level.lighting.LayerLightSectionStorage$SectionType
 *  net.minecraft.world.level.lighting.LightEngine
 *  net.minecraft.world.level.lighting.LightEngine$QueueEntry
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LightEngine.class})
public abstract class LightEngineMixin<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>>
implements LayerLightEventListener {
    @Shadow
    public static int MAX_LEVEL;
    @Shadow
    protected static int MIN_OPACITY;
    @Shadow
    protected static long PULL_LIGHT_IN_ENTRY;
    @Shadow
    private static int MIN_QUEUE_SIZE;
    @Shadow
    protected static Direction[] PROPAGATION_DIRECTIONS;
    @Shadow
    protected LightChunkGetter chunkSource;
    @Shadow
    protected S storage;
    @Shadow
    private LongOpenHashSet blockNodesToCheck;
    @Shadow
    private LongArrayFIFOQueue decreaseQueue;
    @Shadow
    private LongArrayFIFOQueue increaseQueue;
    @Shadow
    private static int CACHE_SIZE;
    @Shadow
    private long[] lastChunkPos;
    @Shadow
    private LightChunk[] lastChunk;

    @Overwrite
    public static boolean hasDifferentLightProperties(BlockState oldState, BlockState newState) {
        return newState == oldState ? false : newState.getLightDampening() != oldState.getLightDampening() || newState.getLightEmission() != oldState.getLightEmission() || newState.useShapeForLightOcclusion() || oldState.useShapeForLightOcclusion();
    }

    @Overwrite
    public static int getLightDampeningInto(BlockState fromState, BlockState toState, Direction direction, int simpleOpacity) {
        boolean fromEmpty = LightEngineMixin.isEmptyShape(fromState);
        boolean toEmpty = LightEngineMixin.isEmptyShape(toState);
        if (fromEmpty && toEmpty) {
            return simpleOpacity;
        }
        VoxelShape fromShape = fromEmpty ? Shapes.empty() : fromState.getOcclusionShape();
        VoxelShape toShape = toEmpty ? Shapes.empty() : toState.getOcclusionShape();
        return Shapes.mergedFaceOccludes((VoxelShape)fromShape, (VoxelShape)toShape, (Direction)direction) ? 16 : simpleOpacity;
    }

    @Overwrite
    public static VoxelShape getOcclusionShape(BlockState state, Direction direction) {
        return LightEngineMixin.isEmptyShape(state) ? Shapes.empty() : state.getFaceOcclusionShape(direction);
    }

    @Overwrite
    public static boolean isEmptyShape(BlockState state) {
        return !state.canOcclude() || !state.useShapeForLightOcclusion();
    }

    @Overwrite
    public BlockState getState(BlockPos pos) {
        int chunkZ;
        int chunkX = SectionPos.blockToSectionCoord((int)pos.getX());
        LightChunk chunk = this.getChunk(chunkX, chunkZ = SectionPos.blockToSectionCoord((int)pos.getZ()));
        return chunk == null ? Blocks.BEDROCK.defaultBlockState() : chunk.getBlockState(pos);
    }

    @Overwrite
    public int getOpacity(BlockState state) {
        return Math.max(1, state.getLightDampening());
    }

    @Overwrite
    public boolean shapeOccludes(BlockState fromState, BlockState toState, Direction direction) {
        VoxelShape fromShape = LightEngineMixin.getOcclusionShape(fromState, direction);
        VoxelShape toShape = LightEngineMixin.getOcclusionShape(toState, direction.getOpposite());
        return Shapes.faceShapeOccludes((VoxelShape)fromShape, (VoxelShape)toShape);
    }

    @Overwrite
    public @Nullable LightChunk getChunk(int chunkX, int chunkZ) {
        long pos = ChunkPos.pack((int)chunkX, (int)chunkZ);
        for (int i = 0; i < 2; ++i) {
            if (pos != this.lastChunkPos[i]) continue;
            return this.lastChunk[i];
        }
        LightChunk chunk = this.chunkSource.getChunkForLighting(chunkX, chunkZ);
        for (int i = 1; i > 0; --i) {
            this.lastChunkPos[i] = this.lastChunkPos[i - 1];
            this.lastChunk[i] = this.lastChunk[i - 1];
        }
        this.lastChunkPos[0] = pos;
        this.lastChunk[0] = chunk;
        return chunk;
    }

    @Overwrite
    public void clearChunkCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunk, null);
    }

    @Overwrite
    public void checkBlock(BlockPos pos) {
        this.blockNodesToCheck.add(pos.asLong());
    }

    @Overwrite
    public void queueSectionData(long pos, @Nullable DataLayer data) {
        this.storage.queueSectionData(pos, data);
    }

    @Overwrite
    public void retainData(ChunkPos pos, boolean retain) {
        this.storage.retainData(SectionPos.getZeroNode((int)pos.x(), (int)pos.z()), retain);
    }

    @Overwrite
    public void updateSectionStatus(SectionPos pos, boolean sectionEmpty) {
        this.storage.updateSectionStatus(pos.asLong(), sectionEmpty);
    }

    @Overwrite
    public void setLightEnabled(ChunkPos pos, boolean enable) {
        this.storage.setLightEnabled(SectionPos.getZeroNode((int)pos.x(), (int)pos.z()), enable);
    }

    @Overwrite
    public int runLightUpdates() {
        LongIterator iterator = this.blockNodesToCheck.iterator();
        while (iterator.hasNext()) {
            this.checkNode(iterator.nextLong());
        }
        this.blockNodesToCheck.clear();
        this.blockNodesToCheck.trim(512);
        int count = 0;
        count += this.propagateDecreases();
        this.clearChunkCache();
        this.storage.markNewInconsistencies((LightEngine)this);
        this.storage.swapSectionMap();
        return count += this.propagateIncreases();
    }

    @Overwrite
    public int propagateIncreases() {
        int count = 0;
        while (!this.increaseQueue.isEmpty()) {
            long fromNode = this.increaseQueue.dequeueLong();
            long increaseData = this.increaseQueue.dequeueLong();
            int fromLevel = this.storage.getStoredLevel(fromNode);
            int fromTargetLevel = LightEngine.QueueEntry.getFromLevel((long)increaseData);
            if (LightEngine.QueueEntry.isIncreaseFromEmission((long)increaseData) && fromLevel < fromTargetLevel) {
                this.storage.setStoredLevel(fromNode, fromTargetLevel);
                fromLevel = fromTargetLevel;
            }
            if (fromLevel == fromTargetLevel) {
                this.propagateIncrease(fromNode, increaseData, fromLevel);
            }
            ++count;
        }
        return count;
    }

    @Overwrite
    public int propagateDecreases() {
        int count = 0;
        while (!this.decreaseQueue.isEmpty()) {
            long fromNode = this.decreaseQueue.dequeueLong();
            long decreaseData = this.decreaseQueue.dequeueLong();
            this.propagateDecrease(fromNode, decreaseData);
            ++count;
        }
        return count;
    }

    @Overwrite
    public void enqueueDecrease(long fromNode, long decreaseData) {
        this.decreaseQueue.enqueue(fromNode);
        this.decreaseQueue.enqueue(decreaseData);
    }

    @Overwrite
    public void enqueueIncrease(long fromNode, long increaseData) {
        this.increaseQueue.enqueue(fromNode);
        this.increaseQueue.enqueue(increaseData);
    }

    @Overwrite
    public boolean hasLightWork() {
        return this.storage.hasInconsistencies() || !this.blockNodesToCheck.isEmpty() || !this.decreaseQueue.isEmpty() || !this.increaseQueue.isEmpty();
    }

    @Overwrite
    public @Nullable DataLayer getDataLayerData(SectionPos pos) {
        return this.storage.getDataLayerData(pos.asLong());
    }

    @Overwrite
    public int getLightValue(BlockPos pos) {
        return this.storage.getLightValue(pos.asLong());
    }

    @Overwrite
    public String getDebugData(long sectionNode) {
        return this.getDebugSectionType(sectionNode).display();
    }

    @Overwrite
    public LayerLightSectionStorage.SectionType getDebugSectionType(long sectionNode) {
        return this.storage.getDebugSectionType(sectionNode);
    }

    @Shadow
    public abstract void checkNode(long var1);

    @Shadow
    public abstract void propagateIncrease(long var1, long var3, int var5);

    @Shadow
    public abstract void propagateDecrease(long var1, long var3);
}


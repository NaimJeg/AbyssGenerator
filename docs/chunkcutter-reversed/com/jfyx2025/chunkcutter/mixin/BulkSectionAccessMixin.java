/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.BulkSectionAccess
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.cubic.ChunkCutterSectionWrite;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BulkSectionAccess.class})
public abstract class BulkSectionAccessMixin
implements AutoCloseable {
    @Shadow
    private LevelAccessor level;
    @Shadow
    private Long2ObjectMap<LevelChunkSection> acquiredSections;
    @Shadow
    private @Nullable LevelChunkSection lastSection;
    @Shadow
    private long lastSectionKey;

    @Overwrite
    public @Nullable LevelChunkSection getSection(BlockPos pos) {
        int sectionIndex = this.level.getSectionIndex(pos.getY());
        if (sectionIndex >= 0 && sectionIndex < this.level.getSectionsCount()) {
            long sectionKey = SectionPos.asLong((BlockPos)pos);
            if (this.lastSection == null || this.lastSectionKey != sectionKey) {
                this.lastSection = (LevelChunkSection)this.acquiredSections.computeIfAbsent(sectionKey, key -> {
                    ChunkAccess chunk = this.level.getChunk(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getZ()));
                    LevelChunkSection result = ((ChunkCutterSectionWrite)chunk).chunkcutter$getSectionForWrite(sectionIndex);
                    result.acquire();
                    return result;
                });
                this.lastSectionKey = sectionKey;
            }
            return this.lastSection;
        }
        return null;
    }

    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        LevelChunkSection section = this.getSection(pos);
        if (section == null) {
            return Blocks.AIR.defaultBlockState();
        }
        int sectionRelativeX = SectionPos.sectionRelative((int)pos.getX());
        int sectionRelativeY = SectionPos.sectionRelative((int)pos.getY());
        int sectionRelativeZ = SectionPos.sectionRelative((int)pos.getZ());
        return section.getBlockState(sectionRelativeX, sectionRelativeY, sectionRelativeZ);
    }

    @Override
    @Overwrite
    public void close() {
        for (LevelChunkSection section : this.acquiredSections.values()) {
            section.release();
        }
    }
}


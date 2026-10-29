/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.client.renderer.chunk.SectionCopy
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  net.minecraft.world.level.levelgen.DebugLevelSource
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.jfyx2025.chunkcutter.client.mixin;

import java.util.Map;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SectionCopy.class})
public abstract class SectionCopyMixin {
    @Shadow
    private Map<BlockPos, BlockEntity> blockEntities;
    @Shadow
    private @Nullable PalettedContainer<BlockState> section;
    @Shadow
    private boolean debug;
    @Shadow
    private LevelHeightAccessor levelHeightAccessor;

    @Redirect(method={"<init>"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")})
    private boolean chunkcutter$air(LevelChunkSection levelChunkSection) {
        return levelChunkSection == null || levelChunkSection.hasOnlyAir();
    }

    @Overwrite
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.blockEntities.get(pos);
    }

    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (this.debug) {
            BlockState blockState = null;
            if (y == 60) {
                blockState = Blocks.BARRIER.defaultBlockState();
            }
            if (y == 70) {
                blockState = DebugLevelSource.getBlockStateFor((int)x, (int)z);
            }
            return blockState == null ? Blocks.AIR.defaultBlockState() : blockState;
        }
        if (this.section == null) {
            return Blocks.AIR.defaultBlockState();
        }
        try {
            return (BlockState)this.section.get(x & 0xF, y & 0xF, z & 0xF);
        }
        catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable((Throwable)t, (String)"Getting block state");
            CrashReportCategory category = report.addCategory("Block being got");
            category.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this.levelHeightAccessor, (int)x, (int)y, (int)z));
            throw new ReportedException(report);
        }
    }
}


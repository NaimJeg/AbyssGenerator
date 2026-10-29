/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.block.BlockAndTintGetter
 *  net.minecraft.client.renderer.chunk.RenderSectionRegion
 *  net.minecraft.client.renderer.chunk.SectionCopy
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.CardinalLighting
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.level.material.FluidState
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.client.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={RenderSectionRegion.class})
public abstract class RenderSectionRegionMixin
implements BlockAndTintGetter {
    @Shadow
    public static int RADIUS;
    @Shadow
    public static int SIZE;
    @Shadow
    private int minSectionX;
    @Shadow
    private int minSectionY;
    @Shadow
    private int minSectionZ;
    @Shadow
    private SectionCopy[] sections;
    @Shadow
    private ClientLevel level;
    @Shadow
    private CardinalLighting cardinalLighting;
    @Shadow
    private LevelLightEngine lightEngine;

    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        return this.getSection(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ())).getBlockState(pos);
    }

    @Overwrite
    public FluidState getFluidState(BlockPos pos) {
        return this.getSection(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ())).getBlockState(pos).getFluidState();
    }

    @Overwrite
    public CardinalLighting cardinalLighting() {
        return this.cardinalLighting;
    }

    @Overwrite
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Overwrite
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.getSection(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ())).getBlockEntity(pos);
    }

    @Overwrite
    public SectionCopy getSection(int sectionX, int sectionY, int sectionZ) {
        return this.sections[RenderSectionRegionMixin.index(this.minSectionX, this.minSectionY, this.minSectionZ, sectionX, sectionY, sectionZ)];
    }

    @Overwrite
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        return this.level.getBlockTint(pos, resolver);
    }

    @Overwrite
    public int getMinY() {
        return this.level.getMinY();
    }

    @Overwrite
    public int getHeight() {
        return this.level.getHeight();
    }

    @Overwrite
    public static int index(int minSectionX, int minSectionY, int minSectionZ, int sectionX, int sectionY, int sectionZ) {
        return sectionX - minSectionX + (sectionY - minSectionY) * 3 + (sectionZ - minSectionZ) * 3 * 3;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityFluidInteraction
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.jfyx2025.chunkcutter.mixin;

import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityFluidInteraction.class})
public abstract class EntityFluidInteractionMixin {
    @Redirect(method={"hasFluidAndLoaded"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;hasFluid()Z")})
    private static boolean chunkcutter$hasFluid(LevelChunkSection section) {
        return section != null && section.hasFluid();
    }
}


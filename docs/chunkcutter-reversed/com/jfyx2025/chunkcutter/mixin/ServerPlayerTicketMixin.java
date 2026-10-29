/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerPlayer.class})
public abstract class ServerPlayerTicketMixin {
    @Inject(method={"registerAndUpdateEnderPearlTicket"}, at={@At(value="HEAD")})
    private void chunkcutter$recordEnderPearlHeight(ThrownEnderpearl pearl, CallbackInfoReturnable<Long> cir) {
        Level level = pearl.level();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level2.dimension()).setTicketCenterY(pearl.chunkPosition(), SectionPos.blockToSectionCoord((int)pearl.getBlockY()), 2);
        }
    }
}


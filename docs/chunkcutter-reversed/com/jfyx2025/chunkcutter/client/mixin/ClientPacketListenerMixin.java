/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.jfyx2025.chunkcutter.client.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ClientPacketListener.class})
public abstract class ClientPacketListenerMixin {
    @Redirect(method={"enableChunkLight"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")})
    private boolean chunkcutter$air(LevelChunkSection section) {
        return section == null || section.hasOnlyAir();
    }
}


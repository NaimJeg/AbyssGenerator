/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class EntityTicketMixin {
    @Inject(method={"placePortalTicket"}, at={@At(value="HEAD")})
    private void chunkcutter$recordPortalTicketHeight(BlockPos pos, CallbackInfo ci) {
        Entity self = (Entity)this;
        Level level = self.level();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level2.dimension()).setTicketCenterY(ChunkPos.containing((BlockPos)pos), SectionPos.blockToSectionCoord((int)pos.getY()), 3);
        }
    }
}


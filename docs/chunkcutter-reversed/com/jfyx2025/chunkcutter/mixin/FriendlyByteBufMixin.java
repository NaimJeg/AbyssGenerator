/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package com.jfyx2025.chunkcutter.mixin;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={FriendlyByteBuf.class})
public abstract class FriendlyByteBufMixin {
    @Overwrite
    public static BlockPos readBlockPos(ByteBuf input) {
        return new BlockPos(input.readInt(), input.readInt(), input.readInt());
    }

    @Overwrite
    public static void writeBlockPos(ByteBuf output, BlockPos pos) {
        output.writeInt(pos.getX());
        output.writeInt(pos.getY());
        output.writeInt(pos.getZ());
    }
}


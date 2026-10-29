/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package com.jfyx2025.chunkcutter.mixin;

import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={BlockPos.class})
public abstract class BlockPosMixin {
    private static final long CC_XZ_MASK = 0xFFFFFFL;
    private static final long CC_Y_MASK = 65535L;

    @Overwrite
    public static long asLong(int x, int y, int z) {
        long node = 0L;
        node |= ((long)x & 0xFFFFFFL) << 40;
        return (node |= (long)y & 0xFFFFL) | ((long)z & 0xFFFFFFL) << 16;
    }

    @Overwrite
    public static int getX(long blockNode) {
        return (int)(blockNode >> 40);
    }

    @Overwrite
    public static int getY(long blockNode) {
        return (int)(blockNode << 48 >> 48);
    }

    @Overwrite
    public static int getZ(long blockNode) {
        return (int)(blockNode << 24 >> 40);
    }
}


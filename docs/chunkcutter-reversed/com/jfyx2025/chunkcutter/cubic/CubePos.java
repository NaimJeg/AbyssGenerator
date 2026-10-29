/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.SectionPos
 */
package com.jfyx2025.chunkcutter.cubic;

import net.minecraft.core.SectionPos;

public record CubePos(int x, int y, int z) {
    public static final int SIZE = 16;

    public static CubePos fromBlock(int blockX, int blockY, int blockZ) {
        return new CubePos(blockX >> 4, blockY >> 4, blockZ >> 4);
    }

    public static CubePos of(long packed) {
        return new CubePos(SectionPos.x((long)packed), SectionPos.y((long)packed), SectionPos.z((long)packed));
    }

    public long asLong() {
        return SectionPos.asLong((int)this.x, (int)this.y, (int)this.z);
    }

    public int minBlockX() {
        return this.x << 4;
    }

    public int minBlockY() {
        return this.y << 4;
    }

    public int minBlockZ() {
        return this.z << 4;
    }

    @Override
    public String toString() {
        return "CubePos[" + this.x + ", " + this.y + ", " + this.z + "]";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.ChunkPos
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CubePos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public final class CloPos {
    private final boolean cube;
    private final long packed;

    private CloPos(boolean cube, long packed) {
        this.cube = cube;
        this.packed = packed;
    }

    public static CloPos chunk(int x, int z) {
        return new CloPos(false, ChunkPos.pack((int)x, (int)z));
    }

    public static CloPos cube(int x, int y, int z) {
        return new CloPos(true, SectionPos.asLong((int)x, (int)y, (int)z));
    }

    public static CloPos of(CubePos pos) {
        return CloPos.cube(pos.x(), pos.y(), pos.z());
    }

    public static CloPos of(ChunkPos pos) {
        return CloPos.chunk(pos.x(), pos.z());
    }

    public static CloPos of(SectionPos pos) {
        return CloPos.cube(pos.x(), pos.y(), pos.z());
    }

    public static CloPos chunkOf(long packed) {
        return new CloPos(false, packed);
    }

    public static CloPos cubeOf(long packed) {
        return new CloPos(true, packed);
    }

    public boolean isCube() {
        return this.cube;
    }

    public boolean isChunk() {
        return !this.cube;
    }

    public int x() {
        return this.cube ? SectionPos.x((long)this.packed) : ChunkPos.getX((long)this.packed);
    }

    public int y() {
        return this.cube ? SectionPos.y((long)this.packed) : 0;
    }

    public int z() {
        return this.cube ? SectionPos.z((long)this.packed) : ChunkPos.getZ((long)this.packed);
    }

    public CubePos cubePos() {
        return new CubePos(this.x(), this.y(), this.z());
    }

    public ChunkPos chunkPos() {
        return new ChunkPos(this.x(), this.z());
    }

    public long asLong() {
        return this.packed;
    }

    public SectionPos asSectionPos() {
        return SectionPos.of((int)this.x(), (int)this.y(), (int)this.z());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof CloPos)) return false;
        CloPos other = (CloPos)obj;
        if (other.cube != this.cube) return false;
        if (other.packed != this.packed) return false;
        return true;
    }

    public int hashCode() {
        return Long.hashCode(this.packed);
    }

    public String toString() {
        return (this.cube ? "Cube[" : "Chunk[") + this.x() + (String)(this.cube ? ", " + this.y() : "") + ", " + this.z() + "]";
    }
}


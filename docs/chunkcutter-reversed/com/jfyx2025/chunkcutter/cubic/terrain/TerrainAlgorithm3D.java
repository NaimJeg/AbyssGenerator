/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import com.jfyx2025.chunkcutter.cubic.terrain.BlockWriter;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public interface TerrainAlgorithm3D {
    public int heightAt(int var1, int var2);

    public BlockState stateAt(int var1, int var2, int var3);

    default public Holder<Biome> biomeAt(int x, int y, int z) {
        return null;
    }

    default public List<CompoundTag> blockEntitiesAt(int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ) {
        return List.of();
    }

    default public void generateRegion(int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ, BlockWriter out) {
        for (int x = minX; x < minX + sizeX; ++x) {
            for (int z = minZ; z < minZ + sizeZ; ++z) {
                for (int y = minY; y < minY + sizeY; ++y) {
                    BlockState state = this.stateAt(x, y, z);
                    if (state.isAir()) continue;
                    out.set(x, y, z, state);
                }
            }
        }
    }
}


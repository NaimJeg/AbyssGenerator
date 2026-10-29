/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import com.jfyx2025.chunkcutter.cubic.terrain.TerrainAlgorithm3D;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class EmptyTerrainSource
implements TerrainAlgorithm3D {
    private final int minY;

    public EmptyTerrainSource(ServerLevel level) {
        this.minY = level.getMinY();
    }

    @Override
    public int heightAt(int x, int z) {
        return this.minY;
    }

    @Override
    public BlockState stateAt(int x, int y, int z) {
        return Blocks.AIR.defaultBlockState();
    }
}


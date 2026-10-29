/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkSource
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.mixin.DistanceManagerAccessor;
import com.jfyx2025.chunkcutter.mixin.ServerChunkCacheAccessor;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;

public final class CubeRange {
    private CubeRange() {
    }

    public static int simulationRadiusBlocks(Level level) {
        ServerChunkCacheAccessor accessor;
        DistanceManager dm;
        ChunkSource cs = level.getChunkSource();
        if (cs instanceof ServerChunkCacheAccessor && (dm = (accessor = (ServerChunkCacheAccessor)cs).chunkcutter$distanceManager()) instanceof DistanceManagerAccessor) {
            DistanceManagerAccessor dmAccessor = (DistanceManagerAccessor)dm;
            return Math.max(1, dmAccessor.chunkcutter$simulationDistance()) * 16;
        }
        return 160;
    }

    public static boolean withinRadius(Level level, double x, double y, double z, int radius) {
        for (Player player : level.players()) {
            double dx = Math.abs(player.getX() - x);
            double dy = Math.abs(player.getY() - y);
            double dz = Math.abs(player.getZ() - z);
            if (!(dx <= (double)radius) || !(dy <= (double)radius) || !(dz <= (double)radius)) continue;
            return true;
        }
        return false;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.NaturalSpawner
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={NaturalSpawner.class})
public abstract class NaturalSpawnerMixin {
    private static final int SPAWN_RANGE = 128;

    @Overwrite
    private static BlockPos getRandomPosWithin(Level level, LevelChunk chunk) {
        int hi;
        int lo;
        ChunkPos pos = chunk.getPos();
        int x = pos.getMinBlockX() + level.random.nextInt(16);
        int z = pos.getMinBlockZ() + level.random.nextInt(16);
        int topEmptyY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
        int minY = level.getMinY();
        int maxY = level.getMaxY();
        Player nearest = level.getNearestPlayer((double)x + 0.5, (double)topEmptyY, (double)z + 0.5, -1.0, false);
        int baseY = nearest != null ? (int)nearest.getY() : topEmptyY - 1;
        int range = 128;
        CubeLoadManager clm = null;
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (level.getChunkSource() instanceof CubeSource) {
                clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)serverLevel.dimension());
                range = Math.max(16, clm.simulationDistance() * 16);
            }
        }
        if ((lo = Mth.clamp((int)(baseY - range), (int)minY, (int)maxY)) > (hi = Mth.clamp((int)(baseY + range), (int)minY, (int)maxY))) {
            lo = hi;
        }
        int y = Mth.randomBetweenInclusive((RandomSource)level.random, (int)lo, (int)hi);
        if (clm != null && !clm.ticking(x >> 4, y >> 4, z >> 4)) {
            y = Mth.clamp((int)baseY, (int)lo, (int)hi);
        }
        return new BlockPos(x, y, z);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import com.jfyx2025.chunkcutter.cubic.terrain.BlockWriter;
import com.jfyx2025.chunkcutter.cubic.terrain.ChunkGeneratorBackend;
import com.jfyx2025.chunkcutter.cubic.terrain.EmptyTerrainSource;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainAlgorithm3D;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainCacheControl;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class TerrainSources {
    public static final int FORMAT_VERSION = 13;
    private static final Map<ResourceKey<Level>, TerrainAlgorithm3D> ALGORITHMS = new ConcurrentHashMap<ResourceKey<Level>, TerrainAlgorithm3D>();

    private TerrainSources() {
    }

    public static TerrainAlgorithm3D forLevel(ServerLevel level) {
        return ALGORITHMS.computeIfAbsent((ResourceKey<Level>)level.dimension(), key -> {
            ServerChunkCache patt0$temp = level.getChunkSource();
            if (patt0$temp instanceof ServerChunkCache) {
                ServerChunkCache chunkCache = patt0$temp;
                return new ChunkGeneratorBackend(level, chunkCache.getGenerator());
            }
            return new EmptyTerrainSource(level);
        });
    }

    public static void fillRegion(ServerLevel level, int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ, BlockWriter out) {
        TerrainSources.forLevel(level).generateRegion(minX, minY, minZ, sizeX, sizeY, sizeZ, out);
    }

    public static Holder<Biome> biomeAt(ServerLevel level, int x, int y, int z) {
        return TerrainSources.forLevel(level).biomeAt(x, y, z);
    }

    public static List<CompoundTag> blockEntitiesAt(ServerLevel level, int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ) {
        return TerrainSources.forLevel(level).blockEntitiesAt(minX, minY, minZ, sizeX, sizeY, sizeZ);
    }

    public static int version(ServerLevel level) {
        return 13;
    }

    public static void clearColumn(ServerLevel level, int cx, int cz) {
        TerrainAlgorithm3D algorithm = ALGORITHMS.get(level.dimension());
        if (algorithm instanceof TerrainCacheControl) {
            TerrainCacheControl cache = (TerrainCacheControl)((Object)algorithm);
            cache.clearColumn(cx, cz);
        }
    }

    public static void beginShutdown() {
        for (TerrainAlgorithm3D algorithm : ALGORITHMS.values()) {
            if (!(algorithm instanceof TerrainCacheControl)) continue;
            TerrainCacheControl cache = (TerrainCacheControl)((Object)algorithm);
            cache.beginShutdown();
        }
    }

    public static void clear() {
        ALGORITHMS.clear();
    }
}


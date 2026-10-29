/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.PalettedContainerRO
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.SectionLoadMetrics;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainSources;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public final class SectionFiller {
    private SectionFiller() {
    }

    public static Filled fill(ServerLevel level, CloPos clo) {
        long started = System.nanoTime();
        int minX = clo.x() << 4;
        int minY = clo.y() << 4;
        int minZ = clo.z() << 4;
        PalettedContainerFactory factory = level.palettedContainerFactory();
        PalettedContainer biomes = factory.createForBiomes();
        LevelChunkSection section = new LevelChunkSection(factory.createForBlockStates(), (PalettedContainerRO)biomes);
        TerrainSources.fillRegion(level, minX, minY, minZ, 16, 16, 16, (x, y, z, state) -> section.setBlockState(x & 0xF, y & 0xF, z & 0xF, state));
        long biomeStarted = System.nanoTime();
        for (int qx = 0; qx < 4; ++qx) {
            for (int qy = 0; qy < 4; ++qy) {
                for (int qz = 0; qz < 4; ++qz) {
                    Holder biome = TerrainSources.biomeAt(level, minX + qx * 4, minY + qy * 4, minZ + qz * 4);
                    biomes.set(qx, qy, qz, (Object)(biome == null ? factory.defaultBiome() : biome));
                }
            }
        }
        long blockEntityStarted = System.nanoTime();
        List<CompoundTag> blockEntities = TerrainSources.blockEntitiesAt(level, minX, minY, minZ, 16, 16, 16);
        SectionLoadMetrics.recordBiome(blockEntityStarted - biomeStarted);
        SectionLoadMetrics.recordBlockEntities(System.nanoTime() - blockEntityStarted);
        SectionLoadMetrics.recordFill(System.nanoTime() - started);
        return new Filled(section, blockEntities);
    }

    public record Filled(LevelChunkSection section, List<CompoundTag> blockEntities) {
    }
}


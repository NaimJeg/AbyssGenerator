/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket$ChunkBiomeData
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package com.jfyx2025.chunkcutter.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={ClientboundChunksBiomesPacket.ChunkBiomeData.class})
public abstract class ClientboundChunksBiomesPacketMixin {
    @Overwrite
    private static int calculateChunkSize(LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        int total = sections.length;
        for (LevelChunkSection section : sections) {
            if (section == null || section.hasOnlyAir()) continue;
            total += section.getBiomes().getSerializedSize();
        }
        return total;
    }

    @Overwrite
    public static void extractChunkData(FriendlyByteBuf buffer, LevelChunk chunk) {
        LevelChunkSection[] sections;
        for (LevelChunkSection section : sections = chunk.getSections()) {
            buffer.writeBoolean(section != null && !section.hasOnlyAir());
        }
        for (LevelChunkSection section : sections) {
            if (section == null || section.hasOnlyAir()) continue;
            section.getBiomes().write(buffer);
        }
        if (buffer.writerIndex() != buffer.capacity()) {
            throw new IllegalStateException("Didn't fill biome buffer: expected " + buffer.capacity() + " bytes, got " + buffer.writerIndex());
        }
    }
}


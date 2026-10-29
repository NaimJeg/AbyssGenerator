/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityInfo
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityTagOutput
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ClientboundLevelChunkPacketData.class})
public abstract class ClientboundLevelChunkPacketDataMixin {
    @Shadow
    private static StreamCodec<ByteBuf, Map<Heightmap.Types, long[]>> HEIGHTMAPS_STREAM_CODEC;
    @Shadow
    private static int TWO_MEGABYTES;
    @Shadow
    private Map<Heightmap.Types, long[]> heightmaps;
    @Shadow
    private byte[] buffer;
    @Shadow
    private List<ClientboundLevelChunkPacketData.BlockEntityInfo> blockEntitiesData;

    @Overwrite
    public void write(RegistryFriendlyByteBuf output) {
        HEIGHTMAPS_STREAM_CODEC.encode((Object)output, this.heightmaps);
        output.writeVarInt(this.buffer.length);
        output.writeBytes(this.buffer);
        ClientboundLevelChunkPacketData.BlockEntityInfo.LIST_STREAM_CODEC.encode((Object)output, this.blockEntitiesData);
    }

    @Overwrite
    public static int calculateChunkSize(LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        int total = sections.length;
        for (LevelChunkSection section : sections) {
            if (section == null || section.hasOnlyAir()) continue;
            total += section.getSerializedSize();
        }
        return total;
    }

    @Overwrite
    public ByteBuf getWriteBuffer() {
        ByteBuf buffer = Unpooled.wrappedBuffer((byte[])this.buffer);
        buffer.writerIndex(0);
        return buffer;
    }

    @Overwrite
    public static void extractChunkData(FriendlyByteBuf buffer, LevelChunk chunk) {
        LevelChunkSection[] sections;
        for (LevelChunkSection section : sections = chunk.getSections()) {
            buffer.writeBoolean(section != null && !section.hasOnlyAir());
        }
        for (LevelChunkSection section : sections) {
            if (section == null || section.hasOnlyAir()) continue;
            section.write(buffer);
        }
        if (buffer.writerIndex() != buffer.capacity()) {
            throw new IllegalStateException("Didn't fill chunk buffer: expected " + buffer.capacity() + " bytes, got " + buffer.writerIndex());
        }
    }

    @Overwrite
    public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int x, int z) {
        return output -> this.getBlockEntitiesTags((ClientboundLevelChunkPacketData.BlockEntityTagOutput)output, x, z);
    }

    @Overwrite
    public void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput output, int x, int z) {
        int baseX = 16 * x;
        int baseZ = 16 * z;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (ClientboundLevelChunkPacketData.BlockEntityInfo data : this.blockEntitiesData) {
            int unpackedX = baseX + SectionPos.sectionRelative((int)(data.packedXZ >> 4));
            int unpackedZ = baseZ + SectionPos.sectionRelative((int)data.packedXZ);
            pos.set(unpackedX, data.y, unpackedZ);
            output.accept((BlockPos)pos, data.type, data.tag);
        }
    }

    @Overwrite
    public FriendlyByteBuf getReadBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])this.buffer));
    }

    @Overwrite
    public Map<Heightmap.Types, long[]> getHeightmaps() {
        return this.heightmaps;
    }
}


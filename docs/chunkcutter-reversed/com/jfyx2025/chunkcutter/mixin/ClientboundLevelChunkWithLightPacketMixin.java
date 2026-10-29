/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketType
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket
 *  net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData
 *  net.minecraft.network.protocol.game.GamePacketTypes
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.GamePacketTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ClientboundLevelChunkWithLightPacket.class})
public abstract class ClientboundLevelChunkWithLightPacketMixin
implements Packet<ClientGamePacketListener> {
    @Shadow
    public static StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkWithLightPacket> STREAM_CODEC;
    @Shadow
    private int x;
    @Shadow
    private int z;
    @Shadow
    private ClientboundLevelChunkPacketData chunkData;
    @Shadow
    private ClientboundLightUpdatePacketData lightData;

    @Overwrite
    public void write(RegistryFriendlyByteBuf output) {
        output.writeInt(this.x);
        output.writeInt(this.z);
        this.chunkData.write(output);
        this.lightData.write((FriendlyByteBuf)output);
    }

    @Overwrite
    public PacketType<ClientboundLevelChunkWithLightPacket> type() {
        return GamePacketTypes.CLIENTBOUND_LEVEL_CHUNK_WITH_LIGHT;
    }

    @Overwrite
    public void handle(ClientGamePacketListener listener) {
        listener.handleLevelChunkWithLight((ClientboundLevelChunkWithLightPacket)this);
    }

    @Overwrite
    public int getX() {
        return this.x;
    }

    @Overwrite
    public int getZ() {
        return this.z;
    }

    @Overwrite
    public ClientboundLevelChunkPacketData getChunkData() {
        return this.chunkData;
    }

    @Overwrite
    public ClientboundLightUpdatePacketData getLightData() {
        return this.lightData;
    }
}


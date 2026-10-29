/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.Identifier
 */
package com.jfyx2025.chunkcutter.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SectionsPayload(Identifier dimension, List<SectionEntry> entries) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<SectionsPayload> TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"chunkcutter", (String)"sections"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SectionsPayload> CODEC = new StreamCodec<RegistryFriendlyByteBuf, SectionsPayload>(){

        public SectionsPayload decode(RegistryFriendlyByteBuf buf) {
            Identifier dimension = buf.readIdentifier();
            int n = buf.readVarInt();
            ArrayList<SectionEntry> list = new ArrayList<SectionEntry>(n);
            for (int i = 0; i < n; ++i) {
                long cubePos = buf.readLong();
                byte[] data = buf.readByteArray();
                byte[] blockLight = SectionsPayload.readNullable(buf);
                byte[] skyLight = SectionsPayload.readNullable(buf);
                int beCount = buf.readVarInt();
                ArrayList<CompoundTag> blockEntities = new ArrayList<CompoundTag>(beCount);
                for (int j = 0; j < beCount; ++j) {
                    CompoundTag tag = buf.readNbt();
                    if (tag == null) continue;
                    blockEntities.add(tag);
                }
                list.add(new SectionEntry(cubePos, data, blockLight, skyLight, blockEntities));
            }
            return new SectionsPayload(dimension, list);
        }

        public void encode(RegistryFriendlyByteBuf buf, SectionsPayload payload) {
            buf.writeIdentifier(payload.dimension());
            buf.writeVarInt(payload.entries().size());
            for (SectionEntry entry : payload.entries()) {
                buf.writeLong(entry.cubePos());
                buf.writeByteArray(entry.data());
                SectionsPayload.writeNullable(buf, entry.blockLight());
                SectionsPayload.writeNullable(buf, entry.skyLight());
                buf.writeVarInt(entry.blockEntities().size());
                for (CompoundTag tag : entry.blockEntities()) {
                    buf.writeNbt((Tag)tag);
                }
            }
        }
    };

    private static byte[] readNullable(RegistryFriendlyByteBuf buf) {
        return buf.readBoolean() ? buf.readByteArray() : null;
    }

    private static void writeNullable(RegistryFriendlyByteBuf buf, byte[] value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeByteArray(value);
        }
    }

    public CustomPacketPayload.Type<SectionsPayload> type() {
        return TYPE;
    }

    public record SectionEntry(long cubePos, byte[] data, byte[] blockLight, byte[] skyLight, List<CompoundTag> blockEntities) {
    }
}


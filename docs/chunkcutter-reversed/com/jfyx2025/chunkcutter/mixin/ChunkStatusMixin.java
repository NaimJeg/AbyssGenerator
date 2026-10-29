/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.chunk.status.ChunkType
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkStatus.class})
public abstract class ChunkStatusMixin {
    @Shadow
    public static int MAX_STRUCTURE_DISTANCE;
    @Shadow
    private static EnumSet<Heightmap.Types> WORLDGEN_HEIGHTMAPS;
    @Shadow
    public static EnumSet<Heightmap.Types> FINAL_HEIGHTMAPS;
    @Shadow
    public static ChunkStatus EMPTY;
    @Shadow
    public static ChunkStatus STRUCTURE_STARTS;
    @Shadow
    public static ChunkStatus STRUCTURE_REFERENCES;
    @Shadow
    public static ChunkStatus BIOMES;
    @Shadow
    public static ChunkStatus NOISE;
    @Shadow
    public static ChunkStatus SURFACE;
    @Shadow
    public static ChunkStatus CARVERS;
    @Shadow
    public static ChunkStatus FEATURES;
    @Shadow
    public static ChunkStatus INITIALIZE_LIGHT;
    @Shadow
    public static ChunkStatus LIGHT;
    @Shadow
    public static ChunkStatus SPAWN;
    @Shadow
    public static ChunkStatus FULL;
    @Shadow
    public static Codec<ChunkStatus> CODEC;
    @Shadow
    private int index;
    @Shadow
    private ChunkStatus parent;
    @Shadow
    private ChunkType chunkType;
    @Shadow
    private EnumSet<Heightmap.Types> heightmapsAfter;

    @Overwrite
    public static ChunkStatus register(String name, @Nullable ChunkStatus parent, EnumSet<Heightmap.Types> heightmaps, ChunkType chunkType) {
        return (ChunkStatus)Registry.register((Registry)BuiltInRegistries.CHUNK_STATUS, (String)name, (Object)new ChunkStatus(parent, heightmaps, chunkType));
    }

    @Overwrite
    public static List<ChunkStatus> getStatusList() {
        ChunkStatus status;
        ArrayList list = Lists.newArrayList();
        for (status = FULL; status.getParent() != status; status = status.getParent()) {
            list.add(status);
        }
        list.add(status);
        Collections.reverse(list);
        return list;
    }

    @Overwrite
    @VisibleForTesting
    public int getIndex() {
        return this.index;
    }

    @Overwrite
    public ChunkStatus getParent() {
        return this.parent;
    }

    @Overwrite
    public ChunkType getChunkType() {
        return this.chunkType;
    }

    @Overwrite
    public static ChunkStatus byName(String key) {
        return (ChunkStatus)BuiltInRegistries.CHUNK_STATUS.getValue(Identifier.tryParse((String)key));
    }

    @Overwrite
    public EnumSet<Heightmap.Types> heightmapsAfter() {
        return this.heightmapsAfter;
    }

    @Overwrite
    public boolean isOrAfter(ChunkStatus step) {
        return this.getIndex() >= step.getIndex();
    }

    @Overwrite
    public boolean isAfter(ChunkStatus step) {
        return this.getIndex() > step.getIndex();
    }

    @Overwrite
    public boolean isOrBefore(ChunkStatus step) {
        return this.getIndex() <= step.getIndex();
    }

    @Overwrite
    public boolean isBefore(ChunkStatus step) {
        return this.getIndex() < step.getIndex();
    }

    @Overwrite
    public static ChunkStatus max(ChunkStatus a, ChunkStatus b) {
        return a.isAfter(b) ? a : b;
    }

    @Overwrite
    public String toString() {
        return this.getName();
    }

    @Overwrite
    public String getName() {
        return BuiltInRegistries.CHUNK_STATUS.getKey((Object)((ChunkStatus)this)).toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.world.level.TicketStorage
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.jfyx2025.chunkcutter.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ServerChunkCache.class})
public interface ServerChunkCacheAccessor {
    @Accessor(value="distanceManager")
    public DistanceManager chunkcutter$distanceManager();

    @Accessor(value="chunkMap")
    public ChunkMap chunkcutter$chunkMap();

    @Accessor(value="ticketStorage")
    public TicketStorage chunkcutter$ticketStorage();
}


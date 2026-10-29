/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.chunk.storage.SectionStorage
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.jfyx2025.chunkcutter.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SectionStorage.class})
public interface SectionStorageAccessor {
    @Accessor(value="storage")
    public Long2ObjectMap<Optional<CompoundTag>> chunkcutter$storage();

    @Accessor(value="loadedChunks")
    public LongSet chunkcutter$loadedChunks();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter
 *  net.minecraft.world.level.chunk.storage.SectionStorage
 *  net.minecraft.world.level.chunk.storage.SimpleRegionStorage
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.mixin.SectionStorageAccessor;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;

public final class SectionStore
extends SectionStorage<CompoundTag, CompoundTag> {
    public SectionStore(SimpleRegionStorage storage, RegistryAccess registryAccess, ChunkIOErrorReporter errorReporter, LevelHeightAccessor heightAccessor) {
        super(storage, CompoundTag.CODEC, tag -> tag, (tag, dirty) -> tag, dirty -> new CompoundTag(), registryAccess, errorReporter, heightAccessor);
    }

    public Optional<CompoundTag> getSection(long sectionPos) {
        return this.getOrLoad(sectionPos);
    }

    public CompoundTag peekSection(long sectionPos) {
        Optional existing = this.get(sectionPos);
        return existing == null ? null : (CompoundTag)existing.orElse(null);
    }

    public void putSection(long sectionPos, CompoundTag tag) {
        CompoundTag slot = (CompoundTag)this.getOrCreate(sectionPos);
        for (String key : new ArrayList(slot.keySet())) {
            slot.remove(key);
        }
        for (String key : tag.keySet()) {
            slot.put(key, tag.get(key));
        }
        this.setDirty(sectionPos);
    }

    public void putSectionDirect(long sectionPos, CompoundTag tag) {
        SectionStorageAccessor accessor = (SectionStorageAccessor)((Object)this);
        accessor.chunkcutter$storage().put(sectionPos, Optional.of(tag));
        accessor.chunkcutter$loadedChunks().add(ChunkPos.pack((int)SectionPos.x((long)sectionPos), (int)SectionPos.z((long)sectionPos)));
        this.setDirty(sectionPos);
    }

    public void tickStore(BooleanSupplier haveTime) {
        this.tick(haveTime);
    }

    public void flushStore() {
        this.flushAll();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.util.VisibleForDebug
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntityInLevelCallback
 *  net.minecraft.world.level.entity.EntityLookup
 *  net.minecraft.world.level.entity.EntitySection
 *  net.minecraft.world.level.entity.EntitySectionStorage
 *  net.minecraft.world.level.entity.LevelCallback
 *  net.minecraft.world.level.entity.LevelEntityGetter
 *  net.minecraft.world.level.entity.TransientEntitySectionManager
 *  net.minecraft.world.level.entity.TransientEntitySectionManager$Callback
 *  net.minecraft.world.level.entity.Visibility
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={TransientEntitySectionManager.class})
public abstract class TransientEntitySectionManagerMixin<T extends EntityAccess> {
    @Shadow
    private LevelCallback<T> callbacks;
    @Shadow
    private EntityLookup<T> entityStorage;
    @Shadow
    private EntitySectionStorage<T> sectionStorage;
    @Shadow
    private LongSet tickingChunks;
    @Shadow
    private LevelEntityGetter<T> entityGetter;

    @Overwrite
    public void startTicking(ChunkPos pos) {
        long chunkKey = pos.pack();
        this.tickingChunks.add(chunkKey);
        this.sectionStorage.getExistingSectionsInChunk(chunkKey).forEach(section -> {
            Visibility previousStatus = section.updateChunkStatus(Visibility.TICKING);
            if (!previousStatus.isTicking()) {
                section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(arg_0 -> this.callbacks.onTickingStart(arg_0));
            }
        });
    }

    @Overwrite
    public void stopTicking(ChunkPos pos) {
        long chunkKey = pos.pack();
        this.tickingChunks.remove(chunkKey);
        this.sectionStorage.getExistingSectionsInChunk(chunkKey).forEach(section -> {
            Visibility previousStatus = section.updateChunkStatus(Visibility.TRACKED);
            if (previousStatus.isTicking()) {
                section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(arg_0 -> this.callbacks.onTickingEnd(arg_0));
            }
        });
    }

    @Overwrite
    public LevelEntityGetter<T> getEntityGetter() {
        return this.entityGetter;
    }

    @Overwrite
    public void addEntity(T entity) {
        this.entityStorage.add(entity);
        long sectionKey = SectionPos.asLong((BlockPos)entity.blockPosition());
        EntitySection entitySection = this.sectionStorage.getOrCreateSection(sectionKey);
        entitySection.add(entity);
        TransientEntitySectionManager transientEntitySectionManager = (TransientEntitySectionManager)this;
        Objects.requireNonNull(transientEntitySectionManager);
        entity.setLevelCallback((EntityInLevelCallback)new TransientEntitySectionManager.Callback(transientEntitySectionManager, entity, sectionKey, entitySection));
        this.callbacks.onCreated(entity);
        this.callbacks.onTrackingStart(entity);
        if (entity.isAlwaysTicking() || entitySection.getStatus().isTicking()) {
            this.callbacks.onTickingStart(entity);
        }
    }

    @Overwrite
    @VisibleForDebug
    public int count() {
        return this.entityStorage.count();
    }

    @Overwrite
    public void removeSectionIfEmpty(long sectionPos, EntitySection<T> section) {
        if (section.isEmpty()) {
            this.sectionStorage.remove(sectionPos);
        }
    }

    @Overwrite
    @VisibleForDebug
    public String gatherStats() {
        return this.entityStorage.count() + "," + this.sectionStorage.count() + "," + this.tickingChunks.size();
    }
}


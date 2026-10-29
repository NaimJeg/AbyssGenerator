/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LightChunkGetter
 *  net.minecraft.world.level.lighting.DataLayerStorageMap
 *  net.minecraft.world.level.lighting.LayerLightSectionStorage
 *  net.minecraft.world.level.lighting.LayerLightSectionStorage$SectionState
 *  net.minecraft.world.level.lighting.LayerLightSectionStorage$SectionType
 *  net.minecraft.world.level.lighting.LightEngine
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LayerLightSectionStorage.class})
public abstract class LayerLightSectionStorageMixin<M extends DataLayerStorageMap<M>> {
    @Shadow
    private LightLayer layer;
    @Shadow
    protected LightChunkGetter chunkSource;
    @Shadow
    protected Long2ByteMap sectionStates;
    @Shadow
    private LongSet columnsWithSources;
    @Shadow
    protected volatile M visibleSectionData;
    @Shadow
    protected M updatingSectionData;
    @Shadow
    protected LongSet changedSections;
    @Shadow
    protected LongSet sectionsAffectedByLightUpdates;
    @Shadow
    protected Long2ObjectMap<DataLayer> queuedSections;
    @Shadow
    private LongSet columnsToRetainQueuedDataFor;
    @Shadow
    private LongSet toRemove;
    @Shadow
    protected volatile boolean hasInconsistencies;

    @Overwrite
    public boolean storingLightForSection(long sectionNode) {
        return this.getDataLayer(sectionNode, true) != null;
    }

    @Overwrite
    public @Nullable DataLayer getDataLayer(long sectionNode, boolean updating) {
        return this.getDataLayer(updating ? this.updatingSectionData : this.visibleSectionData, sectionNode);
    }

    @Overwrite
    public @Nullable DataLayer getDataLayer(M sections, long sectionNode) {
        return sections.getLayer(sectionNode);
    }

    @Overwrite
    public @Nullable DataLayer getDataLayerToWrite(long sectionNode) {
        DataLayer dataLayer = this.updatingSectionData.getLayer(sectionNode);
        if (dataLayer == null) {
            return null;
        }
        if (this.changedSections.add(sectionNode)) {
            dataLayer = dataLayer.copy();
            this.updatingSectionData.setLayer(sectionNode, dataLayer);
            this.updatingSectionData.clearCache();
        }
        return dataLayer;
    }

    @Overwrite
    public @Nullable DataLayer getDataLayerData(long sectionNode) {
        DataLayer layer = (DataLayer)this.queuedSections.get(sectionNode);
        return layer != null ? layer : this.getDataLayer(sectionNode, false);
    }

    @Shadow
    public abstract int getLightValue(long var1);

    public int getStoredLevel(long blockNode) {
        long sectionNode = SectionPos.blockToSection((long)blockNode);
        DataLayer layer = this.getDataLayer(sectionNode, true);
        if (layer == null) {
            return 0;
        }
        return layer.get(SectionPos.sectionRelative((int)BlockPos.getX((long)blockNode)), SectionPos.sectionRelative((int)BlockPos.getY((long)blockNode)), SectionPos.sectionRelative((int)BlockPos.getZ((long)blockNode)));
    }

    @Overwrite
    public void setStoredLevel(long blockNode, int level) {
        long sectionNode = SectionPos.blockToSection((long)blockNode);
        DataLayer layer = this.updatingSectionData.getLayer(sectionNode);
        if (layer == null) {
            layer = this.createDataLayer(sectionNode);
            this.updatingSectionData.setLayer(sectionNode, layer);
            this.changedSections.add(sectionNode);
            this.updatingSectionData.clearCache();
        } else if (this.changedSections.add(sectionNode)) {
            layer = layer.copy();
            this.updatingSectionData.setLayer(sectionNode, layer);
            this.updatingSectionData.clearCache();
        }
        layer.set(SectionPos.sectionRelative((int)BlockPos.getX((long)blockNode)), SectionPos.sectionRelative((int)BlockPos.getY((long)blockNode)), SectionPos.sectionRelative((int)BlockPos.getZ((long)blockNode)), level);
        SectionPos.aroundAndAtBlockPos((long)blockNode, arg_0 -> ((LongSet)this.sectionsAffectedByLightUpdates).add(arg_0));
    }

    @Overwrite
    public void markSectionAndNeighborsAsAffected(long sectionNode) {
        int x = SectionPos.x((long)sectionNode);
        int y = SectionPos.y((long)sectionNode);
        int z = SectionPos.z((long)sectionNode);
        for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
            for (int offsetX = -1; offsetX <= 1; ++offsetX) {
                for (int offsetY = -1; offsetY <= 1; ++offsetY) {
                    this.sectionsAffectedByLightUpdates.add(SectionPos.asLong((int)(x + offsetX), (int)(y + offsetY), (int)(z + offsetZ)));
                }
            }
        }
    }

    @Overwrite
    public DataLayer createDataLayer(long sectionNode) {
        DataLayer queuedLayer = (DataLayer)this.queuedSections.get(sectionNode);
        return queuedLayer != null ? queuedLayer : new DataLayer();
    }

    @Overwrite
    public boolean hasInconsistencies() {
        return this.hasInconsistencies;
    }

    @Overwrite
    public void markNewInconsistencies(LightEngine<M, ?> engine) {
        if (this.hasInconsistencies) {
            long node;
            this.hasInconsistencies = false;
            LongIterator longIterator = this.toRemove.iterator();
            while (longIterator.hasNext()) {
                node = (Long)longIterator.next();
                DataLayer queued = (DataLayer)this.queuedSections.remove(node);
                DataLayer stored = this.updatingSectionData.removeLayer(node);
                if (!this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode((long)node))) continue;
                if (queued != null) {
                    this.queuedSections.put(node, (Object)queued);
                    continue;
                }
                if (stored == null) continue;
                this.queuedSections.put(node, (Object)stored);
            }
            this.updatingSectionData.clearCache();
            longIterator = this.toRemove.iterator();
            while (longIterator.hasNext()) {
                node = (Long)longIterator.next();
                this.onNodeRemoved(node);
                this.changedSections.add(node);
            }
            this.toRemove.clear();
            ObjectIterator iterator = Long2ObjectMaps.fastIterator(this.queuedSections);
            while (iterator.hasNext()) {
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
                long sectionNode = entry.getLongKey();
                if (!this.storingLightForSection(sectionNode)) continue;
                DataLayer data = (DataLayer)entry.getValue();
                if (this.updatingSectionData.getLayer(sectionNode) != data) {
                    this.updatingSectionData.setLayer(sectionNode, data);
                    this.changedSections.add(sectionNode);
                }
                iterator.remove();
            }
            this.updatingSectionData.clearCache();
        }
    }

    @Overwrite
    public void onNodeAdded(long sectionNode) {
    }

    @Overwrite
    public void onNodeRemoved(long sectionNode) {
    }

    @Overwrite
    public void setLightEnabled(long zeroNode, boolean enable) {
        if (enable) {
            this.columnsWithSources.add(zeroNode);
        } else {
            this.columnsWithSources.remove(zeroNode);
        }
    }

    @Overwrite
    public boolean lightOnInSection(long sectionNode) {
        long zeroNode = SectionPos.getZeroNode((long)sectionNode);
        return this.columnsWithSources.contains(zeroNode);
    }

    @Overwrite
    public boolean lightOnInColumn(long sectionZeroNode) {
        return this.columnsWithSources.contains(sectionZeroNode);
    }

    @Overwrite
    public void retainData(long zeroNode, boolean retain) {
        if (retain) {
            this.columnsToRetainQueuedDataFor.add(zeroNode);
        } else {
            this.columnsToRetainQueuedDataFor.remove(zeroNode);
        }
    }

    @Overwrite
    public void queueSectionData(long sectionNode, @Nullable DataLayer data) {
        if (data != null) {
            this.queuedSections.put(sectionNode, (Object)data);
            this.hasInconsistencies = true;
        } else {
            this.queuedSections.remove(sectionNode);
        }
    }

    @Overwrite
    public void updateSectionStatus(long sectionNode, boolean sectionEmpty) {
        byte newState;
        byte state = this.sectionStates.get(sectionNode);
        if (state != (newState = LayerLightSectionStorage.SectionState.hasData((byte)state, (!sectionEmpty ? 1 : 0) != 0))) {
            this.putSectionState(sectionNode, newState);
            int neighborIncrement = sectionEmpty ? -1 : 1;
            for (int offsetX = -1; offsetX <= 1; ++offsetX) {
                for (int offsetY = -1; offsetY <= 1; ++offsetY) {
                    for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                        if (offsetX == 0 && offsetY == 0 && offsetZ == 0) continue;
                        long neighborNode = SectionPos.offset((long)sectionNode, (int)offsetX, (int)offsetY, (int)offsetZ);
                        byte neighborState = this.sectionStates.get(neighborNode);
                        this.putSectionState(neighborNode, LayerLightSectionStorage.SectionState.neighborCount((byte)neighborState, (int)(LayerLightSectionStorage.SectionState.neighborCount((byte)neighborState) + neighborIncrement)));
                    }
                }
            }
        }
    }

    @Overwrite
    public void putSectionState(long sectionNode, byte state) {
        if (state != 0) {
            if (this.sectionStates.put(sectionNode, state) == 0) {
                this.initializeSection(sectionNode);
            }
        } else if (this.sectionStates.remove(sectionNode) != 0) {
            this.removeSection(sectionNode);
        }
    }

    @Overwrite
    public void initializeSection(long sectionNode) {
        if (!this.toRemove.remove(sectionNode)) {
            this.updatingSectionData.setLayer(sectionNode, this.createDataLayer(sectionNode));
            this.changedSections.add(sectionNode);
            this.onNodeAdded(sectionNode);
            this.markSectionAndNeighborsAsAffected(sectionNode);
            this.hasInconsistencies = true;
        }
    }

    @Overwrite
    public void removeSection(long sectionNode) {
        this.toRemove.add(sectionNode);
        this.hasInconsistencies = true;
    }

    @Overwrite
    public void swapSectionMap() {
        if (!this.changedSections.isEmpty()) {
            DataLayerStorageMap copy = this.updatingSectionData.copy();
            copy.disableCache();
            this.visibleSectionData = copy;
            this.changedSections.clear();
        }
        if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
            LongIterator iterator = this.sectionsAffectedByLightUpdates.iterator();
            while (iterator.hasNext()) {
                long sectionNode = iterator.nextLong();
                this.chunkSource.onLightUpdate(this.layer, SectionPos.of((long)sectionNode));
            }
            this.sectionsAffectedByLightUpdates.clear();
        }
    }

    @Overwrite
    public LayerLightSectionStorage.SectionType getDebugSectionType(long sectionNode) {
        return LayerLightSectionStorage.SectionState.type((byte)this.sectionStates.get(sectionNode));
    }
}


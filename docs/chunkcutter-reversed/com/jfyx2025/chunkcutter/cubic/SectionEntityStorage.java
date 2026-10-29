/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.ProblemReporter
 *  net.minecraft.util.ProblemReporter$ScopedCollector
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntitySpawnReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.entity.ChunkEntities
 *  net.minecraft.world.level.entity.EntityPersistentStorage
 *  net.minecraft.world.level.storage.TagValueInput
 *  net.minecraft.world.level.storage.TagValueOutput
 *  net.minecraft.world.level.storage.ValueInput
 *  net.minecraft.world.level.storage.ValueInput$ValueInputList
 *  net.minecraft.world.level.storage.ValueOutput
 *  org.slf4j.Logger
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CubeEntityStore;
import com.jfyx2025.chunkcutter.cubic.VerticalBounds;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public final class SectionEntityStorage
implements EntityPersistentStorage<Entity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ServerLevel level;
    private final EntityPersistentStorage<Entity> fallback;

    public SectionEntityStorage(ServerLevel level, EntityPersistentStorage<Entity> fallback) {
        this.level = level;
        this.fallback = fallback;
    }

    public ServerLevel level() {
        return this.level;
    }

    public static List<Entity> readSectionEntities(ServerLevel level, int cx, int sy, int cz) {
        CompoundTag tag = CubeEntityStore.readSection((Level)level, sy, cx, cz);
        if (tag == null) {
            return List.of();
        }
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath((ChunkPos)new ChunkPos(cx, cz)), LOGGER);){
            ValueInput root = TagValueInput.create((ProblemReporter)reporter, (HolderLookup.Provider)level.registryAccess(), (CompoundTag)tag);
            ValueInput.ValueInputList list = root.childrenListOrEmpty("Entities");
            List<Entity> list2 = EntityType.loadEntitiesRecursive((ValueInput.ValueInputList)list, (Level)level, (EntitySpawnReason)EntitySpawnReason.LOAD).toList();
            return list2;
        }
    }

    public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos pos) {
        if (!CubeEntityStore.hasStored((Level)this.level, pos)) {
            return this.fallback.loadEntities(pos).thenApply(chunkEntities -> {
                if (!chunkEntities.isEmpty()) {
                    this.storeEntities((ChunkEntities<Entity>)chunkEntities);
                }
                return chunkEntities;
            });
        }
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (int sectionY = VerticalBounds.engineMinSection(); sectionY <= VerticalBounds.engineMaxSection(); ++sectionY) {
            CompoundTag tag = CubeEntityStore.readSection((Level)this.level, sectionY, pos.x(), pos.z());
            if (tag == null) continue;
            entities.addAll(this.readEntities(pos, tag));
        }
        return CompletableFuture.completedFuture(new ChunkEntities(pos, entities));
    }

    private List<Entity> readEntities(ChunkPos pos, CompoundTag tag) {
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath((ChunkPos)pos), LOGGER);){
            ValueInput root = TagValueInput.create((ProblemReporter)reporter, (HolderLookup.Provider)this.level.registryAccess(), (CompoundTag)tag);
            ValueInput.ValueInputList list = root.childrenListOrEmpty("Entities");
            List<Entity> list2 = EntityType.loadEntitiesRecursive((ValueInput.ValueInputList)list, (Level)this.level, (EntitySpawnReason)EntitySpawnReason.LOAD).toList();
            return list2;
        }
    }

    public static void storeSectionEntities(ServerLevel level, long sectionKey, List<Entity> entities) {
        int cx = SectionPos.x((long)sectionKey);
        int sy = SectionPos.y((long)sectionKey);
        int cz = SectionPos.z((long)sectionKey);
        ChunkPos pos = new ChunkPos(cx, cz);
        ListTag list = new ListTag();
        if (!entities.isEmpty()) {
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath((ChunkPos)pos), LOGGER);){
                for (Entity entity : entities) {
                    TagValueOutput output;
                    if (!entity.save((ValueOutput)(output = TagValueOutput.createWithContext((ProblemReporter)reporter.forChild(entity.problemPath()), (HolderLookup.Provider)entity.registryAccess())))) continue;
                    list.add((Object)output.buildResult());
                }
            }
        }
        CompoundTag tag = new CompoundTag();
        tag.put("Entities", (Tag)list);
        CubeEntityStore.writeSection((Level)level, sy, cx, cz, tag);
    }

    public void storeEntities(ChunkEntities<Entity> chunk) {
        ChunkPos pos = chunk.getPos();
        TreeMap<Integer, ListTag> bySection = new TreeMap<Integer, ListTag>();
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath((ChunkPos)pos), LOGGER);){
            for (Entity entity : chunk.getEntities().toList()) {
                int sectionY = SectionPos.blockToSectionCoord((int)entity.getBlockY());
                ListTag list = bySection.computeIfAbsent(sectionY, k -> new ListTag());
                TagValueOutput output = TagValueOutput.createWithContext((ProblemReporter)reporter.forChild(entity.problemPath()), (HolderLookup.Provider)entity.registryAccess());
                if (!entity.save((ValueOutput)output)) continue;
                list.add((Object)output.buildResult());
            }
        }
        for (Map.Entry entry : bySection.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("Entities", (Tag)entry.getValue());
            CubeEntityStore.writeSection((Level)this.level, (Integer)entry.getKey(), pos.x(), pos.z(), tag);
        }
    }

    public void flush(boolean flushStorage) {
        CubeEntityStore.flush();
        this.fallback.flush(flushStorage);
    }

    public void close() throws IOException {
        this.fallback.close();
    }
}


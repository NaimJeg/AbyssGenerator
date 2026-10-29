/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.util.CsvOutput
 *  net.minecraft.util.VisibleForDebug
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.entity.ChunkEntities
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntityInLevelCallback
 *  net.minecraft.world.level.entity.EntityLookup
 *  net.minecraft.world.level.entity.EntityPersistentStorage
 *  net.minecraft.world.level.entity.EntitySection
 *  net.minecraft.world.level.entity.EntitySectionStorage
 *  net.minecraft.world.level.entity.LevelCallback
 *  net.minecraft.world.level.entity.LevelEntityGetter
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager$Callback
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager$ChunkLoadStatus
 *  net.minecraft.world.level.entity.Visibility
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.collect.ImmutableList;
import com.jfyx2025.chunkcutter.cubic.SectionEntityController;
import com.jfyx2025.chunkcutter.cubic.SectionEntityStorage;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={PersistentEntitySectionManager.class})
public abstract class PersistentEntitySectionManagerMixin<T extends EntityAccess>
implements AutoCloseable,
SectionEntityController {
    @Shadow
    private Set<UUID> knownUuids;
    @Shadow
    private LevelCallback<T> callbacks;
    @Shadow
    private EntityPersistentStorage<T> permanentStorage;
    @Shadow
    private EntityLookup<T> visibleEntityStorage;
    @Shadow
    private EntitySectionStorage<T> sectionStorage;
    @Shadow
    private LevelEntityGetter<T> entityGetter;
    @Shadow
    private Long2ObjectMap<Visibility> chunkVisibility;
    @Shadow
    private Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses;
    @Shadow
    private LongSet chunksToUnload;
    @Shadow
    private Queue<ChunkEntities<T>> loadingInbox;
    private final LongSet chunkcutter$loadedEntitySections = new LongOpenHashSet();

    @Overwrite
    public void removeSectionIfEmpty(long sectionPos, EntitySection<T> section) {
        if (section.isEmpty()) {
            this.sectionStorage.remove(sectionPos);
        }
    }

    @Overwrite
    public boolean addEntityUuid(T entity) {
        return this.knownUuids.add(entity.getUUID());
    }

    @Overwrite
    public boolean addNewEntity(T entity) {
        return this.addEntity(entity, false);
    }

    @Overwrite
    public boolean addEntity(T entity, boolean loaded) {
        Visibility status;
        if (!this.addEntityUuid(entity)) {
            return false;
        }
        long sectionKey = SectionPos.asLong((BlockPos)entity.blockPosition());
        EntitySection entitySection = this.sectionStorage.getOrCreateSection(sectionKey);
        entitySection.add(entity);
        this.chunkcutter$loadedEntitySections.add(sectionKey);
        PersistentEntitySectionManager persistentEntitySectionManager = (PersistentEntitySectionManager)this;
        Objects.requireNonNull(persistentEntitySectionManager);
        entity.setLevelCallback((EntityInLevelCallback)new PersistentEntitySectionManager.Callback(persistentEntitySectionManager, entity, sectionKey, entitySection));
        if (!loaded) {
            this.callbacks.onCreated(entity);
        }
        if ((status = PersistentEntitySectionManagerMixin.getEffectiveStatus(entity, entitySection.getStatus())).isAccessible()) {
            this.startTracking(entity);
        }
        if (status.isTicking()) {
            this.startTicking(entity);
        }
        return true;
    }

    @Overwrite
    public static <T extends EntityAccess> Visibility getEffectiveStatus(T entity, Visibility status) {
        return entity.isAlwaysTicking() ? Visibility.TICKING : status;
    }

    @Overwrite
    public boolean isTicking(ChunkPos pos) {
        return ((Visibility)this.chunkVisibility.get(pos.pack())).isTicking();
    }

    @Overwrite
    public void addLegacyChunkEntities(Stream<T> entities) {
        entities.forEach(e -> this.addEntity(e, true));
    }

    @Overwrite
    public void addWorldGenChunkEntities(Stream<T> entities) {
        entities.forEach(e -> this.addEntity(e, false));
    }

    @Overwrite
    public void startTicking(T entity) {
        this.callbacks.onTickingStart(entity);
    }

    @Overwrite
    public void stopTicking(T entity) {
        this.callbacks.onTickingEnd(entity);
    }

    @Overwrite
    public void startTracking(T entity) {
        this.visibleEntityStorage.add(entity);
        this.callbacks.onTrackingStart(entity);
    }

    @Overwrite
    public void stopTracking(T entity) {
        this.callbacks.onTrackingEnd(entity);
        this.visibleEntityStorage.remove(entity);
    }

    @Overwrite
    public void updateChunkStatus(ChunkPos pos, FullChunkStatus fullChunkStatus) {
        Visibility chunkStatus = Visibility.fromFullChunkStatus((FullChunkStatus)fullChunkStatus);
        this.updateChunkStatus(pos, chunkStatus);
    }

    @Overwrite
    public void updateChunkStatus(ChunkPos pos, Visibility chunkStatus) {
        long chunkPosKey = pos.pack();
        if (chunkStatus == Visibility.HIDDEN) {
            this.chunkVisibility.remove(chunkPosKey);
            this.chunksToUnload.add(chunkPosKey);
        } else {
            this.chunkVisibility.put(chunkPosKey, (Object)chunkStatus);
            this.chunksToUnload.remove(chunkPosKey);
            this.ensureChunkQueuedForLoad(chunkPosKey);
        }
        this.sectionStorage.getExistingSectionsInChunk(chunkPosKey).forEach(section -> {
            Visibility previousStatus = section.updateChunkStatus(chunkStatus);
            boolean wasAccessible = previousStatus.isAccessible();
            boolean isAccessible = chunkStatus.isAccessible();
            boolean wasTicking = previousStatus.isTicking();
            boolean isTicking = chunkStatus.isTicking();
            if (wasTicking && !isTicking) {
                section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::stopTicking);
            }
            if (wasAccessible && !isAccessible) {
                section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::stopTracking);
            } else if (!wasAccessible && isAccessible) {
                section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::startTracking);
            }
            if (!wasTicking && isTicking) {
                section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::startTicking);
            }
        });
    }

    @Override
    public void chunkcutter$setSectionStatus(long sectionKey, boolean accessible, boolean ticking) {
        EntitySection section = this.sectionStorage.getSection(sectionKey);
        if (section == null) {
            return;
        }
        Visibility target = !accessible ? Visibility.HIDDEN : (ticking ? Visibility.TICKING : Visibility.TRACKED);
        Visibility previous = section.updateChunkStatus(target);
        boolean wasAccessible = previous.isAccessible();
        boolean isAccessible = target.isAccessible();
        boolean wasTicking = previous.isTicking();
        boolean isTicking = target.isTicking();
        if (wasTicking && !isTicking) {
            section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::stopTicking);
        }
        if (wasAccessible && !isAccessible) {
            section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::stopTracking);
        } else if (!wasAccessible && isAccessible) {
            section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::startTracking);
        }
        if (!wasTicking && isTicking) {
            section.getEntities().filter(e -> !e.isAlwaysTicking()).forEach(this::startTicking);
        }
    }

    @Override
    public void chunkcutter$loadSectionEntities(long sectionKey) {
        if (!this.chunkcutter$loadedEntitySections.add(sectionKey)) {
            return;
        }
        EntityPersistentStorage<T> entityPersistentStorage = this.permanentStorage;
        if (!(entityPersistentStorage instanceof SectionEntityStorage)) {
            return;
        }
        SectionEntityStorage storage = (SectionEntityStorage)entityPersistentStorage;
        EntitySection existing = this.sectionStorage.getSection(sectionKey);
        if (existing != null && !existing.isEmpty()) {
            return;
        }
        int cx = SectionPos.x((long)sectionKey);
        int sy = SectionPos.y((long)sectionKey);
        int cz = SectionPos.z((long)sectionKey);
        for (Entity entity : SectionEntityStorage.readSectionEntities(storage.level(), cx, sy, cz)) {
            this.addEntity(entity, true);
        }
    }

    @Override
    public void chunkcutter$unloadSectionEntities(long sectionKey) {
        if (!(this.permanentStorage instanceof SectionEntityStorage)) {
            return;
        }
        if (!this.chunkcutter$loadedEntitySections.remove(sectionKey)) {
            return;
        }
        EntitySection section = this.sectionStorage.getSection(sectionKey);
        if (section == null) {
            return;
        }
        List entities = section.getEntities().toList();
        if (entities.isEmpty()) {
            this.removeSectionIfEmpty(sectionKey, section);
            return;
        }
        EntityPersistentStorage<T> entityPersistentStorage = this.permanentStorage;
        if (entityPersistentStorage instanceof SectionEntityStorage) {
            SectionEntityStorage storage = (SectionEntityStorage)entityPersistentStorage;
            ArrayList<Entity> roots = new ArrayList<Entity>(entities.size());
            for (EntityAccess entity : entities) {
                if (!(entity instanceof Entity)) continue;
                Entity e = (Entity)entity;
                if (!entity.shouldBeSaved()) continue;
                roots.add(e);
            }
            SectionEntityStorage.storeSectionEntities(storage.level(), sectionKey, roots);
        }
        for (EntityAccess entity : entities) {
            this.visibleEntityStorage.remove(entity);
            section.remove(entity);
            this.knownUuids.remove(entity.getUUID());
            if (!(entity instanceof Entity)) continue;
            Entity e = (Entity)entity;
            e.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            e.setLevelCallback(EntityInLevelCallback.NULL);
        }
        this.removeSectionIfEmpty(sectionKey, section);
    }

    @Overwrite
    public void ensureChunkQueuedForLoad(long chunkPos) {
        PersistentEntitySectionManager.ChunkLoadStatus chunkLoadStatus = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(chunkPos);
        if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
            this.requestChunkLoad(chunkPos);
        }
    }

    @Overwrite
    public boolean storeChunkSections(long chunkPos, Consumer<T> savedEntityVisitor) {
        PersistentEntitySectionManager.ChunkLoadStatus chunkLoadStatus = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(chunkPos);
        if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.PENDING) {
            return false;
        }
        List<T> rootEntitiesToSave = this.sectionStorage.getExistingSectionsInChunk(chunkPos).flatMap(section -> section.getEntities().filter(EntityAccess::shouldBeSaved)).collect(Collectors.toList());
        if (rootEntitiesToSave.isEmpty()) {
            if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
                this.permanentStorage.storeEntities(new ChunkEntities(ChunkPos.unpack((long)chunkPos), (List)ImmutableList.of()));
            }
            return true;
        }
        if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
            this.requestChunkLoad(chunkPos);
            return false;
        }
        this.permanentStorage.storeEntities(new ChunkEntities(ChunkPos.unpack((long)chunkPos), rootEntitiesToSave));
        rootEntitiesToSave.forEach(savedEntityVisitor);
        return true;
    }

    @Overwrite
    public void requestChunkLoad(long chunkKey) {
        this.chunkLoadStatuses.put(chunkKey, (Object)PersistentEntitySectionManager.ChunkLoadStatus.PENDING);
        ChunkPos pos = ChunkPos.unpack((long)chunkKey);
        ((CompletableFuture)this.permanentStorage.loadEntities(pos).thenAccept(this.loadingInbox::add)).exceptionally(t -> null);
    }

    @Overwrite
    public boolean processChunkUnload(long chunkKey) {
        boolean storeSuccessful = this.storeChunkSections(chunkKey, entity -> entity.getPassengersAndSelf().forEach(this::unloadEntity));
        if (!storeSuccessful) {
            return false;
        }
        this.sectionStorage.getExistingSectionPositionsInChunk(chunkKey).forEach(arg_0 -> ((LongSet)this.chunkcutter$loadedEntitySections).remove(arg_0));
        this.chunkLoadStatuses.remove(chunkKey);
        return true;
    }

    @Overwrite
    public void unloadEntity(EntityAccess e) {
        e.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        e.setLevelCallback(EntityInLevelCallback.NULL);
    }

    @Overwrite
    public void processUnloads() {
        this.chunksToUnload.removeIf(chunkKey -> this.chunkVisibility.get(chunkKey) != Visibility.HIDDEN ? true : this.processChunkUnload(chunkKey));
    }

    @Overwrite
    public void processPendingLoads() {
        ChunkEntities<T> loadedChunk;
        while ((loadedChunk = this.loadingInbox.poll()) != null) {
            loadedChunk.getEntities().forEach(e -> this.addEntity(e, true));
            this.chunkLoadStatuses.put(loadedChunk.getPos().pack(), (Object)PersistentEntitySectionManager.ChunkLoadStatus.LOADED);
        }
    }

    @Overwrite
    public void tick() {
        this.processPendingLoads();
        this.processUnloads();
    }

    @Overwrite
    public LongSet getAllChunksToSave() {
        LongSet result = this.sectionStorage.getAllChunksWithExistingSections();
        for (Long2ObjectMap.Entry entry : Long2ObjectMaps.fastIterable(this.chunkLoadStatuses)) {
            if (entry.getValue() != PersistentEntitySectionManager.ChunkLoadStatus.LOADED) continue;
            result.add(entry.getLongKey());
        }
        return result;
    }

    @Overwrite
    public void autoSave() {
        this.getAllChunksToSave().forEach(chunkKey -> {
            boolean shouldUnload;
            boolean bl = shouldUnload = this.chunkVisibility.get(chunkKey) == Visibility.HIDDEN;
            if (shouldUnload) {
                this.processChunkUnload(chunkKey);
            } else {
                this.storeChunkSections(chunkKey, e -> {});
            }
        });
    }

    @Overwrite
    public void saveAll() {
        LongSet chunksToSave = this.getAllChunksToSave();
        while (!chunksToSave.isEmpty()) {
            this.permanentStorage.flush(false);
            this.processPendingLoads();
            chunksToSave.removeIf(chunkKey -> {
                boolean shouldUnload = this.chunkVisibility.get(chunkKey) == Visibility.HIDDEN;
                return shouldUnload ? this.processChunkUnload(chunkKey) : this.storeChunkSections(chunkKey, e -> {});
            });
        }
        this.permanentStorage.flush(true);
    }

    @Override
    @Overwrite
    public void close() throws IOException {
        this.saveAll();
        this.permanentStorage.close();
    }

    @Overwrite
    public boolean isLoaded(UUID uuid) {
        return this.knownUuids.contains(uuid);
    }

    @Overwrite
    public LevelEntityGetter<T> getEntityGetter() {
        return this.entityGetter;
    }

    @Overwrite
    public boolean canPositionTick(BlockPos pos) {
        EntitySection section = this.sectionStorage.getSection(SectionPos.asLong((BlockPos)pos));
        if (section != null) {
            return section.getStatus().isTicking();
        }
        return ((Visibility)this.chunkVisibility.get(ChunkPos.pack((BlockPos)pos))).isTicking();
    }

    @Overwrite
    public boolean canPositionTick(ChunkPos pos) {
        return ((Visibility)this.chunkVisibility.get(pos.pack())).isTicking();
    }

    @Overwrite
    public boolean areEntitiesLoaded(long chunkKey) {
        return this.chunkLoadStatuses.get(chunkKey) == PersistentEntitySectionManager.ChunkLoadStatus.LOADED;
    }

    @Overwrite
    public void dumpSections(Writer output) throws IOException {
        CsvOutput csvOutput = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("visibility").addColumn("load_status").addColumn("entity_count").build(output);
        this.sectionStorage.getAllChunksWithExistingSections().forEach(chunkKey -> {
            PersistentEntitySectionManager.ChunkLoadStatus loadStatus = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(chunkKey);
            this.sectionStorage.getExistingSectionPositionsInChunk(chunkKey).forEach(sectionKey -> {
                EntitySection section = this.sectionStorage.getSection(sectionKey);
                if (section != null) {
                    try {
                        csvOutput.writeRow(new Object[]{SectionPos.x((long)sectionKey), SectionPos.y((long)sectionKey), SectionPos.z((long)sectionKey), section.getStatus(), loadStatus, section.size()});
                    }
                    catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        });
    }

    @Overwrite
    @VisibleForDebug
    public String gatherStats() {
        return this.knownUuids.size() + "," + this.visibleEntityStorage.count() + "," + this.sectionStorage.count() + "," + this.chunkLoadStatuses.size() + "," + this.chunkVisibility.size() + "," + this.loadingInbox.size() + "," + this.chunksToUnload.size();
    }

    @Overwrite
    @VisibleForDebug
    public int count() {
        return this.visibleEntityStorage.count();
    }
}


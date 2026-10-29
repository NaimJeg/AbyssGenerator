/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.datafix.DataFixTypes
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter
 *  net.minecraft.world.level.chunk.storage.RegionStorageInfo
 *  net.minecraft.world.level.chunk.storage.SimpleRegionStorage
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.level.storage.LevelResource
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.SectionStore;
import com.jfyx2025.chunkcutter.cubic.VerticalBounds;
import io.netty.buffer.Unpooled;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

public final class CubeSectionStore {
    private static final ConcurrentHashMap<Path, SectionStore> STORES = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Long, CompoundTag> COLUMN_CACHE = new ConcurrentHashMap();
    private static final ConcurrentHashMap<ResourceKey<Level>, Path> ROOTS = new ConcurrentHashMap();
    private static final Object ROOT_LOCK = new Object();
    private static volatile boolean IMMEDIATE_FLUSH = false;

    private CubeSectionStore() {
    }

    public static Path rootFor(Level level) {
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            return ROOTS.computeIfAbsent((ResourceKey<Level>)level.dimension(), key -> {
                Object object = ROOT_LOCK;
                synchronized (object) {
                    Path worldRoot = serverLevel.getServer().getWorldPath(LevelResource.ROOT).normalize();
                    return DimensionType.getStorageFolder((ResourceKey)level.dimension(), (Path)worldRoot).resolve("chunkcutter3d");
                }
            });
        }
        throw new IllegalStateException("CubeSectionStore \u4ec5\u652f\u6301\u670d\u52a1\u7aef\u5b58\u6863: " + String.valueOf(level.dimension().identifier()));
    }

    private static Path storeRoot(Level level) {
        return CubeSectionStore.rootFor(level).resolve("sections");
    }

    private static Path regionFile(Level level, int cx, int cz) {
        return CubeSectionStore.storeRoot(level).resolve("r." + (cx >> 5) + "." + (cz >> 5) + ".mca");
    }

    private static SectionStore store(Level level) {
        Path root = CubeSectionStore.storeRoot(level);
        return STORES.computeIfAbsent(root, key -> {
            ServerLevel server = (ServerLevel)level;
            SimpleRegionStorage storage = new SimpleRegionStorage(new RegionStorageInfo("chunkcutter", level.dimension(), "sections"), root, server.getServer().getFixerUpper(), false, DataFixTypes.CHUNK);
            return new SectionStore(storage, level.registryAccess(), (ChunkIOErrorReporter)server.getServer(), LevelHeightAccessor.create((int)VerticalBounds.engineMinY(), (int)VerticalBounds.engineHeight()));
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean hasStored(Level level, CloPos pos) {
        try {
            SectionStore sectionStore;
            SectionStore sectionStore2 = sectionStore = CubeSectionStore.store(level);
            synchronized (sectionStore2) {
                Optional<CompoundTag> tag = sectionStore.getSection(pos.asSectionPos().asLong());
                return tag != null && tag.isPresent();
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    public static CompoundTag buildColumnTag(Level level, int cx, int cz) {
        return null;
    }

    public static void saveChunkSections(Level level, ChunkAccess chunk) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void save(Level level, CloPos pos, LevelChunkSection section, PalettedContainerFactory factory, List<CompoundTag> blockEntities) {
        SectionStore sectionStore;
        DataLayer skyLight;
        DataLayer blockLight;
        if (section == null) {
            return;
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        section.write(buffer);
        byte[] data = new byte[buffer.writerIndex()];
        buffer.getBytes(0, data);
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("Data", data);
        if (blockEntities != null && !blockEntities.isEmpty()) {
            ListTag list = new ListTag();
            for (CompoundTag blockEntity : blockEntities) {
                list.add((Object)blockEntity.copy());
            }
            tag.put("BlockEntities", (Tag)list);
        }
        if ((blockLight = CubeSectionStore.lightData(level, LightLayer.BLOCK, pos)) != null) {
            tag.putByteArray("BlockLight", blockLight.getData());
        }
        if ((skyLight = CubeSectionStore.lightData(level, LightLayer.SKY, pos)) != null) {
            tag.putByteArray("SkyLight", skyLight.getData());
        }
        SectionStore sectionStore2 = sectionStore = CubeSectionStore.store(level);
        synchronized (sectionStore2) {
            sectionStore.putSection(pos.asSectionPos().asLong(), tag);
        }
    }

    private static @Nullable DataLayer lightData(Level level, LightLayer layer, CloPos pos) {
        try {
            return level.getLightEngine().getLayerListener(layer).getDataLayerData(pos.asSectionPos());
        }
        catch (Exception e) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static @Nullable Loaded load(Level level, CloPos pos, PalettedContainerFactory factory) {
        Optional<CompoundTag> stored;
        try {
            SectionStore sectionStore;
            SectionStore sectionStore2 = sectionStore = CubeSectionStore.store(level);
            synchronized (sectionStore2) {
                stored = sectionStore.getSection(pos.asSectionPos().asLong());
            }
        }
        catch (Exception e) {
            return null;
        }
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        CompoundTag tag = stored.get();
        byte[] data = tag.getByteArray("Data").orElse(null);
        if (data == null) {
            return null;
        }
        LevelChunkSection section = new LevelChunkSection(factory);
        section.read(new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])data)));
        ArrayList blockEntities = new ArrayList();
        ListTag list = tag.getListOrEmpty("BlockEntities");
        for (int i = 0; i < list.size(); ++i) {
            list.getCompound(i).ifPresent(t -> blockEntities.add(t.copy()));
        }
        DataLayer blockLight = tag.getByteArray("BlockLight").map(DataLayer::new).orElse(null);
        DataLayer skyLight = tag.getByteArray("SkyLight").map(DataLayer::new).orElse(null);
        return new Loaded(section, List.copyOf(blockEntities), blockLight, skyLight);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void tickStores(Level level) {
        SectionStore sectionStore = STORES.get(CubeSectionStore.storeRoot(level));
        if (sectionStore != null) {
            SectionStore sectionStore2 = sectionStore;
            synchronized (sectionStore2) {
                sectionStore.tickStore(() -> true);
            }
        }
    }

    public static void beginShutdown() {
        IMMEDIATE_FLUSH = true;
    }

    public static void endShutdown() {
        IMMEDIATE_FLUSH = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void closeAll() {
        long started = System.nanoTime();
        ArrayList<SectionStore> stores = new ArrayList<SectionStore>(STORES.values());
        STORES.clear();
        for (SectionStore store : stores) {
            try {
                long f = System.nanoTime();
                SectionStore sectionStore = store;
                synchronized (sectionStore) {
                    store.flushStore();
                    long c = System.nanoTime();
                    store.close();
                    System.out.println("[CC-TIME] section store flush=" + (c - f) / 1000000L + "ms close=" + (System.nanoTime() - c) / 1000000L + "ms");
                }
            }
            catch (Exception exception) {
            }
        }
        COLUMN_CACHE.clear();
        ROOTS.clear();
        System.out.println("[CC-TIME] section closeAll stores=" + stores.size() + " total=" + (System.nanoTime() - started) / 1000000L + "ms");
    }

    public record Loaded(LevelChunkSection section, List<CompoundTag> blockEntities, @Nullable DataLayer blockLight, @Nullable DataLayer skyLight) {
    }
}


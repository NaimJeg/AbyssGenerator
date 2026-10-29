/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.datafix.DataFixTypes
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter
 *  net.minecraft.world.level.chunk.storage.RegionStorageInfo
 *  net.minecraft.world.level.chunk.storage.SimpleRegionStorage
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CubeSectionStore;
import com.jfyx2025.chunkcutter.cubic.SectionStore;
import com.jfyx2025.chunkcutter.cubic.VerticalBounds;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.jspecify.annotations.Nullable;

public final class CubeEntityStore {
    private static final ConcurrentHashMap<Path, SectionStore> STORES = new ConcurrentHashMap();
    private static final Object IO_LOCK = new Object();
    private static volatile boolean IMMEDIATE_FLUSH = false;

    private CubeEntityStore() {
    }

    private static Path rootFor(Level level) {
        return CubeSectionStore.rootFor(level).resolve("entities");
    }

    private static Path regionFile(Level level, ChunkPos pos) {
        return CubeEntityStore.rootFor(level).resolve("r." + (pos.x() >> 5) + "." + (pos.z() >> 5) + ".mca");
    }

    private static SectionStore store(Level level) {
        Path root = CubeEntityStore.rootFor(level);
        return STORES.computeIfAbsent(root, key -> {
            ServerLevel server = (ServerLevel)level;
            SimpleRegionStorage storage = new SimpleRegionStorage(new RegionStorageInfo("chunkcutter", level.dimension(), "entity"), root, server.getServer().getFixerUpper(), false, DataFixTypes.CHUNK);
            return new SectionStore(storage, level.registryAccess(), (ChunkIOErrorReporter)server.getServer(), LevelHeightAccessor.create((int)VerticalBounds.engineMinY(), (int)VerticalBounds.engineHeight()));
        });
    }

    public static boolean hasStored(Level level, ChunkPos pos) {
        return Files.exists(CubeEntityStore.regionFile(level, pos), new LinkOption[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static @Nullable CompoundTag readSection(Level level, int sectionY, int sectionX, int sectionZ) {
        if (!Files.exists(CubeEntityStore.regionFile(level, new ChunkPos(sectionX, sectionZ)), new LinkOption[0])) {
            return null;
        }
        Object object = IO_LOCK;
        synchronized (object) {
            return CubeEntityStore.store(level).getSection(SectionPos.asLong((int)sectionX, (int)sectionY, (int)sectionZ)).orElse(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeSection(Level level, int sectionY, int sectionX, int sectionZ, CompoundTag tag) {
        Object object = IO_LOCK;
        synchronized (object) {
            SectionStore entityStore = CubeEntityStore.store(level);
            entityStore.putSection(SectionPos.asLong((int)sectionX, (int)sectionY, (int)sectionZ), tag);
            if (IMMEDIATE_FLUSH) {
                entityStore.flush(new ChunkPos(sectionX, sectionZ));
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
    public static void tickStores(Level level) {
        long deadline = System.nanoTime() + 2000000L;
        Object object = IO_LOCK;
        synchronized (object) {
            CubeEntityStore.store(level).tickStore(() -> System.nanoTime() < deadline);
        }
    }

    public static void flush() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void closeAll() {
        long started = System.nanoTime();
        int count = STORES.size();
        Object object = IO_LOCK;
        synchronized (object) {
            for (SectionStore store : STORES.values()) {
                try {
                    store.flushStore();
                    store.close();
                }
                catch (Exception exception) {}
            }
            STORES.clear();
        }
        System.out.println("[CC-TIME] entity closeAll stores=" + count + " total=" + (System.nanoTime() - started) / 1000000L + "ms");
    }
}


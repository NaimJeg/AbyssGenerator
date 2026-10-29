/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
 *  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 */
package com.jfyx2025.chunkcutter;

import com.jfyx2025.chunkcutter.ChunkCutterCommand;
import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.cubic.CubeEntityStore;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.CubeSectionStore;
import com.jfyx2025.chunkcutter.cubic.SectionGenerationQueue;
import com.jfyx2025.chunkcutter.cubic.VerticalBounds;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainSources;
import com.jfyx2025.chunkcutter.network.SectionsPayload;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class ChunkCutterMod
implements ModInitializer {
    public static final String MOD_ID = "chunkcutter";
    private static final Map<ResourceKey<Level>, CubeLoadManager> CUBE_LOAD_MANAGERS = new ConcurrentHashMap<ResourceKey<Level>, CubeLoadManager>();
    private static volatile long LAST_TICK_NANOS = System.nanoTime();
    private static final Map<ResourceKey<Level>, SectionGenerationQueue> SECTION_QUEUES = new ConcurrentHashMap<ResourceKey<Level>, SectionGenerationQueue>();

    public static CubeLoadManager cubeLoadManager(ResourceKey<Level> dimension) {
        return CUBE_LOAD_MANAGERS.computeIfAbsent(dimension, k -> new CubeLoadManager());
    }

    public static SectionGenerationQueue sectionGenerationQueue(ResourceKey<Level> dimension) {
        return SECTION_QUEUES.computeIfAbsent(dimension, k -> new SectionGenerationQueue());
    }

    public void onInitialize() {
        ChunkCutterMod.applyConfig();
        PayloadTypeRegistry.clientboundPlay().register(SectionsPayload.TYPE, SectionsPayload.CODEC);
        ChunkCutterCommand.register();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ChunkCutterConfig.isEnabled()) {
                return;
            }
            LAST_TICK_NANOS = System.nanoTime();
            for (ServerLevel level : server.getAllLevels()) {
                ResourceKey dimension = level.dimension();
                ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)dimension).tick(level);
                CubeSectionStore.tickStores((Level)level);
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LAST_TICK_NANOS = System.nanoTime();
            Thread watchdog = new Thread(() -> {
                boolean dumped = false;
                while (server.isRunning()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                    if (System.nanoTime() - LAST_TICK_NANOS > 5000000000L) {
                        if (dumped) continue;
                        dumped = true;
                        ChunkCutterMod.dumpStalledThreads();
                        continue;
                    }
                    dumped = false;
                }
            }, "ChunkCutter-Watchdog");
            watchdog.setDaemon(true);
            watchdog.start();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(ChunkCutterMod::saveAll);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ChunkCutterMod.clearWorldState());
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (handler.player != null) {
                CubeProvider.clearPreloaded(handler.player);
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player != null) {
                CubeProvider.clearPreloaded(handler.player);
            }
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> CubeProvider.clearSentSections(player));
    }

    private static void dumpStalledThreads() {
        System.out.println("[CC-WATCHDOG] server tick stalled >5s");
        block0: for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            String name = entry.getKey().getName();
            if (!name.matches(".*(Server thread|Render thread|IO-Worker|ChunkCutter|Netty|Worker-Main|commonPool).*")) continue;
            System.out.println("[CC-WATCHDOG] " + name + " state=" + String.valueOf((Object)entry.getKey().getState()));
            int count = 0;
            for (StackTraceElement element : entry.getValue()) {
                System.out.println("  at " + String.valueOf(element));
                if (++count >= 25) continue block0;
            }
        }
    }

    private static void saveAll(MinecraftServer server) {
        CubeSectionStore.beginShutdown();
        CubeEntityStore.beginShutdown();
        TerrainSources.beginShutdown();
        CubeProvider.shutdownTerrainExecutor();
        CubeProvider.clearPending();
        long started = System.nanoTime();
        for (ServerLevel level : server.getAllLevels()) {
            long a = System.nanoTime();
            int saved = CubeProvider.saveAll(level);
            System.out.println("[CC-TIME] saveAll " + String.valueOf(level.dimension().identifier()) + " cubes=" + saved + " ms=" + (System.nanoTime() - a) / 1000000L);
        }
        System.out.println("[CC-TIME] saveAll total=" + (System.nanoTime() - started) / 1000000L + "ms");
    }

    public static void clearWorldState() {
        CUBE_LOAD_MANAGERS.clear();
        SECTION_QUEUES.clear();
        CubeSectionStore.closeAll();
        CubeEntityStore.closeAll();
        CubeSectionStore.endShutdown();
        CubeEntityStore.endShutdown();
        TerrainSources.clear();
        CubeProvider.clearPending();
        CubeProvider.clearAllPreloaded();
        CubeProvider.clearLogs();
    }

    public static void applyConfig() {
        ChunkCutterConfig cfg = ChunkCutterConfig.get();
        VerticalBounds.configure(cfg.enabled, cfg.minSectionY, cfg.maxSectionY);
        TerrainSources.clear();
    }
}


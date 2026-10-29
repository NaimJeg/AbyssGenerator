/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.progress.LevelLoadListener
 *  net.minecraft.server.level.progress.LevelLoadListener$Stage
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.TicketStorage
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MinecraftServer.class})
public abstract class MinecraftServerMixin {
    @Shadow
    private LevelLoadListener levelLoadListener;
    @Shadow
    private Map<ResourceKey<Level>, ServerLevel> levels;

    @Shadow
    private void updateEffectiveRespawnData() {
    }

    @Shadow
    public abstract void updateMobSpawningFlags();

    @Inject(method={"prepareLevels"}, at={@At(value="HEAD")}, cancellable=true)
    private void chunkcutter$skipVanillaSpawnPreload(CallbackInfo ci) {
        System.out.println("[CC-LOAD] prepareLevels intercepted enabled=" + ChunkCutterConfig.isEnabled());
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        for (ServerLevel level : this.levels.values()) {
            TicketStorage tickets = (TicketStorage)level.getDataStorage().get(TicketStorage.TYPE);
            if (tickets != null) {
                tickets.activateAllDeactivatedTickets();
            }
            CubeProvider.preloadSpawn(level);
        }
        LevelLoadListener listener = this.levelLoadListener;
        if (listener != null) {
            listener.start(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS, 0);
            listener.finish(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS);
        }
        this.updateMobSpawningFlags();
        this.updateEffectiveRespawnData();
        ci.cancel();
    }
}


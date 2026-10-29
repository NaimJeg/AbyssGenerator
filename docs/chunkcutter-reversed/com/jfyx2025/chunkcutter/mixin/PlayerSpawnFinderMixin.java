/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.PlayerSpawnFinder
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainSources;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerSpawnFinder.class})
public abstract class PlayerSpawnFinderMixin {
    @Inject(method={"findSpawn"}, at={@At(value="HEAD")}, cancellable=true)
    private static void chunkcutter$findSpawn3D(ServerLevel level, BlockPos spawnSuggestion, CallbackInfoReturnable<CompletableFuture<Vec3>> cir) {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        if (!(level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator)) {
            return;
        }
        try {
            int x = spawnSuggestion.getX();
            int z = spawnSuggestion.getZ();
            int surfaceY = TerrainSources.forLevel(level).heightAt(x, z) + 1;
            CubeProvider.prepareSpawnColumn(level, x, surfaceY, z);
            cir.setReturnValue(CompletableFuture.completedFuture(Vec3.atBottomCenterOf((Vec3i)new BlockPos(x, surfaceY, z))));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}


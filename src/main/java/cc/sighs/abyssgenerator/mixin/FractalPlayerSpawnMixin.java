package cc.sighs.abyssgenerator.mixin;

import cc.sighs.abyssgenerator.world.generation.FractalChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerSpawnFinder.class)
public abstract class FractalPlayerSpawnMixin {
    @Shadow private static Vec3 fixupSpawnHeight(CollisionGetter level, BlockPos pos) { throw new AssertionError(); }

    @Inject(method = "findSpawn", at = @At("HEAD"), cancellable = true)
    private static void abyssgenerator$prepareSpawn(ServerLevel level, BlockPos suggestion,
            CallbackInfoReturnable<CompletableFuture<Vec3>> cir) {
        if (!level.dimensionType().hasSkyLight() && level.getChunkSource().getGenerator() instanceof FractalChunkGenerator) {
            // Vanilla's no-skylight branch skips the asynchronous chunk loading used
            // by its surface search. A collision check on unloaded data finds no floor.
            cir.setReturnValue(level.getChunkSource().addTicketAndLoadWithRadius(
                    TicketType.PLAYER_SPAWN, ChunkPos.containing(suggestion), 0)
                    .thenApplyAsync(ignored -> fixupSpawnHeight(level, suggestion), level.getServer()));
        }
    }
}

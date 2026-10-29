package cc.sighs.abyssgenerator.mixin;

import cc.sighs.abyssgenerator.world.generation.GenerationAuditAccess;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStep.class)
public abstract class ChunkStepMixin {
    @Unique private static final boolean abyssgenerator$auditEnabled = Boolean.getBoolean("abyssgenerator.generationAudit");

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/status/ChunkStatusTask;doWork(Lnet/minecraft/world/level/chunk/status/WorldGenContext;Lnet/minecraft/world/level/chunk/status/ChunkStep;Lnet/minecraft/util/StaticCache2D;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<ChunkAccess> abyssgenerator$measureStage(ChunkStatusTask task, WorldGenContext context,
            ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk) {
        if (!abyssgenerator$auditEnabled) return task.doWork(context, step, cache, chunk);
        var audit = ((GenerationAuditAccess) context.level()).abyssgenerator$generationAudit();
        var observation = audit.begin(step.targetStatus().getName(), chunk.getPersistedStatus().isBefore(step.targetStatus()),
                chunk.getPos().x(), chunk.getPos().z(), chunk.getMinSectionY(), chunk.getSectionsCount());
        try {
            return task.doWork(context, step, cache, chunk).whenComplete((result, error) -> audit.finish(observation, error != null));
        } catch (RuntimeException | Error error) {
            audit.finish(observation, true);
            throw error;
        }
    }
}

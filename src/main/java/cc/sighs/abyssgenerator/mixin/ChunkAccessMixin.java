package cc.sighs.abyssgenerator.mixin;

import cc.sighs.abyssgenerator.world.bridge.VanillaColumnBridge;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin {
    @Shadow @Final protected LevelHeightAccessor levelHeightAccessor;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkAccess;replaceMissingSections(Lnet/minecraft/world/level/chunk/PalettedContainerFactory;[Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"))
    private void abyssgenerator$sparseConstruction(PalettedContainerFactory factory, LevelChunkSection[] sections) {
        VanillaColumnBridge.fillMissing(levelHeightAccessor, factory, sections);
    }

    @Inject(method = "fillBiomesFromNoise", at = @At("HEAD"), cancellable = true)
    private void abyssgenerator$deferBiomes(BiomeResolver resolver, Climate.Sampler sampler, CallbackInfo ci) {
        if (VanillaColumnBridge.enabled(levelHeightAccessor)) ci.cancel();
    }
}

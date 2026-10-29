package cc.sighs.abyssgenerator.mixin;

import cc.sighs.abyssgenerator.world.bridge.UnknownSection;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SerializableChunkData.class)
public abstract class SerializableChunkDataMixin {
    @Redirect(method = "copyOf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;copy()Lnet/minecraft/world/level/chunk/LevelChunkSection;"))
    private static LevelChunkSection abyssgenerator$omitUnknown(LevelChunkSection section) {
        return section instanceof UnknownSection ? null : section.copy();
    }
}

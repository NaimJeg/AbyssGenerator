package cc.sighs.abyssgenerator.mixin;

import cc.sighs.abyssgenerator.world.bridge.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    private void abyssgenerator$rejectUnknownWrite(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
        var chunk = (LevelChunk) (Object) this;
        if (VanillaColumnBridge.enabled(chunk.getLevel()) && !chunk.isOutsideBuildHeight(pos)
                && chunk.getSection(chunk.getSectionIndex(pos.getY())) instanceof UnknownSection) {
            throw new IllegalStateException("Section is not ready: " + pos + "; explicit ensure required in P1 spike");
        }
    }
}

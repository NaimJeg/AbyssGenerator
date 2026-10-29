package cc.sighs.abyssgenerator.world.generation;

/** Authoritative pre-surface metadata. Missing context must be requested, never inferred from air. */
public interface SectionSurfaceContext {
    SurfaceDepthProfile column(int localX, int localZ);
    int surfaceHeight(int localX, int localZ);
    default boolean supportsBiomeExtensions() { return false; }
    default net.minecraft.world.level.block.state.BlockState noiseBlockState(int localX, int y, int localZ) {
        throw new UnsupportedOperationException("Noise boundary samples unavailable");
    }
}

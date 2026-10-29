package cc.sighs.abyssgenerator.world.generation;

/** Read-only surface context, including dependency sampling outside the target section. */
public interface SurfaceDepthProfile {
    SurfaceColumnProfile.Depths depthsAt(int y);
    SurfaceDepthProfile withSolidFill(int fromY, int toYExclusive);
}

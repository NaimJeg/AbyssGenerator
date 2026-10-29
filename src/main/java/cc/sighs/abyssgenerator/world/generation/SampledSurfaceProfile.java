package cc.sighs.abyssgenerator.world.generation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import cc.sighs.abyssgenerator.world.generation.SurfaceColumnProfile.*;

/** Task-local, lazily sampled dependency context; never reads live world sections. */
public final class SampledSurfaceProfile implements SurfaceDepthProfile {
    private final int minY, maxY;
    private final IntFunction<Material> sampler;
    private final Map<Integer, Material> materials = new HashMap<>();
    private final Map<Integer, Depths> depths = new HashMap<>();
    public SampledSurfaceProfile(int minY, int maxYExclusive, IntFunction<Material> sampler) {
        if (minY >= maxYExclusive) throw new IllegalArgumentException("Invalid sampling bounds");
        this.minY = minY; this.maxY = maxYExclusive; this.sampler = java.util.Objects.requireNonNull(sampler);
    }
    private Material sample(int y) {
        return materials.computeIfAbsent(y, key -> java.util.Objects.requireNonNull(sampler.apply(key), "Unknown boundary sample"));
    }
    @Override public Depths depthsAt(int y) {
        if (y < minY || y >= maxY || sample(y) != Material.SOLID) throw new IllegalArgumentException("Not a solid sample: " + y);
        return depths.computeIfAbsent(y, ignored -> {
            int above = 1, below = 1, water = Integer.MIN_VALUE;
            for (int cursor = y + 1; cursor < maxY; cursor++) {
                Material material = sample(cursor);
                if (material == Material.AIR) break;
                if (material == Material.SOLID) above++;
                else water = cursor + 1;
            }
            for (int cursor = y - 1; cursor >= minY && sample(cursor) == Material.SOLID; cursor--) below++;
            return new Depths(above, below, water);
        });
    }
    @Override public SurfaceDepthProfile withSolidFill(int fromY, int toYExclusive) {
        if (fromY >= toYExclusive) return this;
        if (fromY < minY || toYExclusive > maxY) throw new IllegalArgumentException("Extension outside bounds");
        return new SampledSurfaceProfile(minY, maxY, y -> y >= fromY && y < toYExclusive ? Material.SOLID : sample(y));
    }
}

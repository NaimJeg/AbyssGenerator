package cc.sighs.abyssgenerator.world.generation;

import java.util.List;

/** Immutable pre-surface run metadata, independent of loaded world sections. */
public final class SurfaceColumnProfile implements SurfaceDepthProfile {
    public enum Material { AIR, FLUID, SOLID }
    public record Run(int minY, int maxYExclusive, Material material) {
        public Run {
            if (minY >= maxYExclusive || material == null) throw new IllegalArgumentException("Invalid surface run");
        }
    }
    public record Depths(int above, int below, int waterHeight) {}
    private final List<Run> runs;
    private final int[] aboveAtTop, waterAtTop;

    /** Runs must cover the original generation bounds, ordered bottom to top. */
    public SurfaceColumnProfile(int minY, int maxYExclusive, List<Run> runs) {
        this.runs = List.copyOf(runs);
        if (runs.isEmpty() || runs.getFirst().minY != minY || runs.getLast().maxYExclusive != maxYExclusive)
            throw new IllegalArgumentException("Surface profile must cover original bounds");
        for (int i = 1; i < runs.size(); i++) {
            if (runs.get(i - 1).maxYExclusive != runs.get(i).minY || runs.get(i - 1).material == runs.get(i).material)
                throw new IllegalArgumentException("Surface runs must be contiguous and coalesced");
        }
        aboveAtTop = new int[runs.size()];
        waterAtTop = new int[runs.size()];
        int above = 0, water = Integer.MIN_VALUE;
        for (int i = runs.size() - 1; i >= 0; i--) {
            Run run = runs.get(i);
            aboveAtTop[i] = above;
            waterAtTop[i] = water;
            switch (run.material) {
                case AIR -> { above = 0; water = Integer.MIN_VALUE; }
                case FLUID -> { if (water == Integer.MIN_VALUE) water = run.maxYExclusive; }
                case SOLID -> above = Math.addExact(above, Math.subtractExact(run.maxYExclusive, run.minY));
            }
        }
    }

    /** Same counters as the vanilla descending surface scan, without replaying it. */
    public Depths depthsAt(int y) {
        int low = 0, high = runs.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Run run = runs.get(mid);
            if (y < run.minY) high = mid - 1;
            else if (y >= run.maxYExclusive) low = mid + 1;
            else {
                if (run.material != Material.SOLID) throw new IllegalArgumentException("Surface depth requested for non-solid block at " + y);
                return new Depths(aboveAtTop[mid] + run.maxYExclusive - y, y - run.minY + 1, waterAtTop[mid]);
            }
        }
        throw new IllegalArgumentException("Surface depth outside profile: " + y);
    }

    /** Applies a badlands extension without resampling the rest of the column. */
    public SurfaceColumnProfile withSolidFill(int fromY, int toYExclusive) {
        if (fromY >= toYExclusive) return this;
        if (fromY < runs.getFirst().minY || toYExclusive > runs.getLast().maxYExclusive)
            throw new IllegalArgumentException("Extension outside profile");
        var result = new java.util.ArrayList<Run>();
        for (Run run : runs) {
            append(result, run.minY, Math.min(run.maxYExclusive, fromY), run.material);
            append(result, Math.max(run.minY, fromY), Math.min(run.maxYExclusive, toYExclusive), Material.SOLID);
            append(result, Math.max(run.minY, toYExclusive), run.maxYExclusive, run.material);
        }
        return new SurfaceColumnProfile(runs.getFirst().minY, runs.getLast().maxYExclusive, result);
    }

    private static void append(java.util.ArrayList<Run> runs, int minY, int maxY, Material material) {
        if (minY >= maxY) return;
        if (!runs.isEmpty() && runs.getLast().material == material && runs.getLast().maxYExclusive == minY) {
            var previous = runs.removeLast();
            runs.add(new Run(previous.minY, maxY, material));
        } else runs.add(new Run(minY, maxY, material));
    }
}

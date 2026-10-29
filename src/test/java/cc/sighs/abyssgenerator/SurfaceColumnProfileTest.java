package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.generation.SurfaceColumnProfile;
import cc.sighs.abyssgenerator.world.generation.SampledSurfaceProfile;
import cc.sighs.abyssgenerator.world.generation.SurfaceColumnProfile.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SurfaceColumnProfileTest {
    @Test void runMetadataMatchesDescendingScanAcrossSectionBoundaries() {
        var random = new Random(104729);
        for (int iteration = 0; iteration < 200; iteration++) {
            var materials = new Material[384];
            var runs = new ArrayList<Run>();
            int start = 0;
            while (start < materials.length) {
                Material material = Material.values()[random.nextInt(3)];
                int end = Math.min(materials.length, start + random.nextInt(40) + 1);
                Arrays.fill(materials, start, end, material);
                if (!runs.isEmpty() && runs.getLast().material() == material) {
                    var previous = runs.removeLast();
                    runs.add(new Run(previous.minY(), end - 64, material));
                } else runs.add(new Run(start - 64, end - 64, material));
                start = end;
            }
            var profile = new SurfaceColumnProfile(-64, 320, runs);
            var sampled = new SampledSurfaceProfile(-64, 320, y -> materials[y + 64]);
            int above = 0, water = Integer.MIN_VALUE;
            for (int i = materials.length - 1; i >= 0; i--) {
                if (materials[i] == Material.AIR) { above = 0; water = Integer.MIN_VALUE; }
                else if (materials[i] == Material.FLUID) { if (water == Integer.MIN_VALUE) water = i - 64 + 1; }
                else {
                    above++;
                    int below = 1;
                    while (i - below >= 0 && materials[i - below] == Material.SOLID) below++;
                    assertEquals(new Depths(above, below, water), profile.depthsAt(i - 64));
                    assertEquals(new Depths(above, below, water), sampled.depthsAt(i - 64));
                }
            }
        }
    }

    @Test void missingCoverageAndUnknownCoordinatesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SurfaceColumnProfile(-64, 320, List.of(new Run(0, 320, Material.SOLID))));
        var profile = new SurfaceColumnProfile(-16, 16, List.of(new Run(-16, 0, Material.SOLID), new Run(0, 16, Material.AIR)));
        assertThrows(IllegalArgumentException.class, () -> profile.depthsAt(0));
        assertThrows(IllegalArgumentException.class, () -> profile.depthsAt(-17));
        assertEquals(new Depths(1, 16, Integer.MIN_VALUE), profile.depthsAt(-1));
    }

    @Test void extensionMergesRunsAndPreservesOriginalProfile() {
        var original = new SurfaceColumnProfile(-32, 32, List.of(new Run(-32, -16, Material.SOLID), new Run(-16, 32, Material.AIR)));
        var extended = original.withSolidFill(-16, 17);
        assertEquals(new Depths(1, 49, Integer.MIN_VALUE), extended.depthsAt(16));
        assertEquals(new Depths(34, 16, Integer.MIN_VALUE), extended.depthsAt(-17));
        assertEquals(new Depths(1, 16, Integer.MIN_VALUE), original.depthsAt(-17));
        assertThrows(IllegalArgumentException.class, () -> original.depthsAt(0));
        assertThrows(IllegalArgumentException.class, () -> original.withSolidFill(0, 33));
        var sampled = new SampledSurfaceProfile(-32, 32, y -> y < -16 ? Material.SOLID : Material.AIR).withSolidFill(-16, 17);
        assertEquals(extended.depthsAt(16), sampled.depthsAt(16));
        assertEquals(extended.depthsAt(-17), sampled.depthsAt(-17));
    }
}

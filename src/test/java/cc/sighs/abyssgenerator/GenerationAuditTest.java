package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.generation.GenerationAudit;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class GenerationAuditTest {
    @Test void resetExcludesEarlierInFlightStages() {
        var audit = new GenerationAudit();
        var old = audit.begin("noise", true, 0, 0, -4, 24);
        audit.reset();
        var current = audit.begin("noise", false, 8, -8, -127, 254);
        audit.finish(old, true);
        audit.finish(current, false);
        var result = audit.snapshot().get("noise");
        assertEquals(1, result.calls());
        assertEquals(0, result.generatedCalls());
        assertEquals(1, result.completed());
        assertEquals(0, result.failed());
        assertEquals(254, result.inputSectionSlots());
        assertEquals(254, result.maxInputSectionSlots());
        assertEquals(-127, result.minSectionY());
        assertEquals(126, result.maxSectionY());
    }

    @Test void concurrentCompletionAndBoundsAreRetained() throws Exception {
        var audit = new GenerationAudit();
        try (var workers = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 1000; i++) {
                int coordinate = i;
                workers.submit(() -> audit.finish(audit.begin("features", true, coordinate, -coordinate, -4, 24), coordinate % 2 == 0));
            }
        }
        var result = audit.snapshot().get("features");
        assertEquals(1000, result.calls());
        assertEquals(1000, result.completed());
        assertEquals(500, result.failed());
        assertEquals(24000, result.inputSectionSlots());
        assertEquals(999, result.maxChunkX());
        assertEquals(-999, result.minChunkZ());
        assertTrue(result.elapsedNanos() >= 0);
    }

    @Test void snapshotsAreDetachedAndStageNamesBounded() {
        var audit = new GenerationAudit();
        var snapshot = audit.snapshot();
        for (int i = 0; i < 1000; i++) audit.finish(audit.begin("stage-" + i, true, 0, 0, 0, 16), false);
        assertTrue(snapshot.isEmpty());
        assertEquals(33, audit.snapshot().size());
        assertEquals(968, audit.snapshot().get("other").calls());
    }
}

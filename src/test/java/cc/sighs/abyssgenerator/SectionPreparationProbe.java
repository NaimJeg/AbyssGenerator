package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.coord.SectionKey;
import cc.sighs.abyssgenerator.world.generation.GenerationAuditAccess;
import cc.sighs.abyssgenerator.world.section.SectionManager;
import com.google.gson.Gson;
import net.minecraft.server.level.ServerLevel;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Real-server assertions run on completion, before normal unloading. */
public final class SectionPreparationProbe {
    private static volatile String report = "{\"ready\":false}";
    private SectionPreparationProbe() {}
    public static String report() { return report; }
    public static String start(ServerLevel level, int x) {
        report = "{\"ready\":false}";
        var manager = SectionManager.of(level);
        var audit = ((GenerationAuditAccess) level).abyssgenerator$generationAudit();
        var first = new SectionKey(x, -64, 64);
        var second = new SectionKey(x, 64, 64);
        var cancelled = new SectionKey(x, 32, 64);
        var timedOut = new SectionKey(x, 33, 64);
        check(level.getChunkSource().getChunkNow(x, 64) == null, "requires a cold column");
        audit.reset();
        var request = manager.prepareSection(first, Duration.ofSeconds(30));
        check(request == manager.prepareSection(first, Duration.ofSeconds(30)), "duplicate request not shared");
        var firstFuture = request.toCompletableFuture();
        var secondFuture = manager.prepareSection(second, Duration.ofSeconds(30)).toCompletableFuture();
        var cancelledFuture = manager.prepareSection(cancelled, Duration.ofSeconds(30)).toCompletableFuture();
        check(manager.cancelPreparation(cancelled), "cancel did not find request");
        check(cancelledFuture.isCompletedExceptionally(), "cancel did not complete request");
        var expired = manager.prepareSection(timedOut, Duration.ofNanos(1)).toCompletableFuture();
        manager.tick();
        check(expired.isCompletedExceptionally(), "expired request did not fail");
        check(manager.preparationColumnCount() == 1, "same column did not share one ticket");
        CompletableFuture.allOf(firstFuture, secondFuture).thenRun(() -> {
            check(manager.readySection(first).isPresent() && manager.readySection(second).isPresent(), "ready data absent");
            check(!manager.readySection(cancelled).isPresent(), "cancelled section installed late");
            check(!manager.readySection(timedOut).isPresent(), "timed out section installed late");
            check(manager.pendingCount() == 0 && manager.preparationColumnCount() == 0, "preparation resources leaked");
            var chunk = level.getChunkSource().getChunkNow(x, 64);
            check(firstFuture.join().data() == chunk.getSection(level.getSectionIndexFromSectionY(first.y())), "ownership mismatch");
            check(!audit.snapshot().isEmpty(), "audit did not observe stages");
            report = new Gson().toJson(Map.of("phase", "P1-prepare-complete", "passed", true, "ready", true,
                    "stages", audit.snapshot(), "limitation", "Vanilla column dependency remains; input slot counts are not voxel work."));
        }).exceptionally(error -> {
            report = new Gson().toJson(Map.of("phase", "P1-prepare-complete", "passed", false, "ready", true, "error", error.toString()));
            return null;
        });
        return new Gson().toJson(Map.of("phase", "P1-prepare-start", "passed", true, "pending", manager.pendingCount(), "columns", manager.preparationColumnCount()));
    }
    private static void check(boolean ok, String message) { if (!ok) throw new IllegalStateException(message); }
}

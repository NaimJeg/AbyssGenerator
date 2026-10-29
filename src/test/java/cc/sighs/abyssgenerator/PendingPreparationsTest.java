package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.section.PendingPreparations;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;

class PendingPreparationsTest {
    private final AtomicLong clock = new AtomicLong();
    private final Queue<Runnable> main = new ArrayDeque<>();
    private final List<CompletableFuture<Void>> loads = new ArrayList<>();
    private final List<String> installed = new ArrayList<>(), released = new ArrayList<>();
    private final PendingPreparations<String, String> requests = new PendingPreparations<>(2, clock::get, main::add,
            key -> { var future = new CompletableFuture<Void>(); loads.add(future); return future; },
            key -> { installed.add(key); return key; }, released::add);
    private CompletionStage<String> request(String key) { return requests.request(key, Duration.ofNanos(10)); }
    private void drain() { while (!main.isEmpty()) main.remove().run(); }

    @Test void deduplicatesAndInstallsOnlyOnOwnerExecutor() {
        var first = request("a");
        assertSame(first, request("a"));
        assertEquals(1, loads.size());
        assertFalse(first.toCompletableFuture().isDone());
        loads.getFirst().complete(null);
        assertTrue(installed.isEmpty());
        drain();
        assertEquals("a", first.toCompletableFuture().join());
        assertEquals(List.of("a"), installed);
        assertEquals(List.of("a"), released);
        assertEquals(0, requests.size());
    }

    @Test void detachedFutureCannotCancelOtherConsumers() {
        var result = request("a");
        result.toCompletableFuture().cancel(false);
        loads.getFirst().complete(null);
        drain();
        assertEquals("a", result.toCompletableFuture().join());
    }

    @Test void cancelledLateCompletionCannotInstallOrReleaseReplacement() {
        var old = request("a");
        assertTrue(requests.cancel("a"));
        var replacement = request("a");
        loads.getFirst().complete(null);
        drain();
        assertTrue(installed.isEmpty());
        assertFalse(replacement.toCompletableFuture().isDone());
        assertEquals(List.of("a"), released);
        loads.get(1).complete(null);
        drain();
        assertEquals("a", replacement.toCompletableFuture().join());
        assertTrue(old.toCompletableFuture().isCompletedExceptionally());
        assertEquals(List.of("a", "a"), released);
    }

    @Test void timeoutReleasesAndRejectsLateInstallation() {
        var result = request("a");
        clock.set(10);
        requests.tick();
        assertInstanceOf(TimeoutException.class, assertThrows(CompletionException.class,
                () -> result.toCompletableFuture().join()).getCause());
        loads.getFirst().complete(null);
        drain();
        assertTrue(installed.isEmpty());
        assertEquals(List.of("a"), released);
    }

    @Test void completionChecksDeadlineEvenBeforeNextTick() {
        var result = request("a");
        loads.getFirst().complete(null);
        clock.set(11);
        drain();
        assertTrue(result.toCompletableFuture().isCompletedExceptionally());
        assertTrue(installed.isEmpty());
    }

    @Test void closeRejectsNewRequestsAndLateResults() {
        var first = request("a");
        var second = request("b");
        requests.close();
        requests.close();
        loads.forEach(load -> load.complete(null));
        drain();
        assertTrue(installed.isEmpty());
        assertEquals(2, released.size());
        assertTrue(first.toCompletableFuture().isCompletedExceptionally());
        assertTrue(second.toCompletableFuture().isCompletedExceptionally());
        assertTrue(request("c").toCompletableFuture().isCompletedExceptionally());
        assertEquals(2, loads.size());
    }

    @Test void capacityRejectsWithoutAcquiringResources() {
        request("a"); request("b");
        var rejected = request("c");
        assertInstanceOf(RejectedExecutionException.class, assertThrows(CompletionException.class,
                () -> rejected.toCompletableFuture().join()).getCause());
        assertEquals(2, loads.size());
        requests.cancel("a");
        request("c");
        assertEquals(3, loads.size());
    }

    @Test void loadFailureReleasesWithoutInstalling() {
        var result = request("a");
        var failure = new IllegalStateException("load failed");
        loads.getFirst().completeExceptionally(failure);
        drain();
        assertSame(failure, assertThrows(CompletionException.class, () -> result.toCompletableFuture().join()).getCause());
        assertTrue(installed.isEmpty());
        assertEquals(List.of("a"), released);
    }

    @Test void installationFailureStillReleases() {
        var failing = new PendingPreparations<String, String>(1, clock::get, Runnable::run,
                key -> CompletableFuture.completedFuture(null), key -> { throw new IllegalStateException("install"); }, released::add);
        assertTrue(failing.request("a", Duration.ofSeconds(1)).toCompletableFuture().isCompletedExceptionally());
        assertEquals(List.of("a"), released);
        assertEquals(0, failing.size());
    }

    @Test void synchronousLoadFailureStillReleases() {
        var failing = new PendingPreparations<String, String>(1, clock::get, Runnable::run,
                key -> { throw new IllegalStateException("load"); }, key -> key, released::add);
        assertTrue(failing.request("a", Duration.ofSeconds(1)).toCompletableFuture().isCompletedExceptionally());
        assertEquals(List.of("a"), released);
        assertEquals(0, failing.size());
    }

    @Test void rejectsOffThreadAccess() throws Exception {
        try (var executor = Executors.newSingleThreadExecutor()) {
            assertInstanceOf(IllegalStateException.class, assertThrows(ExecutionException.class,
                    () -> executor.submit(() -> request("a")).get()).getCause());
        }
        assertTrue(loads.isEmpty());
    }
}

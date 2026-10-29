package cc.sighs.abyssgenerator.world.section;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Main-thread administrative preparation, not the terrain worker scheduler.
 * A key has one deadline and one shared result; cancellation cancels that entire
 * administrative request. Completion never installs a cancelled/replaced request.
 * Call tick regularly, including while the world has no players.
 */
public final class PendingPreparations<K, V> implements AutoCloseable {
    private final Thread owner = Thread.currentThread();
    private final Map<K, Pending> pending = new HashMap<>();
    private final int limit;
    private final LongSupplier clock;
    private final Executor mainThread;
    private final Function<K, ? extends CompletionStage<?>> preload;
    private final Function<K, V> install;
    private final Consumer<K> release;
    private boolean closed;

    private final class Pending {
        final long started = clock.getAsLong();
        final long timeout;
        final CompletableFuture<V> result = new CompletableFuture<>();
        final CompletionStage<V> view = result.minimalCompletionStage();
        Pending(long timeout) { this.timeout = timeout; }
    }

    public PendingPreparations(int limit, LongSupplier clock, Executor mainThread,
            Function<K, ? extends CompletionStage<?>> preload, Function<K, V> install, Consumer<K> release) {
        if (limit < 1) throw new IllegalArgumentException("Preparation limit must be positive");
        this.limit = limit;
        this.clock = Objects.requireNonNull(clock);
        this.mainThread = Objects.requireNonNull(mainThread);
        this.preload = Objects.requireNonNull(preload);
        this.install = Objects.requireNonNull(install);
        this.release = Objects.requireNonNull(release);
    }

    public CompletionStage<V> request(K key, Duration timeout) {
        checkThread();
        Objects.requireNonNull(key);
        long nanos = timeout.toNanos();
        if (nanos <= 0) throw new IllegalArgumentException("Preparation timeout must be positive");
        if (closed) return CompletableFuture.<V>failedFuture(new IllegalStateException("World closed")).minimalCompletionStage();
        Pending previous = pending.get(key);
        if (previous != null) return previous.view;
        if (pending.size() >= limit)
            return CompletableFuture.<V>failedFuture(new RejectedExecutionException("Preparation limit reached: " + limit)).minimalCompletionStage();
        Pending entry = new Pending(nanos);
        pending.put(key, entry);
        try {
            preload.apply(key).whenComplete((ignored, error) -> mainThread.execute(() -> finish(key, entry, error)));
        } catch (RuntimeException error) {
            finish(key, entry, error);
        }
        return entry.view;
    }

    private void finish(K key, Pending entry, Throwable failure) {
        checkThread();
        if (pending.get(key) != entry) return;
        if (failure == null && expired(entry)) failure = new TimeoutException("Section preparation timed out: " + key);
        V value = null;
        if (failure == null) {
            try { value = install.apply(key); }
            catch (RuntimeException error) { failure = error; }
        }
        pending.remove(key);
        try { release.accept(key); }
        catch (RuntimeException error) {
            if (failure == null) failure = error;
            else failure.addSuppressed(error);
        }
        if (failure == null) entry.result.complete(value);
        else entry.result.completeExceptionally(failure);
    }

    public boolean cancel(K key) {
        checkThread();
        Pending entry = pending.get(key);
        if (entry == null) return false;
        finish(key, entry, new CancellationException("Section preparation cancelled: " + key));
        return true;
    }

    public void tick() {
        checkThread();
        for (var entry : Map.copyOf(pending).entrySet()) {
            if (expired(entry.getValue()))
                finish(entry.getKey(), entry.getValue(), new TimeoutException("Section preparation timed out: " + entry.getKey()));
        }
    }

    public int size() { checkThread(); return pending.size(); }

    @Override public void close() {
        checkThread();
        closed = true;
        for (var entry : Map.copyOf(pending).entrySet())
            finish(entry.getKey(), entry.getValue(), new CancellationException("World closed"));
    }

    private boolean expired(Pending entry) { return clock.getAsLong() - entry.started >= entry.timeout; }
    private void checkThread() {
        if (Thread.currentThread() != owner) throw new IllegalStateException("Preparation accessed off owner thread");
    }
}

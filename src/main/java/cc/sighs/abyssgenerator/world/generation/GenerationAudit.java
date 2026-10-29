package cc.sighs.abyssgenerator.world.generation;

import java.util.LinkedHashMap;
import java.util.Map;

/** Bounded per-world stage counters. Slot counts describe inputs, not voxel work. */
public final class GenerationAudit {
    private long epoch;
    private final Map<String, Stage> stages = new LinkedHashMap<>();
    private static final class Stage {
        long calls, generatedCalls, completed, failed, nanos, inputSectionSlots;
        int maxInputSectionSlots;
        int minChunkX = Integer.MAX_VALUE, maxChunkX = Integer.MIN_VALUE;
        int minChunkZ = Integer.MAX_VALUE, maxChunkZ = Integer.MIN_VALUE;
        int minSectionY = Integer.MAX_VALUE, maxSectionY = Integer.MIN_VALUE;
    }
    public record Observation(long epoch, String stage, long startedNanos) {}
    public record Snapshot(long calls, long generatedCalls, long completed, long failed, long elapsedNanos,
            long inputSectionSlots, int maxInputSectionSlots, int minChunkX, int maxChunkX, int minChunkZ, int maxChunkZ,
            int minSectionY, int maxSectionY) {}

    public synchronized Observation begin(String name, boolean generating, int x, int z, int minSectionY, int slots) {
        // Vanilla has a fixed number of statuses. Keep malformed/custom names bounded too.
        if (!stages.containsKey(name) && stages.size() >= 32) name = "other";
        var stage = stages.computeIfAbsent(name, ignored -> new Stage());
        stage.calls++;
        if (generating) stage.generatedCalls++;
        stage.inputSectionSlots += slots;
        stage.maxInputSectionSlots = Math.max(stage.maxInputSectionSlots, slots);
        stage.minChunkX = Math.min(stage.minChunkX, x); stage.maxChunkX = Math.max(stage.maxChunkX, x);
        stage.minChunkZ = Math.min(stage.minChunkZ, z); stage.maxChunkZ = Math.max(stage.maxChunkZ, z);
        stage.minSectionY = Math.min(stage.minSectionY, minSectionY);
        stage.maxSectionY = Math.max(stage.maxSectionY, minSectionY + slots - 1);
        return new Observation(epoch, name, System.nanoTime());
    }

    public synchronized void finish(Observation observation, boolean failed) {
        if (observation.epoch != epoch) return;
        var stage = stages.get(observation.stage);
        stage.completed++;
        if (failed) stage.failed++;
        stage.nanos += System.nanoTime() - observation.startedNanos;
    }

    /** Starts a measurement epoch; already-running stages remain outside it. */
    public synchronized void reset() { epoch++; stages.clear(); }

    public synchronized Map<String, Snapshot> snapshot() {
        var result = new LinkedHashMap<String, Snapshot>();
        stages.forEach((name, s) -> result.put(name, new Snapshot(s.calls, s.generatedCalls, s.completed, s.failed,
                s.nanos, s.inputSectionSlots, s.maxInputSectionSlots, s.minChunkX, s.maxChunkX, s.minChunkZ, s.maxChunkZ, s.minSectionY, s.maxSectionY)));
        return Map.copyOf(result);
    }
}

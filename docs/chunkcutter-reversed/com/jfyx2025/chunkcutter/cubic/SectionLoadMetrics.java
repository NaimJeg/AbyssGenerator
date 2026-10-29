/*
 * Decompiled with CFR 0.152.
 */
package com.jfyx2025.chunkcutter.cubic;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public final class SectionLoadMetrics {
    private static final AtomicLong LOAD_COUNT = new AtomicLong();
    private static final AtomicLong LOAD_NANOS = new AtomicLong();
    private static final AtomicLong LOAD_MAX = new AtomicLong();
    private static final AtomicLong LOAD_WAIT_NANOS = new AtomicLong();
    private static final AtomicLong FILL_COUNT = new AtomicLong();
    private static final AtomicLong FILL_NANOS = new AtomicLong();
    private static final AtomicLong FILL_MAX = new AtomicLong();
    private static final AtomicLong BIOME_NANOS = new AtomicLong();
    private static final AtomicLong BLOCK_ENTITY_NANOS = new AtomicLong();
    private static final AtomicLong INSTALL_COUNT = new AtomicLong();
    private static final AtomicLong INSTALL_NANOS = new AtomicLong();
    private static final AtomicLong INSTALL_MAX = new AtomicLong();
    private static final AtomicLong QUEUE_COLUMNS = new AtomicLong();
    private static final AtomicLong QUEUE_SECTIONS = new AtomicLong();
    private static final AtomicLong QUEUE_NANOS = new AtomicLong();
    private static final AtomicLong SWEEP_COLUMNS = new AtomicLong();
    private static final AtomicLong SWEEP_ADDED = new AtomicLong();
    private static final AtomicLong SWEEP_NANOS = new AtomicLong();
    private static final AtomicLong IO_WAIT_NANOS = new AtomicLong();
    private static final AtomicLong IO_READ_NANOS = new AtomicLong();
    private static final AtomicLong IO_WRITE_NANOS = new AtomicLong();
    private static final AtomicLong IO_COUNT = new AtomicLong();
    private static final AtomicLong LOCK_COUNT = new AtomicLong();
    private static final AtomicLong LOCK_WAIT_NANOS = new AtomicLong();
    private static final AtomicLong LOCK_MAX = new AtomicLong();
    private static final AtomicLong STRUCTURE_NANOS = new AtomicLong();
    private static final AtomicLong REFERENCE_NANOS = new AtomicLong();
    private static final AtomicLong BIOME_STAGE_NANOS = new AtomicLong();
    private static final AtomicLong NOISE_NANOS = new AtomicLong();
    private static final AtomicLong SURFACE_NANOS = new AtomicLong();
    private static final AtomicLong CARVER_NANOS = new AtomicLong();
    private static final AtomicLong FEATURE_NANOS = new AtomicLong();
    private static final AtomicLong STRUCTURE_MAX = new AtomicLong();
    private static final AtomicLong REFERENCE_MAX = new AtomicLong();
    private static final AtomicLong BIOME_STAGE_MAX = new AtomicLong();
    private static final AtomicLong NOISE_MAX = new AtomicLong();
    private static final AtomicLong SURFACE_MAX = new AtomicLong();
    private static final AtomicLong CARVER_MAX = new AtomicLong();
    private static final AtomicLong FEATURE_MAX = new AtomicLong();
    private static final AtomicLong LAST_LOG = new AtomicLong(Long.MIN_VALUE);

    private SectionLoadMetrics() {
    }

    public static void recordLoad(long waitNanos, long durationNanos) {
        LOAD_COUNT.incrementAndGet();
        LOAD_WAIT_NANOS.addAndGet(waitNanos);
        LOAD_NANOS.addAndGet(durationNanos);
        LOAD_MAX.accumulateAndGet(durationNanos, Math::max);
    }

    public static void recordFill(long nanos) {
        FILL_COUNT.incrementAndGet();
        FILL_NANOS.addAndGet(nanos);
        FILL_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordBiome(long nanos) {
        BIOME_NANOS.addAndGet(nanos);
    }

    public static void recordBlockEntities(long nanos) {
        BLOCK_ENTITY_NANOS.addAndGet(nanos);
    }

    public static void recordInstall(long nanos) {
        INSTALL_COUNT.incrementAndGet();
        INSTALL_NANOS.addAndGet(nanos);
        INSTALL_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordQueue(int columns, int sections, long nanos) {
        QUEUE_COLUMNS.addAndGet(columns);
        QUEUE_SECTIONS.addAndGet(sections);
        QUEUE_NANOS.addAndGet(nanos);
    }

    public static void recordSweep(int columns, int added, long nanos) {
        SWEEP_COLUMNS.addAndGet(columns);
        SWEEP_ADDED.addAndGet(added);
        SWEEP_NANOS.addAndGet(nanos);
    }

    public static void recordLock(long waitNanos) {
        LOCK_COUNT.incrementAndGet();
        LOCK_WAIT_NANOS.addAndGet(waitNanos);
        LOCK_MAX.accumulateAndGet(waitNanos, Math::max);
    }

    public static void recordStructure(long nanos) {
        STRUCTURE_NANOS.addAndGet(nanos);
        STRUCTURE_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordReference(long nanos) {
        REFERENCE_NANOS.addAndGet(nanos);
        REFERENCE_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordBiomeStage(long nanos) {
        BIOME_STAGE_NANOS.addAndGet(nanos);
        BIOME_STAGE_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordNoise(long nanos) {
        NOISE_NANOS.addAndGet(nanos);
        NOISE_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordSurface(long nanos) {
        SURFACE_NANOS.addAndGet(nanos);
        SURFACE_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordCarver(long nanos) {
        CARVER_NANOS.addAndGet(nanos);
        CARVER_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordFeature(long nanos) {
        FEATURE_NANOS.addAndGet(nanos);
        FEATURE_MAX.accumulateAndGet(nanos, Math::max);
    }

    public static void recordIo(long waitNanos, long ioNanos, boolean write) {
        IO_COUNT.incrementAndGet();
        IO_WAIT_NANOS.addAndGet(waitNanos);
        (write ? IO_WRITE_NANOS : IO_READ_NANOS).addAndGet(ioNanos);
    }

    public static void logIfDue(long gameTime, int mapSize, int queueSize, int pending, int changed) {
        long last = LAST_LOG.get();
        if (last != Long.MIN_VALUE && gameTime - last < 200L) {
            return;
        }
        LAST_LOG.set(gameTime);
        long loadCount = LOAD_COUNT.getAndSet(0L);
        long fillCount = FILL_COUNT.getAndSet(0L);
        long installCount = INSTALL_COUNT.getAndSet(0L);
        long ioCount = IO_COUNT.getAndSet(0L);
        long loadMax = LOAD_MAX.getAndSet(0L);
        long fillMax = FILL_MAX.getAndSet(0L);
        long installMax = INSTALL_MAX.getAndSet(0L);
        long lockCount = LOCK_COUNT.getAndSet(0L);
        long lockMax = LOCK_MAX.getAndSet(0L);
        long structMax = STRUCTURE_MAX.getAndSet(0L);
        long refMax = REFERENCE_MAX.getAndSet(0L);
        long biomeMax = BIOME_STAGE_MAX.getAndSet(0L);
        long noiseMax = NOISE_MAX.getAndSet(0L);
        long surfMax = SURFACE_MAX.getAndSet(0L);
        long carveMax = CARVER_MAX.getAndSet(0L);
        long featMax = FEATURE_MAX.getAndSet(0L);
        System.out.println("[CC-PERF] t=" + gameTime + " map=" + mapSize + " queue=" + queueSize + " pending=" + pending + " changed=" + changed + " load[n=" + loadCount + ",avg=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(LOAD_NANOS, loadCount)) + ",max=" + SectionLoadMetrics.ms(loadMax) + ",wait=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(LOAD_WAIT_NANOS, loadCount)) + "] fill[n=" + fillCount + ",avg=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(FILL_NANOS, fillCount)) + ",max=" + SectionLoadMetrics.ms(fillMax) + ",biome=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(BIOME_NANOS, fillCount)) + ",be=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(BLOCK_ENTITY_NANOS, fillCount)) + "] install[n=" + installCount + ",avg=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(INSTALL_NANOS, installCount)) + ",max=" + SectionLoadMetrics.ms(installMax) + "] queue[cols=" + QUEUE_COLUMNS.getAndSet(0L) + ",secs=" + QUEUE_SECTIONS.getAndSet(0L) + ",t=" + SectionLoadMetrics.ms(QUEUE_NANOS.getAndSet(0L)) + "] sweep[cols=" + SWEEP_COLUMNS.getAndSet(0L) + ",add=" + SWEEP_ADDED.getAndSet(0L) + ",t=" + SectionLoadMetrics.ms(SWEEP_NANOS.getAndSet(0L)) + "] io[n=" + ioCount + ",wait=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(IO_WAIT_NANOS, ioCount)) + ",read=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(IO_READ_NANOS, ioCount)) + ",write=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(IO_WRITE_NANOS, ioCount)) + "] lock[n=" + lockCount + ",avg=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(LOCK_WAIT_NANOS, lockCount)) + ",max=" + SectionLoadMetrics.ms(lockMax) + "] stages[struct=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(STRUCTURE_NANOS, fillCount)) + ",ref=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(REFERENCE_NANOS, fillCount)) + ",biome=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(BIOME_STAGE_NANOS, fillCount)) + ",noise=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(NOISE_NANOS, fillCount)) + ",surf=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(SURFACE_NANOS, fillCount)) + ",carve=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(CARVER_NANOS, fillCount)) + ",feat=" + SectionLoadMetrics.ms(SectionLoadMetrics.avg(FEATURE_NANOS, fillCount)) + "] stageMax[struct=" + SectionLoadMetrics.ms(structMax) + ",ref=" + SectionLoadMetrics.ms(refMax) + ",biome=" + SectionLoadMetrics.ms(biomeMax) + ",noise=" + SectionLoadMetrics.ms(noiseMax) + ",surf=" + SectionLoadMetrics.ms(surfMax) + ",carve=" + SectionLoadMetrics.ms(carveMax) + ",feat=" + SectionLoadMetrics.ms(featMax) + "]");
    }

    private static long avg(AtomicLong total, long count) {
        long n = count;
        return n == 0L ? 0L : total.getAndSet(0L) / n;
    }

    private static String ms(long nanos) {
        return String.format(Locale.ROOT, "%.2fms", (double)nanos / 1000000.0);
    }
}


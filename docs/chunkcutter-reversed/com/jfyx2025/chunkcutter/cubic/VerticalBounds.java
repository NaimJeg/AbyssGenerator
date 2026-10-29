/*
 * Decompiled with CFR 0.152.
 */
package com.jfyx2025.chunkcutter.cubic;

public final class VerticalBounds {
    public static final int DEFAULT_ENGINE_MIN_SECTION = -4;
    public static final int DEFAULT_ENGINE_MAX_SECTION = 19;
    private static volatile boolean enabled = true;
    private static volatile int engineMinSection = -4;
    private static volatile int engineMaxSection = 19;

    private VerticalBounds() {
    }

    public static synchronized void configure(boolean enabled, int min, int max) {
        VerticalBounds.enabled = enabled;
        if (min >= max) {
            return;
        }
        engineMinSection = min;
        engineMaxSection = max;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int engineMinSection() {
        return engineMinSection;
    }

    public static int engineMaxSection() {
        return engineMaxSection;
    }

    public static int engineMinY() {
        return engineMinSection * 16;
    }

    public static int engineHeight() {
        return (engineMaxSection - engineMinSection + 1) * 16;
    }

    public static int engineMaxY() {
        return (engineMaxSection + 1) * 16 - 1;
    }

    public static int engineSectionsCount() {
        return engineMaxSection - engineMinSection + 1;
    }
}


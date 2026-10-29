/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.LevelHeightAccessor
 */
package com.jfyx2025.chunkcutter.cubic.terrain;

import net.minecraft.world.level.LevelHeightAccessor;

public final class NoiseBounds
implements LevelHeightAccessor {
    private final int minY;
    private final int height;

    public NoiseBounds(int minY, int height) {
        this.minY = minY;
        this.height = height;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getHeight() {
        return this.height;
    }
}


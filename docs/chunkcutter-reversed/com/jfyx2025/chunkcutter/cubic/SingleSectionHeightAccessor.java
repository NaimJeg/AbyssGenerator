/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.LevelHeightAccessor
 */
package com.jfyx2025.chunkcutter.cubic;

import net.minecraft.world.level.LevelHeightAccessor;

public final class SingleSectionHeightAccessor
implements LevelHeightAccessor {
    private final int minY;

    public SingleSectionHeightAccessor(int sectionY) {
        this.minY = sectionY << 4;
    }

    public int sectionY() {
        return this.minY >> 4;
    }

    public int getHeight() {
        return 16;
    }

    public int getMinY() {
        return this.minY;
    }
}


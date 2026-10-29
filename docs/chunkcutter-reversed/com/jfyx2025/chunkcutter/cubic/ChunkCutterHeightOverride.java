/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.LevelHeightAccessor
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import net.minecraft.world.level.LevelHeightAccessor;
import org.jspecify.annotations.Nullable;

public interface ChunkCutterHeightOverride {
    public @Nullable LevelHeightAccessor chunkcutter$getHeightOverride();

    public void chunkcutter$setHeightOverride(@Nullable LevelHeightAccessor var1);
}


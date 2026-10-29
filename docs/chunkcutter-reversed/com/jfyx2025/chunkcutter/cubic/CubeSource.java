/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.jfyx2025.chunkcutter.cubic;

import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import org.jspecify.annotations.Nullable;

public interface CubeSource {
    public CubeMap chunkcutter$cubeMap();

    default public @Nullable CubeChunk chunkcutter$getCube(CloPos pos) {
        return this.chunkcutter$cubeMap().get(pos);
    }
}


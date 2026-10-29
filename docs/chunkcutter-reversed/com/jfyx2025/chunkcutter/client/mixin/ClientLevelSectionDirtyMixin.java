/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  org.spongepowered.asm.mixin.Mixin
 */
package com.jfyx2025.chunkcutter.client.mixin;

import com.jfyx2025.chunkcutter.cubic.SectionDirtySink;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ClientLevel.class})
public abstract class ClientLevelSectionDirtyMixin
implements SectionDirtySink {
    @Override
    public void chunkcutter$setSectionDirty(int x, int y, int z) {
        ((ClientLevel)this).setSectionDirtyWithNeighbors(x, y, z);
    }
}


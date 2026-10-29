/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.RotatingSectionStorage
 *  net.minecraft.client.renderer.SectionOcclusionGraph
 *  net.minecraft.client.renderer.ViewArea
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.client.mixin;

import net.minecraft.client.RotatingSectionStorage;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ViewArea.class})
public abstract class ViewAreaMixin {
    @Shadow
    private SectionOcclusionGraph sectionOcclusionGraph;
    @Shadow
    private RotatingSectionStorage<SectionRenderDispatcher.RenderSection> sections;
    @Shadow
    private int minY;
    @Shadow
    private int maxY;

    @Overwrite
    public void releaseAllBuffers() {
        for (SectionRenderDispatcher.RenderSection section : this.sections) {
            section.reset();
        }
    }

    @Overwrite
    public int size() {
        return this.sections.size();
    }

    @Overwrite
    public int minY() {
        return this.minY;
    }

    @Overwrite
    public int maxY() {
        return this.maxY;
    }

    @Overwrite
    public int minSectionY() {
        return this.sections.minY();
    }

    @Overwrite
    public int maxSectionY() {
        return this.sections.maxY();
    }

    @Overwrite
    public int sectionCount() {
        return this.sections.height();
    }

    @Overwrite
    public int getViewDistance() {
        return this.sections.radius();
    }

    @Overwrite
    public boolean repositionCamera(SectionPos cameraSectionPos) {
        boolean result = this.sections.repositionCenter(cameraSectionPos);
        if (result) {
            this.sectionOcclusionGraph.invalidate();
        }
        return result;
    }

    @Overwrite
    public SectionPos getCameraSectionPos() {
        return this.sections.centerSectionPos();
    }

    @Overwrite
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SectionRenderDispatcher.RenderSection getRenderSectionAt(BlockPos pos) {
        return (SectionRenderDispatcher.RenderSection)this.sections.getValueAt(pos);
    }

    @Overwrite
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SectionRenderDispatcher.RenderSection getRenderSection(long sectionNode) {
        return (SectionRenderDispatcher.RenderSection)this.sections.getValue(sectionNode);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.renderer.SectionOcclusionGraph
 *  net.minecraft.client.renderer.ViewArea
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.jfyx2025.chunkcutter.client.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.client.ChunkCutterClient;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SectionOcclusionGraph.class})
public abstract class SectionOcclusionGraphMixin {
    @Shadow
    private ViewArea viewArea;
    @Shadow
    private AtomicBoolean needsFrustumUpdate;
    @Shadow
    private AtomicReference<?> currentGraph;
    private long ccCalls;

    @Overwrite
    public boolean consumeFrustumUpdate() {
        boolean vanilla = this.needsFrustumUpdate.compareAndSet(true, false);
        if (!ChunkCutterConfig.isEnabled()) {
            return vanilla;
        }
        return vanilla || this.currentGraph.get() != null && ChunkCutterClient.consumeSectionsDirty();
    }

    @Inject(method={"addSectionsInFrustum"}, at={@At(value="TAIL")})
    private void chunkcutter$addMaterializedSections(Frustum frustum, List<SectionRenderDispatcher.RenderSection> visibleSections, List<SectionRenderDispatcher.RenderSection> nearbyVisibleSections, CallbackInfo ci) {
        if (!ChunkCutterConfig.isEnabled()) {
            return;
        }
        if (this.viewArea == null || Minecraft.getInstance().level == null) {
            return;
        }
        ClientChunkCache clientChunkCache = Minecraft.getInstance().level.getChunkSource();
        if (!(clientChunkCache instanceof CubeSource)) {
            return;
        }
        CubeSource source = (CubeSource)clientChunkCache;
        long[] keys = source.chunkcutter$cubeMap().keys().toLongArray();
        int added = 0;
        for (long sectionNode : keys) {
            SectionRenderDispatcher.RenderSection section = this.viewArea.getRenderSection(sectionNode);
            if (section == null || !frustum.isVisible(section.getBoundingBox()) || visibleSections.contains(section)) continue;
            visibleSections.add(section);
            ++added;
            if (nearbyVisibleSections.contains(section)) continue;
            nearbyVisibleSections.add(section);
        }
        if (Boolean.getBoolean("chunkcutter.debugRenderLog") && ++this.ccCalls % 100L == 0L) {
            System.out.println("[CC-RDIAG] visible=" + visibleSections.size() + " cube=" + keys.length + " ourAdded=" + added);
        }
    }
}


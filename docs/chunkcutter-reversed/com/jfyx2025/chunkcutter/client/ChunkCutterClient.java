/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.EmptyLevelChunk
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.phys.AABB
 *  org.slf4j.LoggerFactory
 */
package com.jfyx2025.chunkcutter.client;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.client.ChunkCutterKeyCategories;
import com.jfyx2025.chunkcutter.client.ClientChunkSink;
import com.jfyx2025.chunkcutter.client.ClientSectionLevelChunk;
import com.jfyx2025.chunkcutter.cubic.ChunkCutterSectionWrite;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.network.SectionsPayload;
import io.netty.buffer.Unpooled;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.slf4j.LoggerFactory;

public class ChunkCutterClient
implements ClientModInitializer {
    private static boolean renderChunkBounds;
    private static KeyMapping toggleChunkRenderKey;
    private static long ccDiagTick;
    private static int ccSectionsReceived;
    private static int ccSectionsApplied;
    private static int ccEnsureCreated;
    private static int ccEnsureFailed;
    private static final AtomicBoolean ccSectionsDirty;

    public static boolean sectionsDirty() {
        return ccSectionsDirty.get();
    }

    public static boolean consumeSectionsDirty() {
        return ccSectionsDirty.compareAndSet(true, false);
    }

    public static void markSectionsDirty() {
        ccSectionsDirty.set(true);
    }

    public void onInitializeClient() {
        if (ChunkCutterConfig.isEnabled()) {
            for (String mod : new String[]{"sodium", "voxy"}) {
                if (!FabricLoader.getInstance().isModLoaded(mod)) continue;
                LoggerFactory.getLogger((String)"chunkcutter").warn("\u68c0\u6d4b\u5230 {}\uff1a\u5b83\u7ed5\u8fc7\u539f\u7248\u6bb5\u6e32\u67d3\u7ba1\u7ebf\uff0csectionOnlySync=true \u65f6\u53ef\u80fd\u65e0\u6cd5\u6e32\u67d3\u5730\u5f62\uff08\u8bf7\u79fb\u9664\u8be5\u6a21\u7ec4\u6216\u5173\u95ed sectionOnlySync\uff09\u3002", (Object)mod);
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(SectionsPayload.TYPE, (payload, context) -> context.client().execute(() -> ChunkCutterClient.applySections(context.client(), payload)));
        toggleChunkRenderKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.chunkcutter.toggle_chunk_render", 78, ChunkCutterKeyCategories.CHUNKCUTTER));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleChunkRenderKey.consumeClick()) {
                renderChunkBounds = !renderChunkBounds;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ChunkCutterConfig.isEnabled()) {
                return;
            }
            if (client.level == null || client.player == null) {
                return;
            }
            if (++ccDiagTick % 100L != 0L) {
                return;
            }
            if (!Boolean.getBoolean("chunkcutter.debugRenderLog")) {
                return;
            }
            try {
                int n;
                int addedLoaded;
                int n2;
                BlockPos blockPos = client.player.blockPosition();
                int sx = blockPos.getX() >> 4;
                int sy = blockPos.getY() >> 4;
                int sz = blockPos.getZ() >> 4;
                ClientChunkCache cache = client.level.getChunkSource();
                if (cache instanceof CubeSource) {
                    CubeSource cs = (CubeSource)cache;
                    n2 = cs.chunkcutter$cubeMap().size();
                } else {
                    n2 = -1;
                }
                int cubeSize = n2;
                ChunkAccess chunk = client.level.getChunk(sx, sz, ChunkStatus.FULL, false);
                boolean lightOn = client.level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode((int)sx, (int)sz));
                int visible = client.levelRenderer.visibleSections().size();
                if (cache instanceof ClientChunkCache) {
                    ClientChunkCache cc = cache;
                    v1 = cc.addedLoadedChunks().size();
                } else {
                    v1 = addedLoaded = -1;
                }
                if (cache instanceof ClientChunkCache) {
                    ClientChunkCache cc2 = cache;
                    n = cc2.removedEmptySections().size();
                } else {
                    n = -1;
                }
                int removedEmpty = n;
                System.out.println("[CC-DIAG] recv=" + ccSectionsReceived + " applied=" + ccSectionsApplied + " ensureOk=" + ccEnsureCreated + " ensureFail=" + ccEnsureFailed + " cube=" + cubeSize + " visible=" + visible + " addedLoaded=" + addedLoaded + " removedEmpty=" + removedEmpty + " chunk=" + (chunk != null) + " lightOn=" + lightOn + " at=" + sx + "," + sy + "," + sz);
            }
            catch (Throwable t) {
                System.out.println("[CC-DIAG] err " + String.valueOf(t));
            }
        });
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> {
            int pcz;
            if (!renderChunkBounds) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) {
                return;
            }
            int pcx = mc.player.blockPosition().getX() >> 4;
            LevelChunk chunk = mc.level.getChunk(pcx, pcz = mc.player.blockPosition().getZ() >> 4);
            if (chunk == null) {
                return;
            }
            LevelChunkSection[] sections = chunk.getSections();
            int baseX = pcx << 4;
            int baseZ = pcz << 4;
            for (int i = 0; i < sections.length; ++i) {
                int y0 = chunk.getSectionYFromSectionIndex(i) << 4;
                AABB box = new AABB((double)baseX, (double)y0, (double)baseZ, (double)baseX + 16.0, (double)y0 + 16.0, (double)baseZ + 16.0);
                Gizmos.cuboid((AABB)box, (GizmoStyle)GizmoStyle.stroke((int)-1, (float)2.0f));
            }
        });
    }

    public static boolean isRenderChunkBounds() {
        return renderChunkBounds;
    }

    private static void applySections(Minecraft mc, SectionsPayload payload) {
        if (mc.level == null || !payload.dimension().equals((Object)mc.level.dimension().identifier())) {
            return;
        }
        for (SectionsPayload.SectionEntry entry : payload.entries()) {
            Object cube;
            ++ccSectionsReceived;
            CloPos clo = CloPos.cubeOf(entry.cubePos());
            int cx = clo.x();
            int cy = clo.y();
            int cz = clo.z();
            LevelChunk existing = mc.level.getChunk(cx, cz);
            boolean valid = existing != null && existing.getPos().x() == cx && existing.getPos().z() == cz && !(existing instanceof EmptyLevelChunk);
            Object chunk = valid ? existing : new ClientSectionLevelChunk(mc.level, new ChunkPos(cx, cz));
            int idx = chunk.getSectionIndexFromSectionY(cy);
            if (idx < 0 || idx >= chunk.getSectionsCount()) continue;
            if (entry.data().length == 0) {
                ClientChunkCache clientChunkCache = mc.level.getChunkSource();
                if (clientChunkCache instanceof CubeSource) {
                    CubeSource cs = (CubeSource)clientChunkCache;
                    cs.chunkcutter$cubeMap().remove(entry.cubePos());
                }
                if (chunk instanceof ClientSectionLevelChunk) {
                    ClientSectionLevelChunk view = (ClientSectionLevelChunk)((Object)chunk);
                    view.chunkcutter$setSection(idx, null);
                }
                ((ChunkCutterSectionWrite)chunk).chunkcutter$clearSection(idx);
                mc.level.setSectionDirtyWithNeighbors(cx, cy, cz);
                clientChunkCache = mc.level.getChunkSource();
                if (clientChunkCache instanceof ClientChunkCache) {
                    ClientChunkCache cache = clientChunkCache;
                    cache.onSectionEmptinessChanged(cx, cy, cz, true);
                }
                ChunkCutterClient.markSectionsDirty();
                continue;
            }
            LevelChunkSection section = ((ChunkCutterSectionWrite)chunk).chunkcutter$getSectionForWrite(idx);
            section.read(new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])entry.data())));
            ClientChunkCache clientChunkCache = mc.level.getChunkSource();
            if (clientChunkCache instanceof CubeSource) {
                CubeSource cs = (CubeSource)clientChunkCache;
                cube = new CubeChunk(clo, mc.level.palettedContainerFactory());
                ((CubeChunk)((Object)cube)).setSection(section);
                cs.chunkcutter$cubeMap().put((CubeChunk)((Object)cube));
            }
            if (chunk instanceof ClientSectionLevelChunk) {
                ClientSectionLevelChunk view = (ClientSectionLevelChunk)((Object)chunk);
                view.chunkcutter$setSection(idx, section);
            }
            if (!valid) {
                cube = mc.level.getChunkSource();
                if (!(cube instanceof ClientChunkSink)) continue;
                ClientChunkSink sink = (ClientChunkSink)cube;
                sink.chunkcutter$insertChunk(cx, cz, (LevelChunk)chunk);
                ++ccEnsureCreated;
            }
            SectionPos sectionPos = SectionPos.of((int)cx, (int)cy, (int)cz);
            if (entry.blockLight() != null) {
                mc.level.getLightEngine().queueSectionData(LightLayer.BLOCK, sectionPos, new DataLayer(entry.blockLight()));
            }
            if (entry.skyLight() != null) {
                mc.level.getLightEngine().queueSectionData(LightLayer.SKY, sectionPos, new DataLayer(entry.skyLight()));
            }
            for (CompoundTag beTag : entry.blockEntities()) {
                BlockState st;
                BlockPos bp = new BlockPos(beTag.getIntOr("x", 0), beTag.getIntOr("y", 0), beTag.getIntOr("z", 0));
                BlockEntity be = BlockEntity.loadStatic((BlockPos)bp, (BlockState)(st = section.getBlockState(bp.getX() & 0xF, bp.getY() & 0xF, bp.getZ() & 0xF)), (CompoundTag)beTag, (HolderLookup.Provider)mc.level.registryAccess());
                if (be == null) continue;
                chunk.setBlockEntity(be);
            }
            mc.level.setSectionDirtyWithNeighbors(cx, cy, cz);
            ClientChunkCache clientChunkCache2 = mc.level.getChunkSource();
            if (clientChunkCache2 instanceof ClientChunkCache) {
                ClientChunkCache cache = clientChunkCache2;
                cache.onSectionEmptinessChanged(cx, cy, cz, section.hasOnlyAir());
            }
            ++ccSectionsApplied;
            ChunkCutterClient.markSectionsDirty();
        }
    }

    static {
        ccSectionsDirty = new AtomicBoolean(false);
    }
}


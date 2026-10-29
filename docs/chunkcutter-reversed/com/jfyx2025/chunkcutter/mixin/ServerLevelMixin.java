/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.material.FluidState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeRange;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.mixin.ServerChunkCacheAccessor;
import com.jfyx2025.chunkcutter.network.SectionsPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerLevel.class})
public abstract class ServerLevelMixin {
    private static long CUBE_MGMT_TICKS;

    @Overwrite
    public void tickChunk(LevelChunk chunk, int tickSpeed) {
        ServerLevel self = (ServerLevel)this;
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        ProfilerFiller profiler = Profiler.get();
        profiler.push("iceandsnow");
        for (int i = 0; i < tickSpeed; ++i) {
            if (self.getRandom().nextInt(48) != 0) continue;
            self.tickPrecipitation(self.getBlockRandomPos(minX, 0, minZ, 15));
        }
        profiler.popPush("tickBlocks");
        if (tickSpeed > 0) {
            CubeMap map = null;
            ServerChunkCache serverChunkCache = self.getChunkSource();
            if (serverChunkCache instanceof CubeSource) {
                CubeSource source = (CubeSource)serverChunkCache;
                map = source.chunkcutter$cubeMap();
            }
            int radius = CubeRange.simulationRadiusBlocks((Level)self);
            for (int sectionY = self.getMinSectionY(); sectionY <= self.getMaxSectionY(); ++sectionY) {
                LevelChunkSection section;
                if (map != null) {
                    CubeChunk cube = map.get(CloPos.cube(chunkPos.x(), sectionY, chunkPos.z()));
                    section = cube == null ? null : cube.section();
                } else {
                    int idx = chunk.getSectionIndexFromSectionY(sectionY);
                    LevelChunkSection levelChunkSection = section = idx >= 0 && idx < chunk.getSectionsCount() ? chunk.getSections()[idx] : null;
                }
                if (section == null || !section.isRandomlyTicking() || !CubeRange.withinRadius((Level)self, (double)minX + 8.0, (double)(sectionY * 16) + 8.0, (double)minZ + 8.0, radius)) continue;
                int minYInSection = SectionPos.sectionToBlockCoord((int)sectionY);
                for (int i = 0; i < tickSpeed; ++i) {
                    FluidState fluidState;
                    BlockPos pos = self.getBlockRandomPos(minX, minYInSection, minZ, 15);
                    profiler.push("randomTick");
                    BlockState blockState = section.getBlockState(pos.getX() - minX, pos.getY() - minYInSection, pos.getZ() - minZ);
                    if (blockState.isRandomlyTicking()) {
                        blockState.randomTick(self, pos, self.getRandom());
                    }
                    if ((fluidState = blockState.getFluidState()).isRandomlyTicking()) {
                        fluidState.randomTick(self, pos, self.getRandom());
                    }
                    profiler.pop();
                }
            }
        }
        profiler.pop();
    }

    private static void chunkcutter$logGeneratedSections(int cx, int cz, LevelChunkSection[] sections) {
    }

    private static void chunkcutter$manageSections(ServerLevel level, LevelChunk chunk, int minX, int minZ, LevelChunkSection[] sections) {
        ServerChunkCache sy2;
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)level.dimension());
        PalettedContainerFactory factory = level.palettedContainerFactory();
        int cx = minX >> 4;
        int cz = minZ >> 4;
        ServerLevelMixin.chunkcutter$logGeneratedSections(cx, cz, sections);
        long now = level.getGameTime();
        ArrayList<SectionsPayload.SectionEntry> sendEntries = new ArrayList<SectionsPayload.SectionEntry>();
        int keepSections = clm.maxKeepSections();
        int lo = Integer.MAX_VALUE;
        int hi = Integer.MIN_VALUE;
        for (ServerPlayer p : level.players()) {
            int psy = SectionPos.blockToSectionCoord((int)p.getBlockY());
            lo = Math.min(lo, psy - keepSections);
            hi = Math.max(hi, psy + keepSections);
        }
        if (lo > hi) {
            return;
        }
        for (int i = 0; i < sections.length; ++i) {
            int sy2 = chunk.getSectionYFromSectionIndex(i);
            if (sy2 < lo || sy2 > hi) continue;
            CloPos clo = CloPos.cube(cx, sy2, cz);
            long key = clo.asLong();
            boolean wanted = clm.wants(cx, sy2, cz);
            boolean keep = clm.keep(cx, sy2, cz);
            if (!wanted && !keep) continue;
            clm.cancelUnload(key);
        }
        if (!sendEntries.isEmpty() && (sy2 = level.getChunkSource()) instanceof ServerChunkCacheAccessor) {
            ServerChunkCacheAccessor scc = (ServerChunkCacheAccessor)sy2;
            SectionsPayload payload = new SectionsPayload(level.dimension().identifier(), sendEntries);
            for (ServerPlayer player : scc.chunkcutter$chunkMap().getPlayers(new ChunkPos(cx, cz), false)) {
                ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)payload);
            }
        }
    }

    private static void chunkcutter$restoreBlockEntities(LevelChunk chunk, LevelChunkSection section, List<CompoundTag> beTags, ServerLevel level) {
        for (CompoundTag t : beTags) {
            BlockState st;
            BlockPos bp = new BlockPos(t.getIntOr("x", 0), t.getIntOr("y", 0), t.getIntOr("z", 0));
            BlockEntity be = BlockEntity.loadStatic((BlockPos)bp, (BlockState)(st = section.getBlockState(bp.getX() & 0xF, bp.getY() & 0xF, bp.getZ() & 0xF)), (CompoundTag)t, (HolderLookup.Provider)level.registryAccess());
            if (be == null) continue;
            chunk.setBlockEntity(be);
        }
    }

    private static SectionsPayload.SectionEntry chunkcutter$encode(LevelChunkSection section, long cubePos) {
        ByteBuf raw = Unpooled.buffer();
        FriendlyByteBuf fb = new FriendlyByteBuf(raw);
        section.write(fb);
        byte[] data = new byte[fb.writerIndex()];
        fb.getBytes(0, data);
        return new SectionsPayload.SectionEntry(cubePos, data, null, null, List.of());
    }

    private static void chunkcutter$log(String action, int cx, int sy, int cz, boolean air, int be) {
    }

    @Inject(method={"tickNonPassenger"}, at={@At(value="HEAD")}, cancellable=true)
    private void chunkcutter$cubeEntityTick(Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayer) {
            return;
        }
        Level self = (Level)this;
        if (!CubeRange.withinRadius(self, entity.getX(), entity.getY(), entity.getZ(), CubeRange.simulationRadiusBlocks(self))) {
            ci.cancel();
        }
    }
}


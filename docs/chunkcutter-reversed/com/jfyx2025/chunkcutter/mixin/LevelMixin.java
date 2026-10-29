/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package com.jfyx2025.chunkcutter.mixin;

import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeLoadManager;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.cubic.VerticalBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={Level.class})
public abstract class LevelMixin {
    public int getMinY() {
        Level self = (Level)this;
        return VerticalBounds.isEnabled() ? VerticalBounds.engineMinY() : self.dimensionType().minY();
    }

    public int getHeight() {
        Level self = (Level)this;
        return VerticalBounds.isEnabled() ? VerticalBounds.engineHeight() : self.dimensionType().height();
    }

    @Overwrite
    public BlockState getBlockState(BlockPos pos) {
        Level self = (Level)this;
        if (!VerticalBounds.isEnabled()) {
            LevelChunk chunk = self.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
            return chunk == null ? Blocks.AIR.defaultBlockState() : chunk.getBlockState(pos);
        }
        if (!self.isInValidBounds(pos)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        LevelChunkSection section = LevelMixin.chunkcutter$cubeSection(self, pos);
        if (section == null) {
            section = LevelMixin.chunkcutter$tryLoadSection(self, pos);
        }
        if (section != null) {
            return section.getBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
        }
        if (self instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)self;
            CubeProvider.enqueueMissingSection(serverLevel, SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ()));
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Overwrite
    public FluidState getFluidState(BlockPos pos) {
        Level self = (Level)this;
        if (!VerticalBounds.isEnabled()) {
            LevelChunk chunk = self.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
            return chunk == null ? Fluids.EMPTY.defaultFluidState() : chunk.getFluidState(pos);
        }
        if (!self.isInValidBounds(pos)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        LevelChunkSection section = LevelMixin.chunkcutter$cubeSection(self, pos);
        if (section == null) {
            section = LevelMixin.chunkcutter$tryLoadSection(self, pos);
        }
        if (section != null) {
            return section.getFluidState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
        }
        if (self instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)self;
            CubeProvider.enqueueMissingSection(serverLevel, SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ()));
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Overwrite
    public boolean shouldTickBlocksAt(BlockPos pos) {
        CubeLoadManager clm;
        Level self = (Level)this;
        if (!VerticalBounds.isEnabled()) {
            return self.shouldTickBlocksAt(ChunkPos.pack((BlockPos)pos));
        }
        if (self.getChunkSource() instanceof CubeSource && !(clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)self.dimension())).ticking(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ()))) {
            return false;
        }
        return self.shouldTickBlocksAt(ChunkPos.pack((BlockPos)pos));
    }

    private static LevelChunkSection chunkcutter$tryLoadSection(Level level, BlockPos pos) {
        if (!VerticalBounds.isEnabled()) {
            return null;
        }
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (!serverLevel.getServer().isSameThread()) {
            return null;
        }
        ChunkSource chunkSource = level.getChunkSource();
        if (!(chunkSource instanceof CubeSource)) {
            return null;
        }
        CubeSource source = (CubeSource)chunkSource;
        int sx = SectionPos.blockToSectionCoord((int)pos.getX());
        int sy = SectionPos.blockToSectionCoord((int)pos.getY());
        int sz = SectionPos.blockToSectionCoord((int)pos.getZ());
        CubeLoadManager clm = ChunkCutterMod.cubeLoadManager((ResourceKey<Level>)serverLevel.dimension());
        if (!clm.wants(sx, sy, sz) && !clm.keep(sx, sy, sz)) {
            return null;
        }
        CloPos clo = CloPos.cube(sx, sy, sz);
        CubeMap map = source.chunkcutter$cubeMap();
        if (map == null) {
            return null;
        }
        CubeChunk cube = map.get(clo);
        if (cube != null) {
            return cube.section();
        }
        if (!CubeProvider.isPending(serverLevel, clo)) {
            CubeProvider.requestLoad(serverLevel, clo, serverLevel.palettedContainerFactory(), map);
        }
        return null;
    }

    private static LevelChunkSection chunkcutter$cubeSection(Level level, BlockPos pos) {
        if (!VerticalBounds.isEnabled()) {
            return null;
        }
        ChunkSource chunkSource = level.getChunkSource();
        if (chunkSource instanceof CubeSource) {
            CubeSource source = (CubeSource)chunkSource;
            CubeMap map = source.chunkcutter$cubeMap();
            if (map == null) {
                return null;
            }
            CubeChunk cube = map.get(CloPos.cube(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ())));
            if (cube != null) {
                return cube.section();
            }
        }
        return null;
    }
}


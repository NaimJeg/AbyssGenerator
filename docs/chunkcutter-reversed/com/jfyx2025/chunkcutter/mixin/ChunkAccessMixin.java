/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.QuartPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.ProblemReporter$PathElement
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.BiomeGenerationSettings
 *  net.minecraft.world.level.biome.BiomeManager$NoiseBiomeSource
 *  net.minecraft.world.level.biome.BiomeResolver
 *  net.minecraft.world.level.biome.Climate$Sampler
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Block$UpdateFlags
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkAccess$ChunkPathElement
 *  net.minecraft.world.level.chunk.ChunkAccess$PackedTicks
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.LightChunk
 *  net.minecraft.world.level.chunk.PalettedContainerFactory
 *  net.minecraft.world.level.chunk.StructureAccess
 *  net.minecraft.world.level.chunk.UpgradeData
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.gameevent.GameEventListenerRegistry
 *  net.minecraft.world.level.levelgen.BelowZeroRetrogen
 *  net.minecraft.world.level.levelgen.Heightmap
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.NoiseChunk
 *  net.minecraft.world.level.levelgen.blending.BlendingData
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.lighting.ChunkSkyLightSources
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.ticks.TickContainerAccess
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import com.google.common.collect.Sets;
import com.jfyx2025.chunkcutter.cubic.ChunkCutterHeightOverride;
import com.jfyx2025.chunkcutter.cubic.ChunkCutterSectionWrite;
import com.jfyx2025.chunkcutter.cubic.SectionChunkFactory;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.TickContainerAccess;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ChunkAccess.class})
public abstract class ChunkAccessMixin
implements LightChunk,
StructureAccess,
BiomeManager.NoiseBiomeSource,
AttachmentTarget,
ChunkCutterSectionWrite,
ChunkCutterHeightOverride {
    @Shadow
    public static int NO_FILLED_SECTION;
    @Shadow
    private static LongSet EMPTY_REFERENCE_SET;
    @Shadow
    protected @Nullable ShortList[] postProcessing;
    @Shadow
    private volatile boolean unsaved;
    @Shadow
    private volatile boolean isLightCorrect;
    @Shadow
    protected ChunkPos chunkPos;
    @Shadow
    private long inhabitedTime;
    @Deprecated
    @Shadow
    private @Nullable BiomeGenerationSettings carverBiomeSettings;
    @Shadow
    protected @Nullable NoiseChunk noiseChunk;
    @Shadow
    protected UpgradeData upgradeData;
    @Shadow
    protected @Nullable BlendingData blendingData;
    @Shadow
    protected Map<Heightmap.Types, Heightmap> heightmaps;
    @Shadow
    protected ChunkSkyLightSources skyLightSources;
    @Shadow
    private Map<Structure, StructureStart> structureStarts;
    @Shadow
    private Map<Structure, LongSet> structuresRefences;
    @Shadow
    protected Map<BlockPos, CompoundTag> pendingBlockEntities;
    @Shadow
    protected Map<BlockPos, BlockEntity> blockEntities;
    @Shadow
    protected LevelHeightAccessor levelHeightAccessor;
    @Shadow
    protected LevelChunkSection[] sections;
    private LevelHeightAccessor chunkcutter$heightOverride;
    private static volatile LevelChunkSection chunkcutter$emptyRef;

    @Override
    public LevelHeightAccessor chunkcutter$getHeightOverride() {
        return this.chunkcutter$heightOverride;
    }

    @Override
    public void chunkcutter$setHeightOverride(LevelHeightAccessor heightAccessor) {
        this.chunkcutter$heightOverride = heightAccessor;
    }

    @Overwrite
    public static void replaceMissingSections(PalettedContainerFactory containerFactory, LevelChunkSection[] sections) {
        LevelChunkSection empty = ChunkAccessMixin.chunkcutter$emptyFor(containerFactory);
        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] != null) continue;
            sections[i] = empty;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static LevelChunkSection chunkcutter$emptyFor(PalettedContainerFactory containerFactory) {
        LevelChunkSection empty = chunkcutter$emptyRef;
        if (empty != null) return empty;
        Class<ChunkAccessMixin> clazz = ChunkAccessMixin.class;
        synchronized (ChunkAccessMixin.class) {
            empty = chunkcutter$emptyRef;
            if (empty != null) return empty;
            chunkcutter$emptyRef = empty = new LevelChunkSection(containerFactory);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return empty;
        }
    }

    @Overwrite
    public GameEventListenerRegistry getListenerRegistry(int section) {
        return GameEventListenerRegistry.NOOP;
    }

    @Overwrite
    public @Nullable BlockState setBlockState(BlockPos pos, BlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    @Shadow
    public abstract @Nullable BlockState setBlockState(BlockPos var1, BlockState var2, @Block.UpdateFlags int var3);

    @Shadow
    public abstract void setBlockEntity(BlockEntity var1);

    @Shadow
    public abstract void addEntity(Entity var1);

    public int getHighestFilledSectionIndex() {
        LevelChunkSection[] sections = this.getSections();
        for (int sectionIndex = sections.length - 1; sectionIndex >= 0; --sectionIndex) {
            LevelChunkSection section = sections[sectionIndex];
            if (section == null || section.hasOnlyAir()) continue;
            return sectionIndex;
        }
        return -1;
    }

    @Deprecated(forRemoval=true)
    @Overwrite
    public int getHighestSectionPosition() {
        int sectionIndex = this.getHighestFilledSectionIndex();
        return sectionIndex == -1 ? this.getMinY() : SectionPos.sectionToBlockCoord((int)this.getSectionYFromSectionIndex(sectionIndex));
    }

    @Overwrite
    public Set<BlockPos> getBlockEntitiesPos() {
        HashSet result = Sets.newHashSet(this.pendingBlockEntities.keySet());
        result.addAll(this.blockEntities.keySet());
        return result;
    }

    @Overwrite
    public LevelChunkSection[] getSections() {
        return this.sections;
    }

    private PalettedContainerFactory chunkcutter$factory() {
        ChunkAccessMixin chunkAccessMixin = this;
        if (chunkAccessMixin instanceof SectionChunkFactory) {
            SectionChunkFactory provider = (SectionChunkFactory)((Object)chunkAccessMixin);
            return provider.chunkcutter$factory();
        }
        return ((Level)this.levelHeightAccessor).palettedContainerFactory();
    }

    @Overwrite
    public LevelChunkSection getSection(int sectionIndex) {
        LevelChunkSection[] arr = this.sections;
        LevelChunkSection section = arr[sectionIndex];
        if (section == chunkcutter$emptyRef && !(this instanceof LevelChunk)) {
            arr[sectionIndex] = section = new LevelChunkSection(this.chunkcutter$factory());
        }
        return section;
    }

    @Override
    public LevelChunkSection chunkcutter$getSectionForWrite(int sectionIndex) {
        LevelChunkSection[] arr = this.sections;
        if (sectionIndex < 0 || sectionIndex >= arr.length) {
            return new LevelChunkSection(this.chunkcutter$factory());
        }
        LevelChunkSection section = arr[sectionIndex];
        if (section == null || section == chunkcutter$emptyRef) {
            arr[sectionIndex] = section = new LevelChunkSection(this.chunkcutter$factory());
        }
        return section;
    }

    @Override
    public void chunkcutter$clearSection(int sectionIndex) {
        LevelChunkSection empty = chunkcutter$emptyRef;
        if (empty == null) {
            empty = ChunkAccessMixin.chunkcutter$emptyFor(this.chunkcutter$factory());
        }
        this.sections[sectionIndex] = empty;
    }

    @Overwrite
    public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Overwrite
    public void setHeightmap(Heightmap.Types key, long[] data) {
        this.getOrCreateHeightmapUnprimed(key).setRawData((ChunkAccess)this, key, data);
    }

    @Overwrite
    public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types type) {
        return this.heightmaps.computeIfAbsent(type, k -> new Heightmap((ChunkAccess)this, k));
    }

    @Overwrite
    public boolean hasPrimedHeightmap(Heightmap.Types type) {
        return this.heightmaps.get(type) != null;
    }

    @Overwrite
    public int getHeight(Heightmap.Types type, int x, int z) {
        Heightmap heightmap = this.heightmaps.get(type);
        if (heightmap == null) {
            if (!SharedConstants.IS_RUNNING_IN_IDE || (ChunkAccess)this instanceof LevelChunk) {
                // empty if block
            }
            Heightmap.primeHeightmaps((ChunkAccess)((ChunkAccess)this), EnumSet.of(type));
            heightmap = this.heightmaps.get(type);
        }
        return heightmap.getFirstAvailable(x & 0xF, z & 0xF) - 1;
    }

    @Overwrite
    public ChunkPos getPos() {
        return this.chunkPos;
    }

    @Overwrite
    public @Nullable StructureStart getStartForStructure(Structure structure) {
        return this.structureStarts.get(structure);
    }

    @Overwrite
    public void setStartForStructure(Structure structure, StructureStart structureStart) {
        this.structureStarts.put(structure, structureStart);
        this.markUnsaved();
    }

    @Overwrite
    public Map<Structure, StructureStart> getAllStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    @Overwrite
    public void setAllStarts(Map<Structure, StructureStart> starts) {
        this.structureStarts.clear();
        this.structureStarts.putAll(starts);
        this.markUnsaved();
    }

    @Overwrite
    public LongSet getReferencesForStructure(Structure structure) {
        return this.structuresRefences.getOrDefault(structure, EMPTY_REFERENCE_SET);
    }

    @Overwrite
    public void addReferenceForStructure(Structure structure, long reference) {
        this.structuresRefences.computeIfAbsent(structure, k -> new LongOpenHashSet()).add(reference);
        this.markUnsaved();
    }

    @Overwrite
    public Map<Structure, LongSet> getAllReferences() {
        return Collections.unmodifiableMap(this.structuresRefences);
    }

    @Overwrite
    public void setAllReferences(Map<Structure, LongSet> data) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll(data);
        this.markUnsaved();
    }

    @Overwrite
    public boolean isYSpaceEmpty(int yStartInclusive, int yEndInclusive) {
        if (yStartInclusive < this.getMinY()) {
            yStartInclusive = this.getMinY();
        }
        if (yEndInclusive > this.getMaxY()) {
            yEndInclusive = this.getMaxY();
        }
        for (int y = yStartInclusive; y <= yEndInclusive; y += 16) {
            if (this.getSection(this.getSectionIndex(y)).hasOnlyAir()) continue;
            return false;
        }
        return true;
    }

    @Overwrite
    public void markUnsaved() {
        this.unsaved = true;
    }

    @Overwrite
    public boolean tryMarkSaved() {
        if (this.unsaved) {
            this.unsaved = false;
            return true;
        }
        return false;
    }

    @Overwrite
    public boolean isUnsaved() {
        return this.unsaved;
    }

    @Shadow
    public abstract ChunkStatus getPersistedStatus();

    public ChunkStatus getHighestGeneratedStatus() {
        ChunkStatus status = this.getPersistedStatus();
        BelowZeroRetrogen belowZeroRetrogen = this.getBelowZeroRetrogen();
        if (belowZeroRetrogen != null) {
            ChunkStatus targetStatus = belowZeroRetrogen.targetStatus();
            return ChunkStatus.max((ChunkStatus)targetStatus, (ChunkStatus)status);
        }
        return status;
    }

    @Shadow
    public abstract void removeBlockEntity(BlockPos var1);

    public void markPosForPostProcessing(BlockPos blockPos) {
    }

    @Overwrite
    public @Nullable ShortList[] getPostProcessing() {
        return this.postProcessing;
    }

    @Overwrite
    public void addPackedPostProcess(ShortList packedOffsets, int sectionIndex) {
        ChunkAccessMixin.getOrCreateOffsetList(this.getPostProcessing(), sectionIndex).addAll(packedOffsets);
    }

    @Overwrite
    public void setBlockEntityNbt(CompoundTag entityTag) {
        BlockPos posFromTag = BlockEntity.getPosFromTag((ChunkPos)this.chunkPos, (CompoundTag)entityTag);
        if (!this.blockEntities.containsKey(posFromTag)) {
            this.pendingBlockEntities.put(posFromTag, entityTag);
        }
    }

    @Overwrite
    public @Nullable CompoundTag getBlockEntityNbt(BlockPos blockPos) {
        return this.pendingBlockEntities.get(blockPos);
    }

    @Shadow
    public abstract @Nullable CompoundTag getBlockEntityNbtForSaving(BlockPos var1, HolderLookup.Provider var2);

    public final void findBlockLightSources(BiConsumer<BlockPos, BlockState> consumer) {
        this.findBlocks(state -> state.getLightEmission() != 0, consumer);
    }

    @Overwrite
    public void findBlocks(Predicate<BlockState> predicate, BiConsumer<BlockPos, BlockState> consumer) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        LevelChunkSection[] arr = this.sections;
        for (int i = 0; i < arr.length; ++i) {
            LevelChunkSection section = arr[i];
            if (section == null || !section.maybeHas(predicate)) continue;
            int sectionY = this.getSectionYFromSectionIndex(i);
            BlockPos origin = SectionPos.of((ChunkPos)this.chunkPos, (int)sectionY).origin();
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (!predicate.test(state)) continue;
                        consumer.accept((BlockPos)mutablePos.setWithOffset((Vec3i)origin, x, y, z), state);
                    }
                }
            }
        }
    }

    @Shadow
    public abstract TickContainerAccess<Block> getBlockTicks();

    @Shadow
    public abstract TickContainerAccess<Fluid> getFluidTicks();

    public void collectBiomesInPalette(Set<Holder<Biome>> output) {
        for (LevelChunkSection section : this.sections) {
            if (section == null) continue;
            section.getBiomes().forEachInPalette(output::add);
        }
    }

    @Overwrite
    public boolean canBeSerialized() {
        return true;
    }

    @Shadow
    public abstract ChunkAccess.PackedTicks getTicksForSerialization(long var1);

    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Overwrite
    public boolean isOldNoiseGeneration() {
        return this.blendingData != null;
    }

    @Overwrite
    public @Nullable BlendingData getBlendingData() {
        return this.blendingData;
    }

    @Overwrite
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Overwrite
    public void incrementInhabitedTime(long inhabitedTimeDelta) {
        this.inhabitedTime += inhabitedTimeDelta;
    }

    @Overwrite
    public void setInhabitedTime(long inhabitedTime) {
        this.inhabitedTime = inhabitedTime;
    }

    @Overwrite
    public static ShortList getOrCreateOffsetList(@Nullable ShortList[] list, int sectionIndex) {
        ShortList result = list[sectionIndex];
        if (result == null) {
            list[sectionIndex] = result = new ShortArrayList();
        }
        return result;
    }

    @Overwrite
    public boolean isLightCorrect() {
        return this.isLightCorrect;
    }

    @Overwrite
    public void setLightCorrect(boolean isLightCorrect) {
        this.isLightCorrect = isLightCorrect;
        this.markUnsaved();
    }

    @Overwrite
    public int getMinY() {
        LevelHeightAccessor override = this.chunkcutter$heightOverride;
        return override != null ? override.getMinY() : this.levelHeightAccessor.getMinY();
    }

    @Overwrite
    public int getHeight() {
        LevelHeightAccessor override = this.chunkcutter$heightOverride;
        return override != null ? override.getHeight() : this.levelHeightAccessor.getHeight();
    }

    @Overwrite
    public NoiseChunk getOrCreateNoiseChunk(Function<ChunkAccess, NoiseChunk> factory) {
        if (this.noiseChunk == null) {
            this.noiseChunk = factory.apply((ChunkAccess)this);
        }
        return this.noiseChunk;
    }

    @Deprecated
    @Overwrite
    public BiomeGenerationSettings carverBiome(Supplier<BiomeGenerationSettings> source) {
        if (this.carverBiomeSettings == null) {
            this.carverBiomeSettings = source.get();
        }
        return this.carverBiomeSettings;
    }

    @Overwrite
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ) {
        try {
            int quartMinY = QuartPos.fromBlock((int)this.getMinY());
            int quartMaxY = quartMinY + QuartPos.fromBlock((int)this.getHeight()) - 1;
            int clampedQuartY = Mth.clamp((int)quartY, (int)quartMinY, (int)quartMaxY);
            int sectionIndex = this.getSectionIndex(QuartPos.toBlock((int)clampedQuartY));
            return this.getSection(sectionIndex).getNoiseBiome(quartX & 3, clampedQuartY & 3, quartZ & 3);
        }
        catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable((Throwable)t, (String)"Getting biome");
            CrashReportCategory category = report.addCategory("Biome being got");
            category.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, (int)quartX, (int)quartY, (int)quartZ));
            throw new ReportedException(report);
        }
    }

    @Overwrite
    public void fillBiomesFromNoise(BiomeResolver biomeResolver, Climate.Sampler sampler) {
        ChunkPos pos = this.getPos();
        int quartMinX = QuartPos.fromBlock((int)pos.getMinBlockX());
        int quartMinZ = QuartPos.fromBlock((int)pos.getMinBlockZ());
        LevelHeightAccessor heightAccessor = this.getHeightAccessorForGeneration();
        for (int sectionY = heightAccessor.getMinSectionY(); sectionY <= heightAccessor.getMaxSectionY(); ++sectionY) {
            LevelChunkSection section = ((ChunkCutterSectionWrite)this).chunkcutter$getSectionForWrite(this.getSectionIndexFromSectionY(sectionY));
            int quartMinY = QuartPos.fromSection((int)sectionY);
            section.fillBiomesFromNoise(biomeResolver, sampler, quartMinX, quartMinY, quartMinZ);
        }
    }

    @Overwrite
    public boolean hasAnyStructureReferences() {
        return !this.getAllReferences().isEmpty();
    }

    @Overwrite
    public @Nullable BelowZeroRetrogen getBelowZeroRetrogen() {
        return null;
    }

    @Overwrite
    public boolean isUpgrading() {
        return this.getBelowZeroRetrogen() != null;
    }

    @Overwrite
    public LevelHeightAccessor getHeightAccessorForGeneration() {
        return this;
    }

    @Overwrite
    public void initializeLightSources() {
        this.skyLightSources.fillFrom((ChunkAccess)this);
    }

    @Overwrite
    public ChunkSkyLightSources getSkyLightSources() {
        return this.skyLightSources;
    }

    @Overwrite
    public static ProblemReporter.PathElement problemPath(ChunkPos pos) {
        return new ChunkAccess.ChunkPathElement(pos);
    }

    @Overwrite
    public ProblemReporter.PathElement problemPath() {
        return ChunkAccessMixin.problemPath(this.getPos());
    }
}


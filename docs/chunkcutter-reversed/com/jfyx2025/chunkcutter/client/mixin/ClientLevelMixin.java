/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.Camera
 *  net.minecraft.client.ClientClockManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockTintCache
 *  net.minecraft.client.color.block.BlockTintSource
 *  net.minecraft.client.gui.screens.WinScreen
 *  net.minecraft.client.multiplayer.CacheSlot
 *  net.minecraft.client.multiplayer.CacheSlot$Cleaner
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.multiplayer.ClientExplosionTracker
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.ClientLevel$ClientLevelData
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler
 *  net.minecraft.client.particle.FireworkParticles$Starter
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.TerrainParticle
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.EndFlashState
 *  net.minecraft.client.renderer.LevelEventHandler
 *  net.minecraft.client.renderer.block.BlockAndTintGetter
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.extract.LevelExtractor
 *  net.minecraft.client.resources.sounds.DirectionalSoundInstance
 *  net.minecraft.client.resources.sounds.EntityBoundSoundInstance
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Cursor3D
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Position
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ExplosionParticleInfo
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.server.level.BlockDestructionProgress
 *  net.minecraft.server.level.ParticleStatus
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.profiling.Profiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.util.profiling.Zone
 *  net.minecraft.util.profiling.jfr.JvmProfiler
 *  net.minecraft.util.random.WeightedList
 *  net.minecraft.world.TickRateManager
 *  net.minecraft.world.attribute.AmbientParticle
 *  net.minecraft.world.attribute.EnvironmentAttributeSystem
 *  net.minecraft.world.attribute.EnvironmentAttributeSystem$Builder
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntitySelector
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragonPart
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.component.FireworkExplosion
 *  net.minecraft.world.item.crafting.RecipeAccess
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.CardinalLighting
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraft.world.level.ExplosionDamageCalculator
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Block$UpdateFlags
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.FuelValues
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.border.WorldBorder
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntityTickList
 *  net.minecraft.world.level.entity.LevelEntityGetter
 *  net.minecraft.world.level.entity.TransientEntitySectionManager
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.saveddata.maps.MapId
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  net.minecraft.world.level.storage.LevelData$RespawnData
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.ticks.BlackholeTickAccess
 *  net.minecraft.world.ticks.LevelTickAccess
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.client.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.ClientClockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientExplosionTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.EndFlashState;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.resources.sounds.DirectionalSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ClientLevel.class})
public abstract class ClientLevelMixin
implements BlockAndTintGetter,
CacheSlot.Cleaner<ClientLevel> {
    @Shadow
    public static Component DEFAULT_QUIT_MESSAGE;
    @Shadow
    private static double FLUID_PARTICLE_SPAWN_OFFSET;
    @Shadow
    private static int NORMAL_LIGHT_UPDATES_PER_FRAME;
    @Shadow
    private static int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD;
    @Shadow
    private static float RAIN_PARTICLES_PER_BLOCK;
    @Shadow
    private static int RAIN_RADIUS;
    @Shadow
    private EntityTickList tickingEntities;
    @Shadow
    private TransientEntitySectionManager<Entity> entityStorage;
    @Shadow
    private ClientPacketListener connection;
    @Shadow
    private LevelExtractor levelExtractor;
    @Shadow
    private LevelEventHandler levelEventHandler;
    @Shadow
    private ClientLevel.ClientLevelData clientLevelData;
    @Shadow
    private TickRateManager tickRateManager;
    @Shadow
    private @Nullable EndFlashState endFlashState;
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private List<AbstractClientPlayer> players;
    @Shadow
    private List<EnderDragonPart> dragonParts;
    @Shadow
    private Map<MapId, MapItemSavedData> mapData;
    @Shadow
    private int skyFlashTime;
    @Shadow
    private int rainSoundTime;
    @Shadow
    private Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches;
    @Shadow
    private ClientChunkCache chunkSource;
    @Shadow
    private Deque<Runnable> lightUpdateQueue;
    @Shadow
    private int serverSimulationDistance;
    @Shadow
    private BlockStatePredictionHandler blockStatePredictionHandler;
    @Shadow
    private Set<BlockEntity> globallyRenderedBlockEntities;
    @Shadow
    private ClientExplosionTracker explosionTracker;
    @Shadow
    private WorldBorder worldBorder;
    @Shadow
    private EnvironmentAttributeSystem environmentAttributes;
    @Shadow
    private Int2ObjectMap<BlockDestructionProgress> destroyingBlocks;
    @Shadow
    private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;
    @Shadow
    private int seaLevel;
    @Shadow
    private static Set<Item> MARKER_PARTICLE_ITEMS;

    private static double chunkcutter$squareDistanceSqr(Vec3 camera, double x, double y, double z) {
        double max = Math.max(Math.abs(camera.x - x), Math.max(Math.abs(camera.y - y), Math.abs(camera.z - z)));
        return max * max;
    }

    @Overwrite
    public void handleBlockChangedAck(int sequence) {
        if (SharedConstants.DEBUG_BLOCK_BREAK) {
            // empty if block
        }
        ((ClientLevel)this).blockStatePredictionHandler.endPredictionsUpTo(sequence, (ClientLevel)this);
    }

    @Overwrite
    public void onBlockEntityAdded(BlockEntity blockEntity) {
        BlockEntityRenderer renderer = ((ClientLevel)this).minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (renderer != null && renderer.shouldRenderOffScreen()) {
            ((ClientLevel)this).globallyRenderedBlockEntities.add(blockEntity);
        }
    }

    @Overwrite
    public Set<BlockEntity> getGloballyRenderedBlockEntities() {
        return ((ClientLevel)this).globallyRenderedBlockEntities;
    }

    @Overwrite
    public void syncBlockState(BlockPos pos, BlockState state, @Nullable Vec3 playerPos) {
        BlockState oldState = ((ClientLevel)this).getBlockState(pos);
        if (oldState != state) {
            ((ClientLevel)this).setBlock(pos, state, 19);
            LocalPlayer player = ((ClientLevel)this).minecraft.player;
            if (playerPos != null && (ClientLevel)this == player.level() && player.isColliding(pos, state)) {
                player.absSnapTo(playerPos.x, playerPos.y, playerPos.z);
            }
        }
    }

    @Overwrite
    public BlockStatePredictionHandler getBlockStatePredictionHandler() {
        return ((ClientLevel)this).blockStatePredictionHandler;
    }

    @Overwrite
    public EnvironmentAttributeSystem.Builder addEnvironmentAttributeLayers(EnvironmentAttributeSystem.Builder environmentAttributes) {
        environmentAttributes.addDefaultLayers((Level)((ClientLevel)this));
        int flashColor = ARGB.color((int)204, (int)204, (int)255);
        environmentAttributes.addTimeBasedLayer(EnvironmentAttributes.SKY_COLOR, (skyColor, cacheTickId) -> ((ClientLevel)this).getSkyFlashTime() > 0 ? ARGB.srgbLerp((float)0.22f, (int)skyColor, (int)flashColor) : skyColor);
        environmentAttributes.addTimeBasedLayer(EnvironmentAttributes.SKY_LIGHT_FACTOR, (skyFactor, cacheTickId) -> Float.valueOf(((ClientLevel)this).getSkyFlashTime() > 0 ? 1.0f : skyFactor.floatValue()));
        return environmentAttributes;
    }

    @Overwrite
    public void queueLightUpdate(Runnable update) {
        ((ClientLevel)this).lightUpdateQueue.add(update);
    }

    @Overwrite
    public void pollLightUpdates() {
        Runnable update;
        int size = ((ClientLevel)this).lightUpdateQueue.size();
        int lightUpdatesPerFrame = size < 1000 ? Math.max(10, size / 10) : size;
        for (int i = 0; i < lightUpdatesPerFrame && (update = (Runnable)((ClientLevel)this).lightUpdateQueue.poll()) != null; ++i) {
            update.run();
        }
    }

    @Overwrite
    public @Nullable EndFlashState endFlashState() {
        return ((ClientLevel)this).endFlashState;
    }

    @Overwrite
    public void tick(BooleanSupplier haveTime) {
        ((ClientLevel)this).updateSkyBrightness();
        if (((ClientLevel)this).tickRateManager().runsNormally()) {
            ((ClientLevel)this).getWorldBorder().tick();
            ((ClientLevel)this).tickTime();
            ((ClientLevel)this).tickWeatherEffects();
            ((ClientLevel)this).removeBlockBreakingProgress();
        }
        if (((ClientLevel)this).skyFlashTime > 0) {
            ((ClientLevel)this).setSkyFlashTime(((ClientLevel)this).skyFlashTime - 1);
        }
        if (((ClientLevel)this).endFlashState != null) {
            ((ClientLevel)this).endFlashState.tick(((ClientLevel)this).getDefaultClockTime());
            if (((ClientLevel)this).endFlashState.flashStartedThisTick() && !(((ClientLevel)this).minecraft.gui.screen() instanceof WinScreen)) {
                ((ClientLevel)this).minecraft.getSoundManager().playDelayed((SoundInstance)new DirectionalSoundInstance(SoundEvents.WEATHER_END_FLASH, SoundSource.WEATHER, ((ClientLevel)this).random, ((ClientLevel)this).minecraft.gameRenderer.mainCamera(), ((ClientLevel)this).endFlashState.getXAngle(), ((ClientLevel)this).endFlashState.getYAngle()), 30);
            }
        }
        ((ClientLevel)this).explosionTracker.tick((ClientLevel)this);
        try (Zone ignored = Profiler.get().zone("blocks");){
            ((ClientLevel)this).chunkSource.tick(haveTime, true);
        }
        JvmProfiler.INSTANCE.onClientTick(((ClientLevel)this).minecraft.getFps());
        ((ClientLevel)this).environmentAttributes().invalidateTickCache();
    }

    @Overwrite
    public void tickWeatherEffects() {
        ParticleStatus particleStatus = (ParticleStatus)((ClientLevel)this).minecraft.options.particles().get();
        int weatherRadius = (Integer)((ClientLevel)this).minecraft.options.weatherRadius().get();
        float rainLevel = ((ClientLevel)this).getRainLevel(1.0f);
        if (!(rainLevel <= 0.0f)) {
            RandomSource random = RandomSource.createThreadLocalInstance((long)(((ClientLevel)this).getGameTime() * 312987231L));
            BlockPos cameraPosition = BlockPos.containing((Position)((ClientLevel)this).minecraft.gameRenderer.mainCamera().position());
            BlockPos rainParticlePosition = null;
            int weatherDiameter = 2 * weatherRadius + 1;
            int weatherArea = weatherDiameter * weatherDiameter;
            int rainParticles = (int)(0.225f * (float)weatherArea * rainLevel * rainLevel) / (particleStatus == ParticleStatus.DECREASED ? 2 : 1);
            for (int ii = 0; ii < rainParticles; ++ii) {
                int z;
                int x = random.nextInt(weatherDiameter) - weatherRadius;
                BlockPos heightmapPosition = ((ClientLevel)this).getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPosition.offset(x, 0, z = random.nextInt(weatherDiameter) - weatherRadius));
                if (heightmapPosition.getY() <= ((ClientLevel)this).getMinY() || heightmapPosition.getY() > cameraPosition.getY() + 10 || heightmapPosition.getY() < cameraPosition.getY() - 10 || ((ClientLevel)this).getPrecipitationAt(heightmapPosition) != Biome.Precipitation.RAIN) continue;
                rainParticlePosition = heightmapPosition.below();
                if (particleStatus == ParticleStatus.MINIMAL) break;
                double blockX = random.nextDouble();
                double blockZ = random.nextDouble();
                BlockState block = ((ClientLevel)this).getBlockState(rainParticlePosition);
                FluidState fluid = ((ClientLevel)this).getFluidState(rainParticlePosition);
                VoxelShape blockShape = block.getCollisionShape((BlockGetter)((ClientLevel)this), rainParticlePosition);
                double blockTop = blockShape.max(Direction.Axis.Y, blockX, blockZ);
                double fluidTop = fluid.getHeight((BlockGetter)((ClientLevel)this), rainParticlePosition);
                double particleY = Math.max(blockTop, fluidTop);
                SimpleParticleType particleType = !fluid.is(FluidTags.LAVA) && !block.is((Object)Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire((BlockState)block) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
                ((ClientLevel)this).addParticle((ParticleOptions)particleType, (double)rainParticlePosition.getX() + blockX, (double)rainParticlePosition.getY() + particleY, (double)rainParticlePosition.getZ() + blockZ, 0.0, 0.0, 0.0);
            }
            if (rainParticlePosition != null && random.nextInt(3) < ((ClientLevel)this).rainSoundTime++) {
                ((ClientLevel)this).rainSoundTime = 0;
                if (rainParticlePosition.getY() > cameraPosition.getY() + 1 && ((ClientLevel)this).getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPosition).getY() > Mth.floor((float)cameraPosition.getY())) {
                    ((ClientLevel)this).playLocalSound(rainParticlePosition, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1f, 0.5f, false);
                } else {
                    ((ClientLevel)this).playLocalSound(rainParticlePosition, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2f, 1.0f, false);
                }
            }
        }
    }

    @Overwrite
    public Biome.Precipitation getPrecipitationAt(BlockPos pos) {
        if (!((ClientLevel)this).chunkSource.hasChunk(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getZ()))) {
            return Biome.Precipitation.NONE;
        }
        Biome biome = (Biome)((ClientLevel)this).getBiome(pos).value();
        return biome.getPrecipitationAt(pos, ((ClientLevel)this).getSeaLevel());
    }

    @Overwrite
    public void removeBlockBreakingProgress() {
        long gameTime = ((ClientLevel)this).getGameTime();
        if (gameTime % 20L == 0L) {
            ObjectIterator iterator = ((ClientLevel)this).destroyingBlocks.values().iterator();
            while (iterator.hasNext()) {
                BlockDestructionProgress block = (BlockDestructionProgress)iterator.next();
                long updatedRenderTick = block.getUpdatedRenderTick();
                if (gameTime - updatedRenderTick <= 400L) continue;
                iterator.remove();
                ((ClientLevel)this).removeProgress(block);
            }
        }
    }

    @Overwrite
    public void removeProgress(BlockDestructionProgress block) {
        long pos = block.getPos().asLong();
        Set progresses = (Set)((ClientLevel)this).destructionProgress.get(pos);
        progresses.remove(block);
        if (progresses.isEmpty()) {
            ((ClientLevel)this).destructionProgress.remove(pos);
        }
    }

    @Overwrite
    public Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress() {
        return ((ClientLevel)this).destructionProgress;
    }

    @Overwrite
    public void tickTime() {
        long gameTime = ((ClientLevel)this).clientLevelData.getGameTime() + 1L;
        ((ClientLevel)this).clientLevelData.setGameTime(gameTime);
        ((ClientLevel)this).clockManager().tick(gameTime);
    }

    @Overwrite
    public void setTimeFromServer(long gameTime) {
        ((ClientLevel)this).clientLevelData.setGameTime(gameTime);
    }

    @Overwrite
    public Iterable<Entity> entitiesForRendering() {
        return ((ClientLevel)this).getEntities().getAll();
    }

    @Overwrite
    public void tickEntities() {
        ((ClientLevel)this).tickingEntities.forEach(entity -> {
            if (!(entity.isRemoved() || entity.isPassenger() || ((ClientLevel)this).tickRateManager.isEntityFrozen(entity))) {
                ((ClientLevel)this).guardEntityTick(arg_0 -> ((ClientLevel)((ClientLevel)this)).tickNonPassenger(arg_0), entity);
            }
        });
    }

    @Overwrite
    public boolean isTickingEntity(Entity entity) {
        return ((ClientLevel)this).tickingEntities.contains(entity);
    }

    @Overwrite
    public boolean shouldTickDeath(Entity entity) {
        return entity.chunkPosition().getChessboardDistance(((ClientLevel)this).minecraft.player.chunkPosition()) <= ((ClientLevel)this).serverSimulationDistance;
    }

    @Overwrite
    public void tickNonPassenger(Entity entity) {
        entity.setOldPosAndRot();
        ++entity.tickCount;
        Profiler.get().push(() -> ((Holder)entity.typeHolder()).getRegisteredName());
        entity.tick();
        Profiler.get().pop();
        for (Entity passenger : entity.getPassengers()) {
            ((ClientLevel)this).tickPassenger(entity, passenger);
        }
    }

    @Overwrite
    public void tickPassenger(Entity vehicle, Entity entity) {
        if (entity.isRemoved() || entity.getVehicle() != vehicle) {
            entity.stopRiding();
        } else if (entity instanceof Player || ((ClientLevel)this).tickingEntities.contains(entity)) {
            entity.setOldPosAndRot();
            ++entity.tickCount;
            entity.rideTick();
            for (Entity passenger : entity.getPassengers()) {
                ((ClientLevel)this).tickPassenger(entity, passenger);
            }
        }
    }

    @Overwrite
    public void update() {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("populateLightUpdates");
        ((ClientLevel)this).pollLightUpdates();
        profiler.popPush("runLightUpdates");
        ((ClientLevel)this).getChunkSource().getLightEngine().runLightUpdates();
        profiler.pop();
    }

    @Overwrite
    public void unload(LevelChunk levelChunk) {
        levelChunk.clearAllBlockEntities();
        ((ClientLevel)this).chunkSource.getLightEngine().setLightEnabled(levelChunk.getPos(), false);
        ((ClientLevel)this).entityStorage.stopTicking(levelChunk.getPos());
    }

    @Overwrite
    public boolean hasChunk(int chunkX, int chunkZ) {
        return true;
    }

    @Overwrite
    public int getEntityCount() {
        return ((ClientLevel)this).entityStorage.count();
    }

    @Overwrite
    public void addEntity(Entity entity) {
        ((ClientLevel)this).removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        ((ClientLevel)this).entityStorage.addEntity((EntityAccess)entity);
    }

    @Overwrite
    public void removeEntity(int id, Entity.RemovalReason reason) {
        Entity entity = (Entity)((ClientLevel)this).getEntities().get(id);
        if (entity != null) {
            entity.setRemoved(reason);
            entity.onClientRemoval();
        }
    }

    @Overwrite
    public List<Entity> getPushableEntities(Entity pusher, AABB boundingBox) {
        LocalPlayer player = ((ClientLevel)this).minecraft.player;
        return player != null && player != pusher && player.getBoundingBox().intersects(boundingBox) && EntitySelector.pushableBy((Entity)pusher).test(player) ? List.of(player) : List.of();
    }

    @Overwrite
    public @Nullable Entity getEntity(int id) {
        return (Entity)((ClientLevel)this).getEntities().get(id);
    }

    @Overwrite
    public void disconnect(Component message) {
        ((ClientLevel)this).connection.getConnection().disconnect(message);
        ((ClientLevel)this).minecraft.getPlayerSocialManager().getPresenceHandler().tryUpdatePresence();
    }

    @Overwrite
    public void animateTick(int xt, int yt, int zt) {
        int r = 32;
        RandomSource animateRandom = RandomSource.createThreadLocalInstance();
        Block markerParticleTarget = ((ClientLevel)this).getMarkerParticleTarget();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 667; ++i) {
            ((ClientLevel)this).doAnimateTick(xt, yt, zt, 16, animateRandom, markerParticleTarget, pos);
            ((ClientLevel)this).doAnimateTick(xt, yt, zt, 32, animateRandom, markerParticleTarget, pos);
        }
    }

    @Overwrite
    public @Nullable Block getMarkerParticleTarget() {
        ItemStack carriedItemStack;
        Item carriedItem;
        if (((ClientLevel)this).minecraft.gameMode.getPlayerMode() == GameType.CREATIVE && MARKER_PARTICLE_ITEMS.contains(carriedItem = (carriedItemStack = ((ClientLevel)this).minecraft.player.getMainHandItem()).getItem()) && carriedItem instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)carriedItem;
            return blockItem.getBlock();
        }
        return null;
    }

    @Overwrite
    public void doAnimateTick(int xt, int yt, int zt, int r, RandomSource animateRandom, @Nullable Block markerParticleTarget, BlockPos.MutableBlockPos pos) {
        int x = xt + ((ClientLevel)this).random.nextInt(r) - ((ClientLevel)this).random.nextInt(r);
        int y = yt + ((ClientLevel)this).random.nextInt(r) - ((ClientLevel)this).random.nextInt(r);
        int z = zt + ((ClientLevel)this).random.nextInt(r) - ((ClientLevel)this).random.nextInt(r);
        pos.set(x, y, z);
        BlockState state = ((ClientLevel)this).getBlockState((BlockPos)pos);
        state.getBlock().animateTick(state, (Level)((ClientLevel)this), (BlockPos)pos, animateRandom);
        FluidState fluidState = ((ClientLevel)this).getFluidState((BlockPos)pos);
        if (!fluidState.isEmpty()) {
            fluidState.animateTick((Level)((ClientLevel)this), (BlockPos)pos, animateRandom);
            ParticleOptions dripParticle = fluidState.getDripParticle();
            if (dripParticle != null && ((ClientLevel)this).random.nextInt(10) == 0) {
                boolean hasWatertightBottom = state.isFaceSturdy((BlockGetter)((ClientLevel)this), (BlockPos)pos, Direction.DOWN);
                BlockPos below = pos.below();
                ((ClientLevel)this).trySpawnDripParticles(below, ((ClientLevel)this).getBlockState(below), dripParticle, hasWatertightBottom);
            }
        }
        if (markerParticleTarget == state.getBlock()) {
            ((ClientLevel)this).addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK_MARKER, state), (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, 0.0, 0.0, 0.0);
        }
        if (!state.isCollisionShapeFullBlock((BlockGetter)((ClientLevel)this), (BlockPos)pos)) {
            for (AmbientParticle particle : (List)((ClientLevel)this).environmentAttributes().getValue(EnvironmentAttributes.AMBIENT_PARTICLES, (BlockPos)pos)) {
                if (!particle.canSpawn(((ClientLevel)this).random)) continue;
                ((ClientLevel)this).addParticle(particle.particle(), (double)pos.getX() + ((ClientLevel)this).random.nextDouble(), (double)pos.getY() + ((ClientLevel)this).random.nextDouble(), (double)pos.getZ() + ((ClientLevel)this).random.nextDouble(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Overwrite
    public void trySpawnDripParticles(BlockPos pos, BlockState state, ParticleOptions dripParticle, boolean isTopSolid) {
        if (state.getFluidState().isEmpty()) {
            VoxelShape collisionShape = state.getCollisionShape((BlockGetter)((ClientLevel)this), pos);
            double topSideHeight = collisionShape.max(Direction.Axis.Y);
            if (topSideHeight < 1.0) {
                if (isTopSolid) {
                    ((ClientLevel)this).spawnFluidParticle((double)pos.getX(), (double)(pos.getX() + 1), (double)pos.getZ(), (double)(pos.getZ() + 1), (double)(pos.getY() + 1) - 0.05, dripParticle);
                }
            } else if (!state.is(BlockTags.IMPERMEABLE)) {
                double bottomSideHeight = collisionShape.min(Direction.Axis.Y);
                if (bottomSideHeight > 0.0) {
                    ((ClientLevel)this).spawnParticle(pos, dripParticle, collisionShape, (double)pos.getY() + bottomSideHeight - 0.05);
                } else {
                    BlockPos below = pos.below();
                    BlockState belowState = ((ClientLevel)this).getBlockState(below);
                    VoxelShape belowShape = belowState.getCollisionShape((BlockGetter)((ClientLevel)this), below);
                    double belowTopSideHeight = belowShape.max(Direction.Axis.Y);
                    if (belowTopSideHeight < 1.0 && belowState.getFluidState().isEmpty()) {
                        ((ClientLevel)this).spawnParticle(pos, dripParticle, collisionShape, (double)pos.getY() - 0.05);
                    }
                }
            }
        }
    }

    @Overwrite
    public void spawnParticle(BlockPos pos, ParticleOptions dripParticle, VoxelShape dripShape, double height) {
        ((ClientLevel)this).spawnFluidParticle((double)pos.getX() + dripShape.min(Direction.Axis.X), (double)pos.getX() + dripShape.max(Direction.Axis.X), (double)pos.getZ() + dripShape.min(Direction.Axis.Z), (double)pos.getZ() + dripShape.max(Direction.Axis.Z), height, dripParticle);
    }

    @Overwrite
    public void spawnFluidParticle(double x1, double x2, double z1, double z2, double y, ParticleOptions dripParticle) {
        ((ClientLevel)this).addParticle(dripParticle, Mth.lerp((double)((ClientLevel)this).random.nextDouble(), (double)x1, (double)x2), y, Mth.lerp((double)((ClientLevel)this).random.nextDouble(), (double)z1, (double)z2), 0.0, 0.0, 0.0);
    }

    @Overwrite
    public void playSeededSound(@Nullable Entity except, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
        if (except == ((ClientLevel)this).minecraft.player) {
            ((ClientLevel)this).playSound(x, y, z, (SoundEvent)sound.value(), source, volume, pitch, false, seed);
        }
    }

    @Overwrite
    public void playSeededSound(@Nullable Entity except, Entity sourceEntity, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
        if (except == ((ClientLevel)this).minecraft.player) {
            ((ClientLevel)this).minecraft.getSoundManager().play((SoundInstance)new EntityBoundSoundInstance((SoundEvent)sound.value(), source, volume, pitch, sourceEntity, seed));
        }
    }

    @Overwrite
    public void playLocalSound(Entity sourceEntity, SoundEvent sound, SoundSource source, float volume, float pitch) {
        ((ClientLevel)this).minecraft.getSoundManager().play((SoundInstance)new EntityBoundSoundInstance(sound, source, volume, pitch, sourceEntity, ((ClientLevel)this).random.nextLong()));
    }

    @Overwrite
    public void playPlayerSound(SoundEvent sound, SoundSource source, float volume, float pitch) {
        if (((ClientLevel)this).minecraft.player != null) {
            ((ClientLevel)this).minecraft.getSoundManager().play((SoundInstance)new EntityBoundSoundInstance(sound, source, volume, pitch, (Entity)((ClientLevel)this).minecraft.player, ((ClientLevel)this).random.nextLong()));
        }
    }

    @Overwrite
    public void playLocalSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
        ((ClientLevel)this).playSound(x, y, z, sound, source, volume, pitch, distanceDelay, ((ClientLevel)this).random.nextLong());
    }

    @Overwrite
    public void playSound(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed) {
        double distanceToSqr = ClientLevelMixin.chunkcutter$squareDistanceSqr(((ClientLevel)this).minecraft.gameRenderer.mainCamera().position(), x, y, z);
        SimpleSoundInstance instance = new SimpleSoundInstance(sound, source, volume, pitch, RandomSource.create((long)seed), x, y, z);
        if (distanceDelay && distanceToSqr > 100.0) {
            double delayInSeconds = Math.sqrt(distanceToSqr) / 40.0;
            ((ClientLevel)this).minecraft.getSoundManager().playDelayed((SoundInstance)instance, (int)(delayInSeconds * 20.0));
        } else {
            ((ClientLevel)this).minecraft.getSoundManager().play((SoundInstance)instance);
        }
    }

    @Overwrite
    public void createFireworks(double x, double y, double z, double xd, double yd, double zd, List<FireworkExplosion> explosions) {
        if (explosions.isEmpty()) {
            for (int i = 0; i < ((ClientLevel)this).random.nextInt(3) + 2; ++i) {
                ((ClientLevel)this).addParticle((ParticleOptions)ParticleTypes.POOF, x, y, z, ((ClientLevel)this).random.nextGaussian() * 0.05, 0.005, ((ClientLevel)this).random.nextGaussian() * 0.05);
            }
        } else {
            ((ClientLevel)this).minecraft.particleEngine.add((Particle)new FireworkParticles.Starter((ClientLevel)this, x, y, z, xd, yd, zd, ((ClientLevel)this).minecraft.particleEngine, explosions));
        }
    }

    @Overwrite
    public void sendPacketToServer(Packet<?> packet) {
        ((ClientLevel)this).connection.send(packet);
    }

    @Overwrite
    public WorldBorder getWorldBorder() {
        return ((ClientLevel)this).worldBorder;
    }

    @Overwrite
    public RecipeAccess recipeAccess() {
        return ((ClientLevel)this).connection.recipes();
    }

    @Overwrite
    public TickRateManager tickRateManager() {
        return ((ClientLevel)this).tickRateManager;
    }

    @Overwrite
    public ClientClockManager clockManager() {
        return ((ClientLevel)this).connection.clockManager();
    }

    @Overwrite
    public EnvironmentAttributeSystem environmentAttributes() {
        return ((ClientLevel)this).environmentAttributes;
    }

    @Overwrite
    public LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Overwrite
    public LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Overwrite
    public ClientChunkCache getChunkSource() {
        return ((ClientLevel)this).chunkSource;
    }

    @Overwrite
    public @Nullable MapItemSavedData getMapData(MapId id) {
        return (MapItemSavedData)((ClientLevel)this).mapData.get(id);
    }

    @Overwrite
    public void overrideMapData(MapId id, MapItemSavedData data) {
        ((ClientLevel)this).mapData.put(id, data);
    }

    @Overwrite
    public Scoreboard getScoreboard() {
        return ((ClientLevel)this).connection.scoreboard();
    }

    @Overwrite
    public void sendBlockUpdated(BlockPos pos, BlockState old, BlockState current, @Block.UpdateFlags int updateFlags) {
        ((ClientLevel)this).levelExtractor.blockChanged(pos, updateFlags);
    }

    @Overwrite
    public void setBlocksDirty(BlockPos pos, BlockState oldState, BlockState newState) {
        ((ClientLevel)this).levelExtractor.setBlockDirty(pos, oldState, newState);
    }

    @Overwrite
    public void setSectionDirtyWithNeighbors(int chunkX, int chunkY, int chunkZ) {
        ((ClientLevel)this).levelExtractor.setSectionDirtyWithNeighbors(chunkX, chunkY, chunkZ);
    }

    @Overwrite
    public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int maxSectionX, int maxSectionY, int maxSectionZ) {
        ((ClientLevel)this).levelExtractor.setSectionRangeDirty(minSectionX, minSectionY, minSectionZ, maxSectionX, maxSectionY, maxSectionZ);
    }

    @Overwrite
    public void destroyBlockProgress(int id, BlockPos pos, int progress) {
        if (progress >= 0 && progress < 10) {
            BlockDestructionProgress entry = (BlockDestructionProgress)((ClientLevel)this).destroyingBlocks.get(id);
            if (entry != null) {
                ((ClientLevel)this).removeProgress(entry);
            }
            if (entry == null || entry.getPos().getX() != pos.getX() || entry.getPos().getY() != pos.getY() || entry.getPos().getZ() != pos.getZ()) {
                entry = new BlockDestructionProgress(id, pos);
                ((ClientLevel)this).destroyingBlocks.put(id, (Object)entry);
            }
            entry.setProgress(progress);
            entry.updateTick(((ClientLevel)this).getGameTime());
            ((SortedSet)((ClientLevel)this).destructionProgress.computeIfAbsent(entry.getPos().asLong(), k -> Sets.newTreeSet())).add(entry);
        } else {
            BlockDestructionProgress removed = (BlockDestructionProgress)((ClientLevel)this).destroyingBlocks.remove(id);
            if (removed != null) {
                ((ClientLevel)this).removeProgress(removed);
            }
        }
    }

    @Overwrite
    public void globalLevelEvent(int type, BlockPos pos, int data) {
        ((ClientLevel)this).levelEventHandler.globalLevelEvent(type, pos, data);
    }

    @Overwrite
    public void levelEvent(@Nullable Entity source, int type, BlockPos pos, int data) {
        try {
            ((ClientLevel)this).levelEventHandler.levelEvent(type, pos, data);
        }
        catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable((Throwable)t, (String)"Playing level event");
            CrashReportCategory category = report.addCategory("Level event being played");
            category.setDetail("Block coordinates", (Object)CrashReportCategory.formatLocation((LevelHeightAccessor)((ClientLevel)this), (BlockPos)pos));
            category.setDetail("Event source", (Object)source);
            category.setDetail("Event type", (Object)type);
            category.setDetail("Event data", (Object)data);
            throw new ReportedException(report);
        }
    }

    @Overwrite
    public void addParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
        ((ClientLevel)this).doAddParticle(particle, particle.getType().getOverrideLimiter(), false, x, y, z, xd, yd, zd);
    }

    @Overwrite
    public void addParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double xd, double yd, double zd) {
        ((ClientLevel)this).doAddParticle(particle, particle.getType().getOverrideLimiter() || overrideLimiter, alwaysShow, x, y, z, xd, yd, zd);
    }

    @Overwrite
    public void addAlwaysVisibleParticle(ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
        ((ClientLevel)this).doAddParticle(particle, false, true, x, y, z, xd, yd, zd);
    }

    @Overwrite
    public void addAlwaysVisibleParticle(ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double xd, double yd, double zd) {
        ((ClientLevel)this).doAddParticle(particle, particle.getType().getOverrideLimiter() || overrideLimiter, true, x, y, z, xd, yd, zd);
    }

    @Overwrite
    public void doAddParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShowParticles, double x, double y, double z, double xd, double yd, double zd) {
        try {
            Camera camera = ((ClientLevel)this).minecraft.gameRenderer.mainCamera();
            ParticleStatus particleLevel = ((ClientLevel)this).calculateParticleLevel(alwaysShowParticles);
            if (overrideLimiter) {
                ((ClientLevel)this).minecraft.particleEngine.createParticle(particle, x, y, z, xd, yd, zd);
            } else if (!(ClientLevelMixin.chunkcutter$squareDistanceSqr(camera.position(), x, y, z) > 1024.0) && particleLevel != ParticleStatus.MINIMAL) {
                ((ClientLevel)this).minecraft.particleEngine.createParticle(particle, x, y, z, xd, yd, zd);
            }
        }
        catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable((Throwable)t, (String)"Exception while adding particle");
            CrashReportCategory category = report.addCategory("Particle being added");
            category.setDetail("ID", (Object)BuiltInRegistries.PARTICLE_TYPE.getKey((Object)particle.getType()));
            category.setDetail("Parameters", () -> ParticleTypes.CODEC.encodeStart((DynamicOps)((ClientLevel)this).registryAccess().createSerializationContext((DynamicOps)NbtOps.INSTANCE), (Object)particle).toString());
            category.setDetail("Position", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)((ClientLevel)this), (double)x, (double)y, (double)z));
            throw new ReportedException(report);
        }
    }

    @Overwrite
    public ParticleStatus calculateParticleLevel(boolean alwaysShowParticles) {
        ParticleStatus particleLevel = (ParticleStatus)((ClientLevel)this).minecraft.options.particles().get();
        if (alwaysShowParticles && particleLevel == ParticleStatus.MINIMAL && ((ClientLevel)this).random.nextInt(10) == 0) {
            particleLevel = ParticleStatus.DECREASED;
        }
        if (particleLevel == ParticleStatus.DECREASED && ((ClientLevel)this).random.nextInt(3) == 0) {
            particleLevel = ParticleStatus.MINIMAL;
        }
        return particleLevel;
    }

    @Overwrite
    public List<AbstractClientPlayer> players() {
        return ((ClientLevel)this).players;
    }

    @Overwrite
    public List<EnderDragonPart> dragonParts() {
        return ((ClientLevel)this).dragonParts;
    }

    @Overwrite
    public Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ) {
        return ((ClientLevel)this).registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
    }

    @Overwrite
    public int getSkyFlashTime() {
        return (Boolean)((ClientLevel)this).minecraft.options.hideLightningFlash().get() != false ? 0 : ((ClientLevel)this).skyFlashTime;
    }

    @Overwrite
    public void setSkyFlashTime(int skyFlashTime) {
        ((ClientLevel)this).skyFlashTime = skyFlashTime;
    }

    @Overwrite
    public CardinalLighting cardinalLighting() {
        return ((ClientLevel)this).dimensionType().cardinalLightType().get();
    }

    @Overwrite
    public int calculateBlockTint(BlockPos pos, ColorResolver colorResolver) {
        int dist = (Integer)Minecraft.getInstance().options.biomeBlendRadius().get();
        if (dist == 0) {
            return colorResolver.getColor((Biome)((ClientLevel)this).getBiome(pos).value(), (double)pos.getX(), (double)pos.getZ());
        }
        int count = (dist * 2 + 1) * (dist * 2 + 1);
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        Cursor3D cursor = new Cursor3D(pos.getX() - dist, pos.getY(), pos.getZ() - dist, pos.getX() + dist, pos.getY(), pos.getZ() + dist);
        BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();
        while (cursor.advance()) {
            nextPos.set(cursor.nextX(), cursor.nextY(), cursor.nextZ());
            int color = colorResolver.getColor((Biome)((ClientLevel)this).getBiome((BlockPos)nextPos).value(), (double)nextPos.getX(), (double)nextPos.getZ());
            totalRed += ARGB.red((int)color);
            totalGreen += ARGB.green((int)color);
            totalBlue += ARGB.blue((int)color);
        }
        return ARGB.color((int)(totalRed / count), (int)(totalGreen / count), (int)(totalBlue / count));
    }

    @Overwrite
    public void setRespawnData(LevelData.RespawnData respawnData) {
        ((ClientLevel)this).levelData.setSpawn(((ClientLevel)this).getWorldBorderAdjustedRespawnData(respawnData));
    }

    @Overwrite
    public LevelData.RespawnData getRespawnData() {
        return ((ClientLevel)this).levelData.getRespawnData();
    }

    @Overwrite
    public String toString() {
        return "ClientLevel";
    }

    @Overwrite
    public ClientLevel.ClientLevelData getLevelData() {
        return ((ClientLevel)this).clientLevelData;
    }

    @Overwrite
    public void gameEvent(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context) {
    }

    @Overwrite
    public Map<MapId, MapItemSavedData> getAllMapData() {
        return ImmutableMap.copyOf((Map)((ClientLevel)this).mapData);
    }

    @Overwrite
    public void addMapData(Map<MapId, MapItemSavedData> mapData) {
        ((ClientLevel)this).mapData.putAll(mapData);
    }

    @Overwrite
    public LevelEntityGetter<Entity> getEntities() {
        return ((ClientLevel)this).entityStorage.getEntityGetter();
    }

    @Overwrite
    public String gatherChunkSourceStats() {
        return "Chunks[C] W: " + ((ClientLevel)this).chunkSource.gatherStats() + " E: " + ((ClientLevel)this).entityStorage.gatherStats();
    }

    @Overwrite
    public void addDestroyBlockEffect(BlockPos pos, BlockState blockState) {
        if (!blockState.isAir() && blockState.shouldSpawnTerrainParticles()) {
            VoxelShape shape = blockState.getShape((BlockGetter)((ClientLevel)this), pos);
            double density = 0.25;
            shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                double widthX = Math.min(1.0, x2 - x1);
                double widthY = Math.min(1.0, y2 - y1);
                double widthZ = Math.min(1.0, z2 - z1);
                int countX = Math.max(2, Mth.ceil((double)(widthX / 0.25)));
                int countY = Math.max(2, Mth.ceil((double)(widthY / 0.25)));
                int countZ = Math.max(2, Mth.ceil((double)(widthZ / 0.25)));
                for (int xx = 0; xx < countX; ++xx) {
                    for (int yy = 0; yy < countY; ++yy) {
                        for (int zz = 0; zz < countZ; ++zz) {
                            double relX = ((double)xx + 0.5) / (double)countX;
                            double relY = ((double)yy + 0.5) / (double)countY;
                            double relZ = ((double)zz + 0.5) / (double)countZ;
                            double x = relX * widthX + x1;
                            double y = relY * widthY + y1;
                            double z = relZ * widthZ + z1;
                            ((ClientLevel)this).minecraft.particleEngine.add((Particle)new TerrainParticle((ClientLevel)this, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, relX - 0.5, relY - 0.5, relZ - 0.5, blockState, pos));
                        }
                    }
                }
            });
        }
    }

    @Overwrite
    public void addBreakingBlockEffect(BlockPos pos, Direction direction) {
        BlockState blockState = ((ClientLevel)this).getBlockState(pos);
        if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.shouldSpawnTerrainParticles()) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            float r = 0.1f;
            AABB shape = blockState.getShape((BlockGetter)((ClientLevel)this), pos).bounds();
            double xp = (double)x + ((ClientLevel)this).random.nextDouble() * (shape.maxX - shape.minX - (double)0.2f) + (double)0.1f + shape.minX;
            double yp = (double)y + ((ClientLevel)this).random.nextDouble() * (shape.maxY - shape.minY - (double)0.2f) + (double)0.1f + shape.minY;
            double zp = (double)z + ((ClientLevel)this).random.nextDouble() * (shape.maxZ - shape.minZ - (double)0.2f) + (double)0.1f + shape.minZ;
            if (direction == Direction.DOWN) {
                yp = (double)y + shape.minY - (double)0.1f;
            }
            if (direction == Direction.UP) {
                yp = (double)y + shape.maxY + (double)0.1f;
            }
            if (direction == Direction.NORTH) {
                zp = (double)z + shape.minZ - (double)0.1f;
            }
            if (direction == Direction.SOUTH) {
                zp = (double)z + shape.maxZ + (double)0.1f;
            }
            if (direction == Direction.WEST) {
                xp = (double)x + shape.minX - (double)0.1f;
            }
            if (direction == Direction.EAST) {
                xp = (double)x + shape.maxX + (double)0.1f;
            }
            ((ClientLevel)this).minecraft.particleEngine.add(new TerrainParticle((ClientLevel)this, xp, yp, zp, 0.0, 0.0, 0.0, blockState, pos).setPower(0.2f).scale(0.6f));
        }
    }

    @Overwrite
    public void setServerSimulationDistance(int serverSimulationDistance) {
        ((ClientLevel)this).serverSimulationDistance = serverSimulationDistance;
    }

    @Overwrite
    public int getServerSimulationDistance() {
        return ((ClientLevel)this).serverSimulationDistance;
    }

    @Overwrite
    public FeatureFlagSet enabledFeatures() {
        return ((ClientLevel)this).connection.enabledFeatures();
    }

    @Overwrite
    public PotionBrewing potionBrewing() {
        return ((ClientLevel)this).connection.potionBrewing();
    }

    @Overwrite
    public FuelValues fuelValues() {
        return ((ClientLevel)this).connection.fuelValues();
    }

    @Overwrite
    public void explode(@Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float r, boolean fire, Level.ExplosionInteraction interactionType, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, WeightedList<ExplosionParticleInfo> secondaryParticles, Holder<SoundEvent> explosionSound) {
    }

    @Overwrite
    public int getSeaLevel() {
        return ((ClientLevel)this).seaLevel;
    }

    @Overwrite
    public int getClientLeafTintColor(BlockPos pos) {
        BlockState state = ((ClientLevel)this).getBlockState(pos);
        BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
        return tintSource != null ? tintSource.colorInWorld(state, (BlockAndTintGetter)((ClientLevel)this), pos) : -1;
    }

    @Overwrite
    public void registerForCleaning(CacheSlot<ClientLevel, ?> slot) {
        ((ClientLevel)this).connection.registerForCleaning(slot);
    }

    @Overwrite
    public void trackExplosionEffects(Vec3 center, float radius, int blockCount, WeightedList<ExplosionParticleInfo> blockParticles) {
        ((ClientLevel)this).explosionTracker.track(center, radius, blockCount, blockParticles);
    }
}


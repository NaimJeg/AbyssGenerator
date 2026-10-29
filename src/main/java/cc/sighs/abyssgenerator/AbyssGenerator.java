package cc.sighs.abyssgenerator;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ServerLevel;
import cc.sighs.abyssgenerator.world.bridge.VanillaColumnBridge;
import cc.sighs.abyssgenerator.world.section.SectionManager;
import org.slf4j.Logger;

@Mod( cc.sighs.abyssgenerator.AbyssGenerator.MODID)
public class AbyssGenerator {

    public static final String MODID = "abyssgenerator";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Dev-only switch for the Wide-Y black-box regression harness.
     *
     * Only the dev run configurations set this property; production launches
     * never install the /wideytest command suite.
     */
    private static final String WIDE_Y_HARNESS_PROPERTY = "abyssgenerator.wideytest";

    public AbyssGenerator(IEventBus modEventBus, ModContainer modContainer) {
        cc.sighs.abyssgenerator.world.generation.ModWorldGeneration.register(modEventBus);
        cc.sighs.abyssgenerator.world.section.PreparationTickets.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);

        if (Boolean.getBoolean(WIDE_Y_HARNESS_PROPERTY)) {
            installWideYHarness();
        }
    }

    /**
     * Minimal development-only bootstrap for the Wide-Y regression harness.
     *
     * The harness class lives in the test source set, so it is resolved via
     * reflection to avoid a main -> test compile-time dependency. It registers
     * /wideytest on NeoForge.EVENT_BUS when the class is on the classpath.
     */
    private static void installWideYHarness() {
        try {
            Class<?> harness = Class.forName("cc.sighs.abyssgenerator.WideYBaselineTest");
            harness.getMethod("install").invoke(null);
            LOGGER.info("WideYBaselineTest harness installed (dev-only)");
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.warn("WideYBaselineTest harness not available on runtime classpath: {}", e.toString());
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && VanillaColumnBridge.enabled(level)
                && event.getChunk() instanceof LevelChunk chunk) SectionManager.of(level).adoptColumn(chunk);
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && VanillaColumnBridge.enabled(level)) {
            var pos = event.getChunk().getPos();
            SectionManager.of(level).unloadColumn(pos.x(), pos.z());
        }
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && VanillaColumnBridge.enabled(level)) SectionManager.of(level).close();
    }

    @SubscribeEvent
    public void onLevelTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level && VanillaColumnBridge.enabled(level)) SectionManager.of(level).tick();
    }

    @SubscribeEvent
    public void onTravel(EntityTravelToDimensionEvent event) {
        // P1 is an administrative test fixture. Do not let entities enter unknown
        // space before the readiness boundary and player tickets have been implemented.
        if (event.getEntity().level() instanceof ServerLevel source) {
            var destination = source.getServer().getLevel(event.getDimension());
            if (destination != null && VanillaColumnBridge.enabled(destination)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCreateSpawn(LevelEvent.CreateSpawnPosition event) {
        if (event.getLevel() instanceof ServerLevel level && level.getChunkSource().getGenerator()
                instanceof cc.sighs.abyssgenerator.world.generation.FractalChunkGenerator) {
            var spawn = cc.sighs.abyssgenerator.world.generation.FractalChunkGenerator.SPAWN;
            level.getChunk(spawn);
            level.setRespawnData(net.minecraft.world.level.storage.LevelData.RespawnData.of(level.dimension(), spawn, 0, 0));
            event.setCanceled(true);
        }
    }
}

package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.coord.*;
import cc.sighs.abyssgenerator.world.bridge.VanillaColumnBridge;
import cc.sighs.abyssgenerator.world.section.SectionManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import java.util.*;

/** Development-only assertions. Arithmetic extremes are not world-height promises. */
public final class WideYBaselineTest {
    public static final int TEST_X = 8, TEST_Z = 8;
    public static final int[] WORLD_YS = {-2032,-2031,-1024,-257,-256,-255,-17,-16,-1,0,15,16,255,256,257,1024,2031};
    private static boolean installed;
    private static final Map<UUID, PendingTeleport> TELEPORTS = new HashMap<>();
    private record PendingTeleport(ServerPlayer player, ServerLevel level, int y, int deadline) {}
    private WideYBaselineTest() {}

    public static synchronized void install() {
        if (installed) return;
        installed = true;
        NeoForge.EVENT_BUS.addListener(WideYBaselineTest::registerCommands);
        NeoForge.EVENT_BUS.addListener(WideYBaselineTest::tick);
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> TELEPORTS.clear());
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        var root = Commands.literal("wideytest").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
        root.then(Commands.literal("info").executes(c -> info(c.getSource())));
        root.then(Commands.literal("stats").executes(c -> info(c.getSource())));
        root.then(Commands.literal("selfcheck").executes(c -> selfcheck(c.getSource())));
        root.then(Commands.literal("save").executes(c -> save(c.getSource())));
        for (String action : List.of("probe", "ensure", "prepare", "cancelPrepare", "write", "verify", "clear", "verifyAir", "tp")) {
            root.then(Commands.literal(action).then(Commands.argument("y", IntegerArgumentType.integer())
                    .executes(c -> operate(c.getSource(), action, IntegerArgumentType.getInteger(c, "y")))));
        }
        event.getDispatcher().register(root);
    }

    public static String stats(ServerLevel level) {
        var chunk = level.getChunkSource().getChunkNow(0, 0);
        int distinct = 0, nonAir = 0;
        if (chunk != null) {
            var identities = Collections.newSetFromMap(new IdentityHashMap<LevelChunkSection, Boolean>());
            for (var section : chunk.getSections()) {
                if (section != null) { identities.add(section); if (!section.hasOnlyAir()) nonAir++; }
            }
            distinct = identities.size();
        }
        return "dimension=" + level.dimension().identifier() + " minY=" + level.getMinY() + " height=" + level.getHeight()
                + " maxYInclusive=" + level.getMaxY() + " loadedColumns=" + level.getChunkSource().getLoadedChunksCount()
                + " vanillaPendingTasks=" + level.getChunkSource().getPendingTasksCount()
                + " markerColumnContainers=" + distinct + " markerColumnNonAir=" + nonAir
                + " markerColumnUnsaved=" + (chunk != null && chunk.isUnsaved())
                + " managedSections=" + (VanillaColumnBridge.enabled(level) ? SectionManager.of(level).loadedCount() : "disabled")
                + " administrativePreparations=" + (VanillaColumnBridge.enabled(level) ? SectionManager.of(level).pendingCount() : "disabled")
                + " preparationColumns=" + (VanillaColumnBridge.enabled(level) ? SectionManager.of(level).preparationColumnCount() : "disabled")
                + " sectionQueue=not-installed pageSave=not-installed";
    }

    private static int info(CommandSourceStack source) { return pass(source, stats(source.getLevel())); }
    private static int selfcheck(CommandSourceStack source) {
        for (int y : WORLD_YS) {
            var key = SectionKey.fromBlock(TEST_X, y, TEST_Z);
            if (!key.equals(key.page().section(key.indexInPage()))
                    || (long) PageKey.blockToPage(y) * 256 + PageKey.localBlockY(y) != y)
                return fail(source, "coordinate roundtrip Y=" + y);
        }
        return pass(source, "coordinate selfcheck (arithmetic only)");
    }

    private static int operate(CommandSourceStack source, String action, int y) throws CommandSyntaxException {
        var level = source.getLevel();
        if (level.isOutsideBuildHeight(y)) return fail(source, "Y=" + y + " outside [" + level.getMinY() + "," + (level.getMaxY() + 1) + ")");
        var pos = new BlockPos(TEST_X, y, TEST_Z);
        if (action.equals("prepare") || action.equals("cancelPrepare")) {
            if (!VanillaColumnBridge.enabled(level)) return fail(source, "Sparse bridge disabled");
            var manager = SectionManager.of(level);
            var key = SectionKey.fromBlock(TEST_X, y, TEST_Z);
            if (action.equals("cancelPrepare")) return manager.cancelPreparation(key)
                    ? pass(source, "administrative preparation cancelled " + key) : fail(source, "no preparation for " + key);
            manager.prepareSection(key, java.time.Duration.ofSeconds(10)).whenComplete((record, error) -> {
                if (error != null) fail(source, "administrative preparation failed: " + error);
                else pass(source, "administrative section ready " + record.key() + "; vanilla column dependency remains");
            });
            source.sendSuccess(() -> Component.literal("[WideY] preparation requested " + key + "; completion pending"), false);
            return 1;
        }
        if (action.equals("ensure")) {
            if (!VanillaColumnBridge.enabled(level)) return fail(source, "Sparse bridge disabled; use the isolated bridgeServer run");
            var record = SectionManager.of(level).ensureSection(SectionKey.fromBlock(TEST_X, y, TEST_Z));
            return pass(source, "explicit section ready " + record.key());
        }
        if (action.equals("probe")) {
            var chunk = level.getChunkSource().getChunkNow(0, 0);
            if (chunk != null && chunk.getSection(level.getSectionIndex(y)) instanceof cc.sighs.abyssgenerator.world.bridge.UnknownSection)
                return pass(source, "probe UNKNOWN (no generation requested)");
            return pass(source, chunk == null ? "probe UNLOADED (no load requested)" : "probe Y=" + y + " block=" + chunk.getBlockState(pos));
        }
        if (action.equals("tp")) {
            if (VanillaColumnBridge.enabled(level)) return fail(source, "P1 spike is closed to gameplay until readiness protection is implemented");
            ServerPlayer player = source.getPlayerOrException();
            if (player.level() != level) return fail(source, "enter the target dimension before tp");
            level.getChunk(0, 0); // Controlled baseline command only, not a normal gameplay read.
            player.teleportTo(TEST_X + 0.5, y, TEST_Z + 0.5);
            TELEPORTS.put(player.getUUID(), new PendingTeleport(player, level, y, source.getServer().getTickCount() + 5));
            return pass(source, "teleport requested; server readback pending (client checked by AutoJS)");
        }
        level.getChunk(0, 0); // Explicit administrative load; probe never uses this path.
        if (VanillaColumnBridge.enabled(level)) {
            var section = level.getChunk(0, 0).getSection(level.getSectionIndex(y));
            if (section instanceof cc.sighs.abyssgenerator.world.bridge.UnknownSection)
                return fail(source, "section unknown; run ensure first (not equivalent to air)");
        }
        if (action.equals("write")) level.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
        if (action.equals("clear")) level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        boolean expectedAir = action.equals("clear") || action.equals("verifyAir");
        var actual = level.getBlockState(pos);
        boolean ok = expectedAir ? actual.isAir() : actual.is(Blocks.DIAMOND_BLOCK);
        return ok ? pass(source, action + " Y=" + y + " readback=" + actual)
                : fail(source, action + " Y=" + y + " actual=" + actual);
    }

    private static void tick(ServerTickEvent.Post event) {
        TELEPORTS.values().removeIf(p -> {
            if (event.getServer().getTickCount() < p.deadline) return false;
            boolean ok = !p.player.hasDisconnected() && p.player.level() == p.level
                    && Math.abs(p.player.getY() - p.y) < 0.1
                    && p.level.getChunkSource().getChunkNow(0, 0) != null;
            p.player.sendSystemMessage(Component.literal("[WideY] " + (ok ? "PASS" : "FAIL")
                    + " teleport server readback requested=" + p.y + " actual=" + p.player.getY()));
            return true;
        });
    }

    private static int save(CommandSourceStack source) {
        return source.getServer().saveEverything(false, true, true)
                ? pass(source, "vanilla save flushed (page storage not installed)") : fail(source, "save returned false");
    }
    private static int pass(CommandSourceStack source, String detail) {
        source.sendSuccess(() -> Component.literal("[WideY] PASS " + detail), false);
        return 1;
    }
    private static int fail(CommandSourceStack source, String detail) {
        source.sendFailure(Component.literal("[WideY] FAIL " + detail));
        return 0;
    }
}

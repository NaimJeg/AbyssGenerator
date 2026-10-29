/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunkSection
 */
package com.jfyx2025.chunkcutter;

import com.jfyx2025.chunkcutter.ChunkCutterConfig;
import com.jfyx2025.chunkcutter.ChunkCutterMod;
import com.jfyx2025.chunkcutter.cubic.CloPos;
import com.jfyx2025.chunkcutter.cubic.CubeChunk;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubePos;
import com.jfyx2025.chunkcutter.cubic.CubeProvider;
import com.jfyx2025.chunkcutter.cubic.CubeSectionStore;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainAlgorithm3D;
import com.jfyx2025.chunkcutter.cubic.terrain.TerrainSources;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class ChunkCutterCommand {
    private ChunkCutterCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder chunkcutter = Commands.literal((String)"chunkcutter");
            chunkcutter.then(Commands.literal((String)"cube").executes(ctx -> {
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                CubePos pos = CubePos.fromBlock(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("[chunkcutter] your cube = " + String.valueOf(pos))), false);
                return 1;
            }));
            chunkcutter.then(Commands.literal((String)"set").then(Commands.argument((String)"pos", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> {
                BlockPos pos = BlockPosArgument.getBlockPos((CommandContext)ctx, (String)"pos");
                ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
                level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
                boolean ok = level.getBlockState(pos).is((Object)Blocks.STONE);
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("[chunkcutter] set " + String.valueOf(pos) + " -> " + (ok ? "OK" : "FAILED") + " (engine range y " + level.getMinY() + ".." + level.getMaxY() + ")")), false);
                return ok ? 1 : 0;
            })));
            chunkcutter.then(Commands.literal((String)"place3d").executes(ctx -> {
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                ServerLevel level = player.level();
                int y = 400;
                int baseX = player.getBlockX();
                int baseZ = player.getBlockZ();
                int count = 0;
                for (int dx = 0; dx < 16; ++dx) {
                    for (int dz = 0; dz < 16; ++dz) {
                        level.setBlock(new BlockPos(baseX + dx, y, baseZ + dz), Blocks.STONE.defaultBlockState(), 3);
                        ++count;
                    }
                }
                int placed = count;
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("[chunkcutter] placed 3D platform at y=400, blocks=" + placed)), false);
                return 1;
            }));
            chunkcutter.then(Commands.literal((String)"read3d").executes(ctx -> {
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                ServerLevel level = player.level();
                BlockPos pos = new BlockPos(player.getBlockX(), 400, player.getBlockZ());
                BlockState state = level.getBlockState(pos);
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("[chunkcutter] read3d " + String.valueOf(pos) + " = " + state.getBlock().getName().getString() + " (vanillaOutside=" + level.isOutsideBuildHeight(pos) + ")")), false);
                return 1;
            }));
            chunkcutter.then(((LiteralArgumentBuilder)Commands.literal((String)"terrain").executes(ctx -> {
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                return ChunkCutterCommand.chunkcutter$terrainStatus((CommandSourceStack)ctx.getSource(), player.getBlockX(), player.getBlockY(), player.getBlockZ());
            })).then(Commands.argument((String)"pos", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> {
                BlockPos pos = BlockPosArgument.getBlockPos((CommandContext)ctx, (String)"pos");
                return ChunkCutterCommand.chunkcutter$terrainStatus((CommandSourceStack)ctx.getSource(), pos.getX(), pos.getY(), pos.getZ());
            })));
            chunkcutter.then(Commands.literal((String)"selfcheck").executes(ctx -> {
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                return ChunkCutterCommand.chunkcutter$selfCheck((CommandSourceStack)ctx.getSource(), player);
            }));
            chunkcutter.then(((LiteralArgumentBuilder)Commands.literal((String)"config").executes(ctx -> {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("[chunkcutter] " + ChunkCutterConfig.get().describe())), false);
                return 1;
            })).then(Commands.literal((String)"reload").executes(ctx -> {
                ChunkCutterConfig.load();
                ChunkCutterMod.applyConfig();
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("[chunkcutter] reloaded: " + ChunkCutterConfig.get().describe() + " (height range changes need a restart)")), false);
                return 1;
            })));
            dispatcher.register(chunkcutter);
        });
    }

    private static int chunkcutter$terrainStatus(CommandSourceStack source, int x, int y, int z) {
        ServerLevel level = source.getLevel();
        int height = TerrainSources.forLevel(level).heightAt(x, z);
        BlockPos topPos = new BlockPos(x, height - 1, z);
        BlockPos airPos = new BlockPos(x, height, z);
        CloPos clo = CloPos.cube(x >> 4, y >> 4, z >> 4);
        CubeMap map = ((CubeSource)level.getChunkSource()).chunkcutter$cubeMap();
        boolean loaded = map.contains(clo.asLong());
        boolean pending = CubeProvider.isPending(level, clo);
        boolean stored = CubeSectionStore.hasStored((Level)level, clo);
        source.sendSuccess(() -> Component.literal((String)("[chunkcutter] terrain (" + x + "," + z + ") height=" + height + " top=" + level.getBlockState(topPos).getBlock().getName().getString() + " above=" + level.getBlockState(airPos).getBlock().getName().getString() + " section=" + clo.x() + "," + clo.y() + "," + clo.z() + " loaded=" + loaded + " pending=" + pending + " stored=" + stored + " preloaded=" + CubeProvider.preloadedLoadedCount(level) + "/" + CubeProvider.preloadedCount(level))), false);
        return 1;
    }

    private static int chunkcutter$selfCheck(CommandSourceStack source, ServerPlayer player) {
        ServerLevel level = source.getLevel();
        TerrainAlgorithm3D terrain = TerrainSources.forLevel(level);
        CubeMap map = ((CubeSource)level.getChunkSource()).chunkcutter$cubeMap();
        int centerX = player.getBlockX() >> 4;
        int centerY = player.getBlockY() >> 4;
        int centerZ = player.getBlockZ() >> 4;
        int sections = 0;
        int blocks = 0;
        int mismatches = 0;
        int columnMismatches = 0;
        Object first = "";
        Object firstColumn = "";
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    CloPos clo = CloPos.cube(centerX + dx, centerY + dy, centerZ + dz);
                    CubeChunk cube = map.get(clo);
                    if (cube == null || cube.section() == null) continue;
                    ++sections;
                    int baseX = clo.x() << 4;
                    int baseY = clo.y() << 4;
                    int baseZ = clo.z() << 4;
                    LevelChunkSection section = cube.section();
                    for (int lx = 0; lx < 16; ++lx) {
                        for (int lz = 0; lz < 16; ++lz) {
                            int height = terrain.heightAt(baseX + lx, baseZ + lz);
                            for (int ly = 0; ly < 16; ++ly) {
                                BlockPos pos;
                                BlockState columnActual;
                                ++blocks;
                                BlockState expected = terrain.stateAt(baseX + lx, baseY + ly, baseZ + lz);
                                BlockState actual = section.getBlockState(lx, ly, lz);
                                if (!actual.equals((Object)expected)) {
                                    ++mismatches;
                                    if (((String)first).isEmpty()) {
                                        first = " first=(" + (baseX + lx) + "," + (baseY + ly) + "," + (baseZ + lz) + ") expected=" + expected.getBlock().getName().getString() + " actual=" + actual.getBlock().getName().getString();
                                    }
                                }
                                if ((columnActual = level.getBlockState(pos = new BlockPos(baseX + lx, baseY + ly, baseZ + lz))).equals((Object)expected)) continue;
                                ++columnMismatches;
                                if (!((String)firstColumn).isEmpty()) continue;
                                firstColumn = " firstColumn=(" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ") expected=" + expected.getBlock().getName().getString() + " actual=" + columnActual.getBlock().getName().getString();
                            }
                        }
                    }
                }
            }
        }
        int totalSections = sections;
        int totalBlocks = blocks;
        int totalMismatches = mismatches;
        int totalColumnMismatches = columnMismatches;
        String mismatchText = (String)first + (String)firstColumn;
        source.sendSuccess(() -> Component.literal((String)("[chunkcutter] selfcheck sections=" + totalSections + " blocks=" + totalBlocks + " mismatches=" + totalMismatches + " columnMismatches=" + totalColumnMismatches + mismatchText)), false);
        return totalMismatches == 0 && totalColumnMismatches == 0 ? 1 : 0;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
 *  net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.ServerPlayerGameMode
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Abilities
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.jfyx2025.chunkcutter.mixin;

import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ServerPlayerGameMode.class})
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    private static double FLIGHT_DISABLE_RANGE;
    @Shadow
    protected ServerLevel level;
    @Shadow
    protected ServerPlayer player;
    @Shadow
    private GameType gameModeForPlayer;
    @Shadow
    private @Nullable GameType previousGameModeForPlayer;
    @Shadow
    private boolean isDestroyingBlock;
    @Shadow
    private int destroyProgressStart;
    @Shadow
    private BlockPos destroyPos;
    @Shadow
    private int gameTicks;
    @Shadow
    private boolean hasDelayedDestroy;
    @Shadow
    private BlockPos delayedDestroyPos;
    @Shadow
    private int delayedTickStart;
    @Shadow
    private int lastSentState;

    @Shadow
    public abstract boolean destroyBlock(BlockPos var1);

    @Overwrite
    public boolean changeGameModeForPlayer(GameType gameModeForPlayer) {
        if (gameModeForPlayer == this.gameModeForPlayer) {
            return false;
        }
        Abilities abilities = this.player.getAbilities();
        this.setGameModeForPlayer(gameModeForPlayer, this.gameModeForPlayer);
        if (abilities.flying && gameModeForPlayer != GameType.SPECTATOR && this.isInRangeOfGround()) {
            abilities.flying = false;
        }
        this.player.onUpdateAbilities();
        this.level.getServer().getPlayerList().broadcastAll((Packet)new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, this.player));
        this.level.updateSleepingPlayerList();
        if (gameModeForPlayer == GameType.CREATIVE) {
            this.player.resetCurrentImpulseContext();
        }
        return true;
    }

    @Overwrite
    public void setGameModeForPlayer(GameType gameModeForPlayer, @Nullable GameType previousGameModeForPlayer) {
        this.previousGameModeForPlayer = previousGameModeForPlayer;
        this.gameModeForPlayer = gameModeForPlayer;
        Abilities abilities = this.player.getAbilities();
        gameModeForPlayer.updatePlayerAbilities(abilities);
    }

    @Overwrite
    public boolean isInRangeOfGround() {
        List clipping = Entity.collectAllColliders((Entity)this.player, (Level)this.level, (AABB)this.player.getBoundingBox());
        return clipping.isEmpty() && this.player.getAvailableSpaceBelow(1.0) < 1.0;
    }

    @Overwrite
    public GameType getGameModeForPlayer() {
        return this.gameModeForPlayer;
    }

    @Overwrite
    public @Nullable GameType getPreviousGameModeForPlayer() {
        return this.previousGameModeForPlayer;
    }

    @Overwrite
    public boolean isSurvival() {
        return this.gameModeForPlayer.isSurvival();
    }

    @Overwrite
    public boolean isCreative() {
        return this.gameModeForPlayer.isCreative();
    }

    @Overwrite
    public float incrementDestroyProgress(BlockState blockState, BlockPos delayedDestroyPos, int destroyStartTick) {
        int ticksSpentDestroying = this.gameTicks - destroyStartTick;
        float destroyProgress = blockState.getDestroyProgress((Player)this.player, (BlockGetter)this.player.level(), delayedDestroyPos) * (float)(ticksSpentDestroying + 1);
        int state = (int)(destroyProgress * 10.0f);
        if (state != this.lastSentState) {
            this.level.destroyBlockProgress(this.player.getId(), delayedDestroyPos, state);
            this.lastSentState = state;
        }
        return destroyProgress;
    }

    @Overwrite
    public void debugLogging(BlockPos pos, boolean allGood, int sequence, String message) {
        if (SharedConstants.DEBUG_BLOCK_BREAK) {
            // empty if block
        }
    }

    @Overwrite
    public void destroyAndAck(BlockPos pos, int sequence, String exitId) {
        if (this.destroyBlock(pos)) {
            this.debugLogging(pos, true, sequence, exitId);
        } else {
            this.player.connection.send((Packet)new ClientboundBlockUpdatePacket(pos, this.level.getBlockState(pos)));
            this.debugLogging(pos, false, sequence, exitId);
        }
    }

    @Overwrite
    public void setLevel(ServerLevel newLevel) {
        this.level = newLevel;
    }
}


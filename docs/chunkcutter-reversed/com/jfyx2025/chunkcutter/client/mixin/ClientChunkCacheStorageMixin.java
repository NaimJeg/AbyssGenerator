/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.jfyx2025.chunkcutter.client.mixin;

import com.jfyx2025.chunkcutter.client.ClientChunkSink;
import com.jfyx2025.chunkcutter.client.ClientSectionLevelChunk;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"net.minecraft.client.multiplayer.ClientChunkCache$Storage"})
public abstract class ClientChunkCacheStorageMixin
implements ClientChunkSink {
    @Shadow
    private AtomicReferenceArray<LevelChunk> chunks;

    @Shadow
    abstract int getIndex(int var1, int var2);

    @Shadow
    abstract boolean inRange(int var1, int var2);

    @Shadow
    abstract void replace(int var1, LevelChunk var2);

    @Override
    public LevelChunk chunkcutter$ensureChunk(Level level, int chunkX, int chunkZ) {
        LevelChunk levelChunk;
        if (!this.inRange(chunkX, chunkZ)) {
            return null;
        }
        int index = this.getIndex(chunkX, chunkZ);
        LevelChunk existing = this.chunks.get(index);
        if (existing != null && existing.getPos().x() == chunkX && existing.getPos().z() == chunkZ) {
            return existing;
        }
        if (level instanceof ClientLevel) {
            ClientLevel clientLevel = (ClientLevel)level;
            levelChunk = new ClientSectionLevelChunk(clientLevel, new ChunkPos(chunkX, chunkZ));
        } else {
            levelChunk = new LevelChunk(level, new ChunkPos(chunkX, chunkZ));
        }
        LevelChunk created = levelChunk;
        this.replace(index, created);
        return created;
    }

    @Override
    public void chunkcutter$insertChunk(int chunkX, int chunkZ, LevelChunk chunk) {
        if (!this.inRange(chunkX, chunkZ)) {
            return;
        }
        this.replace(this.getIndex(chunkX, chunkZ), chunk);
    }

    @Redirect(method={"onChunkAdded"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")})
    private boolean chunkcutter$airOnAdded(LevelChunkSection section) {
        return section == null || section.hasOnlyAir();
    }

    @Redirect(method={"refreshEmptySections"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")})
    private boolean chunkcutter$airRefresh(LevelChunkSection section) {
        return section == null || section.hasOnlyAir();
    }
}


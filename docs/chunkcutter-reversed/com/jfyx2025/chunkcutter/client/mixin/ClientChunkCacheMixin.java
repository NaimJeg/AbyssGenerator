/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.multiplayer.ClientChunkCache$Storage
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.SectionPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.jspecify.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.jfyx2025.chunkcutter.client.mixin;

import com.jfyx2025.chunkcutter.client.ClientChunkSink;
import com.jfyx2025.chunkcutter.cubic.CubeMap;
import com.jfyx2025.chunkcutter.cubic.CubeSource;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientChunkCache.class})
public abstract class ClientChunkCacheMixin
implements CubeSource,
ClientChunkSink {
    private final CubeMap chunkcutter$cubes = new CubeMap();
    @Shadow
    private LevelChunk emptyChunk;
    @Shadow
    private LevelLightEngine lightEngine;
    @Shadow
    private volatile ClientChunkCache.Storage storage;
    @Shadow
    private ClientLevel level;

    @Override
    public void chunkcutter$insertChunk(int chunkX, int chunkZ, LevelChunk chunk) {
        ClientChunkCache.Storage storageObject = this.storage;
        if (storageObject instanceof ClientChunkSink) {
            ClientChunkSink sink = (ClientChunkSink)storageObject;
            sink.chunkcutter$insertChunk(chunkX, chunkZ, chunk);
            if (this.lightEngine != null) {
                this.lightEngine.setLightEnabled(new ChunkPos(chunkX, chunkZ), true);
            }
        }
    }

    @Override
    public LevelChunk chunkcutter$ensureChunk(Level level, int chunkX, int chunkZ) {
        ClientChunkCache.Storage storageObject = this.storage;
        if (storageObject instanceof ClientChunkSink) {
            ClientChunkSink sink = (ClientChunkSink)storageObject;
            LevelChunk chunk = sink.chunkcutter$ensureChunk(level, chunkX, chunkZ);
            if (chunk != null && this.lightEngine != null) {
                this.lightEngine.setLightEnabled(new ChunkPos(chunkX, chunkZ), true);
            }
            return chunk;
        }
        return null;
    }

    @Override
    public CubeMap chunkcutter$cubeMap() {
        return this.chunkcutter$cubes;
    }

    @Inject(method={"drop"}, at={@At(value="HEAD")})
    private void chunkcutter$dropCubes(ChunkPos pos, CallbackInfo ci) {
        this.chunkcutter$cubes.removeColumn(pos.x(), pos.z());
    }

    @Overwrite
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Overwrite
    public static boolean isValidChunk(@Nullable LevelChunk chunk, int x, int z) {
        if (chunk == null) {
            return false;
        }
        ChunkPos pos = chunk.getPos();
        return pos.x() == x && pos.z() == z;
    }

    @Overwrite
    public @Nullable LevelChunk getChunk(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
        LevelChunk chunk;
        if (this.storage.inRange(x, z) && ClientChunkCacheMixin.isValidChunk(chunk = this.storage.getChunk(this.storage.getIndex(x, z)), x, z)) {
            return chunk;
        }
        return loadOrGenerate ? this.emptyChunk : null;
    }

    @Overwrite
    public BlockGetter getLevel() {
        return this.level;
    }

    @Overwrite
    public void replaceBiomes(int chunkX, int chunkZ, FriendlyByteBuf readBuffer) {
        int index;
        LevelChunk chunk;
        if (this.storage.inRange(chunkX, chunkZ) && ClientChunkCacheMixin.isValidChunk(chunk = (LevelChunk)this.storage.chunks.get(index = this.storage.getIndex(chunkX, chunkZ)), chunkX, chunkZ)) {
            chunk.replaceBiomes(readBuffer);
        }
    }

    @Overwrite
    public void tick(BooleanSupplier haveTime, boolean tickChunks) {
    }

    @Overwrite
    public void updateViewCenter(int x, int z) {
        this.storage.viewCenterX = x;
        this.storage.viewCenterZ = z;
    }

    @Overwrite
    public static int calculateStorageRange(int viewRange) {
        return Math.max(2, viewRange) + 3;
    }

    @Overwrite
    public String gatherStats() {
        return this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
    }

    @Overwrite
    public int getLoadedChunksCount() {
        return this.storage.chunkCount;
    }

    @Overwrite
    public void onLightUpdate(LightLayer layer, SectionPos pos) {
        Minecraft.getInstance().levelExtractor.setSectionDirty(pos.x(), pos.y(), pos.z());
    }

    @Overwrite
    public LongOpenHashSet addedEmptySections() {
        return this.storage.addedEmptySections[this.storage.updatingSetsIndex];
    }

    @Overwrite
    public LongOpenHashSet removedEmptySections() {
        return this.storage.removedEmptySections[this.storage.updatingSetsIndex];
    }

    @Overwrite
    public LongOpenHashSet addedLoadedChunks() {
        return this.storage.addedLoadedChunks[this.storage.updatingSetsIndex];
    }

    @Overwrite
    public LongOpenHashSet removedLoadedChunks() {
        return this.storage.removedLoadedChunks[this.storage.updatingSetsIndex];
    }

    @Overwrite
    public void flipUpdateTrackingSets() {
        this.storage.updatingSetsIndex = (this.storage.updatingSetsIndex + 1) % 2;
        this.storage.addedEmptySections[this.storage.updatingSetsIndex].clear();
        this.storage.removedEmptySections[this.storage.updatingSetsIndex].clear();
        this.storage.addedLoadedChunks[this.storage.updatingSetsIndex].clear();
        this.storage.removedLoadedChunks[this.storage.updatingSetsIndex].clear();
    }

    @Overwrite
    public void onSectionEmptinessChanged(int sectionX, int sectionY, int sectionZ, boolean empty) {
        this.storage.onSectionEmptinessChanged(sectionX, sectionY, sectionZ, empty);
    }
}


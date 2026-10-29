package cc.sighs.abyssgenerator.world.section;

import cc.sighs.abyssgenerator.world.bridge.UnknownSection;
import cc.sighs.abyssgenerator.world.bridge.VanillaColumnBridge;
import cc.sighs.abyssgenerator.world.coord.SectionKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.ChunkPos;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

/** Owned by one ServerLevel, never retained in a static world cache. */
public final class SectionManager {
    private final ServerLevel level;
    private final Map<SectionKey, SectionRecord> records = new HashMap<>();
    private final Map<ChunkPos, ColumnPreparation> columns = new HashMap<>();
    private final PendingPreparations<SectionKey, SectionRecord> preparations;
    private boolean closed;
    private static final class ColumnPreparation {
        final CompletableFuture<?> future;
        int users = 1;
        ColumnPreparation(CompletableFuture<?> future) { this.future = future; }
    }
    public SectionManager(ServerLevel level) {
        this.level = level;
        preparations = new PendingPreparations<>(256, System::nanoTime, level.getServer(),
                this::loadColumn, this::installPreparedSection, this::releaseColumn);
    }
    public static SectionManager of(ServerLevel level) { return ((SectionManagerAccess) level).abyssgenerator$sections(); }

    /** Controlled P1 initialization only. Gameplay paths must never call this blocking column load. */
    public SectionRecord ensureSection(SectionKey key) {
        validateRequest(key);
        level.getChunk(key.x(), key.z());
        return installPreparedSection(key);
    }

    /**
     * Bounded, non-blocking administrative bridge probe. Still loads a vanilla
     * column and only initializes the empty flat bridge fixture; not worldgen.
     */
    public CompletionStage<SectionRecord> prepareSection(SectionKey key, Duration timeout) {
        validateRequest(key);
        return preparations.request(key, timeout);
    }

    public boolean cancelPreparation(SectionKey key) { return preparations.cancel(key); }
    public int pendingCount() { return preparations.size(); }
    public int preparationColumnCount() { checkThread(); return columns.size(); }
    public void tick() { preparations.tick(); }

    /** Non-loading readiness query. Empty means unknown, never generated air. */
    public Optional<SectionRecord> readySection(SectionKey key) {
        checkThread();
        return Optional.ofNullable(records.get(key));
    }

    private void validateRequest(SectionKey key) {
        checkThread();
        if (closed) throw new IllegalStateException("Section manager closed");
        if (!VanillaColumnBridge.enabled(level)) throw new IllegalStateException("Sparse bridge is not enabled");
        if (key.y() < level.getMinSectionY() || key.y() > level.getMaxSectionY())
            throw new IllegalArgumentException("Section outside dimension: " + key);
    }

    private CompletionStage<?> loadColumn(SectionKey key) {
        var pos = new ChunkPos(key.x(), key.z());
        var existing = columns.get(pos);
        if (existing != null) { existing.users++; return existing.future; }
        // getChunkFuture blocks when called on the server thread in 26.1.2.
        // The ticket API schedules loading without pumping a synchronous wait.
        try {
            var future = level.getChunkSource().addTicketAndLoadWithRadius(PreparationTickets.ADMIN.get(), pos, 0);
            columns.put(pos, new ColumnPreparation(future));
            return future;
        } catch (RuntimeException error) {
            level.getChunkSource().removeTicketWithRadius(PreparationTickets.ADMIN.get(), pos, 0);
            throw error;
        }
    }

    private void releaseColumn(SectionKey key) {
        var pos = new ChunkPos(key.x(), key.z());
        var column = columns.get(pos);
        if (column != null && --column.users == 0) {
            columns.remove(pos);
            level.getChunkSource().removeTicketWithRadius(PreparationTickets.ADMIN.get(), pos, 0);
        }
    }

    private SectionRecord installPreparedSection(SectionKey key) {
        validateRequest(key);
        LevelChunk chunk = level.getChunkSource().getChunkNow(key.x(), key.z());
        if (chunk == null) throw new IllegalStateException("Column not ready after preparation: " + key);
        int index = level.getSectionIndexFromSectionY(key.y());
        LevelChunkSection data = chunk.getSection(index);
        if (data instanceof UnknownSection) {
            data = new LevelChunkSection(level.palettedContainerFactory());
            chunk.getSections()[index] = data;
            chunk.markUnsaved();
        }
        var previous = records.get(key);
        if (previous != null) {
            if (previous.data() != data) throw new IllegalStateException("Duplicate section owner: " + key);
            return previous;
        }
        var record = new SectionRecord(key, data);
        records.put(key, record);
        return record;
    }

    public int loadedCount() { checkThread(); return records.size(); }
    public void adoptColumn(LevelChunk chunk) {
        checkThread();
        if (closed) return;
        for (int index = 0; index < chunk.getSections().length; index++) {
            var data = chunk.getSection(index);
            if (data instanceof UnknownSection) continue;
            var key = new SectionKey(chunk.getPos().x(), level.getSectionYFromSectionIndex(index), chunk.getPos().z());
            var previous = records.putIfAbsent(key, new SectionRecord(key, data));
            if (previous != null && previous.data() != data) throw new IllegalStateException("Duplicate section owner: " + key);
        }
    }
    public void unloadColumn(int x, int z) { checkThread(); records.keySet().removeIf(k -> k.x() == x && k.z() == z); }
    public void close() { checkThread(); closed = true; preparations.close(); records.clear(); }
    private void checkThread() {
        if (!level.getServer().isSameThread()) throw new IllegalStateException("Section manager accessed off server thread");
    }
}

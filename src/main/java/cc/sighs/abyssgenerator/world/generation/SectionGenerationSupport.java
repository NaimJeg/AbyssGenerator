package cc.sighs.abyssgenerator.world.generation;

import java.util.Objects;
import net.minecraft.world.level.chunk.ChunkGenerator;

/** Capability reporting only: never silently falls back to whole-column generation. */
public enum SectionGenerationSupport {
    NATIVE_SECTION,
    COLUMN_ADAPTER_REQUIRED;

    public static SectionGenerationSupport of(ChunkGenerator generator) {
        Objects.requireNonNull(generator, "generator");
        return generator instanceof SectionTerrainGenerator ? NATIVE_SECTION : COLUMN_ADAPTER_REQUIRED;
    }

    public static SectionTerrainGenerator requireNative(ChunkGenerator generator) {
        if (of(generator) == NATIVE_SECTION) return (SectionTerrainGenerator) generator;
        throw new UnsupportedOperationException("Section generation is not implemented for "
                + generator.getClass().getName() + "; whole-column fallback is not permitted");
    }
}

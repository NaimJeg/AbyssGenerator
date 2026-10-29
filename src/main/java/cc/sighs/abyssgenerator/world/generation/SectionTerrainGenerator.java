package cc.sighs.abyssgenerator.world.generation;

import cc.sighs.abyssgenerator.world.coord.SectionKey;
import cc.sighs.abyssgenerator.world.coord.VerticalRange;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;

/**
 * Native, dimension-independent generation contract. Implementations must be thread safe,
 * return exclusively owned data, and must not access a live world or generate other sections.
 * The returned section includes biomes. Installation and lighting belong to the caller.
 * This contract does not adapt arbitrary vanilla column generators.
 */
public interface SectionTerrainGenerator {
    LevelChunkSection generateSection(SectionKey key, VerticalRange range, PalettedContainerFactory factory);
}

package cc.sighs.abyssgenerator.world.section;

import cc.sighs.abyssgenerator.world.coord.SectionKey;
import net.minecraft.world.level.chunk.LevelChunkSection;

/** P1 main-thread ownership record; persistent revision tracking is introduced with P2. */
public record SectionRecord(SectionKey key, LevelChunkSection data) {}

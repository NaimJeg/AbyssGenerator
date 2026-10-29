package cc.sighs.abyssgenerator.world.bridge;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;

/** Experimental P1 bridge. Never enabled implicitly for an existing save. */
public final class VanillaColumnBridge {
    private VanillaColumnBridge() {}

    public static boolean enabled(LevelHeightAccessor accessor) {
        return Boolean.getBoolean("abyssgenerator.sparseSpike") && accessor instanceof Level level
                && level.dimension().identifier().toString().equals("abyssgenerator:bridge_fixture") && !level.isClientSide();
    }

    public static void fillMissing(LevelHeightAccessor accessor, PalettedContainerFactory factory, LevelChunkSection[] sections) {
        LevelChunkSection empty = null;
        boolean sparse = enabled(accessor);
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] != null) continue;
            if (!sparse) sections[i] = new LevelChunkSection(factory);
            else {
                if (empty == null) empty = new UnknownSection(factory);
                sections[i] = empty;
            }
        }
    }
}

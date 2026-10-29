package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.generation.MengerSponge;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MengerSpongeTest {
    @Test void completeCellMatchesRecursiveSubdivision() {
        int filled = 0;
        for (int x = 0; x < 48; x++) for (int y = 0; y < 48; y++) for (int z = 0; z < 48; z++) {
            boolean solid = MengerSponge.isSolid(x, y, z);
            assertEquals(reference((x + .5) / 48, (y + .5) / 48, (z + .5) / 48), solid);
            assertEquals(solid, MengerSponge.isSolid(47 - x, 47 - y, 47 - z));
            assertEquals(solid, MengerSponge.isSolid(z, x, y));
            if (solid) filled++;
        }
        assertTrue(filled > 0 && filled < 48 * 48 * 48);
    }

    private boolean reference(double x, double y, double z) {
        for (int depth = 0; depth < 3; depth++) {
            x *= 3; y *= 3; z *= 3;
            if (((int)x == 1 ? 1 : 0) + ((int)y == 1 ? 1 : 0) + ((int)z == 1 ? 1 : 0) >= 2) return false;
            x -= (int)x; y -= (int)y; z -= (int)z;
        }
        return true;
    }

    @Test void repeatsAcrossNegativeCoordinatesAndSectionSeams() {
        for (int x = -100; x <= 100; x++) for (int y = -100; y <= 100; y++) {
            assertEquals(MengerSponge.isSolid(x, y, -17), MengerSponge.isSolid(x + 48, y - 48, 31));
        }
        assertEquals(MengerSponge.isSolid(Integer.MIN_VALUE, Integer.MAX_VALUE, 0),
                MengerSponge.isSolid(Math.floorMod(Integer.MIN_VALUE, 48), Math.floorMod(Integer.MAX_VALUE, 48), 0));
    }

    @Test void hasThroughHolesAndNaturalSpawnFloor() {
        for (int axis = -96; axis <= 96; axis++) {
            assertFalse(MengerSponge.isSolid(axis, 24, 24));
            assertFalse(MengerSponge.isSolid(24, axis, 24));
            assertFalse(MengerSponge.isSolid(24, 24, axis));
        }
        assertTrue(MengerSponge.isSolid(24, 15, 0));
        assertFalse(MengerSponge.isSolid(24, 16, 0));
        assertFalse(MengerSponge.isSolid(24, 17, 0));
    }
}

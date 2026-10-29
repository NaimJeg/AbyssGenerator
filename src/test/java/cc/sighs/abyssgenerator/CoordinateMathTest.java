package cc.sighs.abyssgenerator;

import cc.sighs.abyssgenerator.world.coord.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class CoordinateMathTest {
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -400000000, -2033, -2032, -1024, -257, -256, -255,
            -17, -16, -1, 0, 15, 16, 255, 256, 257, 1024, 2031, 2032, 400000000, Integer.MAX_VALUE})
    void floorCoordinatesRoundTrip(int y) {
        // Extreme integers are arithmetic checks, not supported world heights.
        var section = SectionKey.fromBlock(-17, y, -1);
        assertEquals(-2, section.x());
        assertEquals(-1, section.z());
        assertEquals(section, section.page().section(section.indexInPage()));
        assertEquals(y, (long) PageKey.blockToPage(y) * 256 + PageKey.localBlockY(y));
        assertEquals(y, (long) section.y() * 16 + Math.floorMod(y, 16));
    }

    @Test void finiteRangeAndPartialPages() {
        var range = VerticalRange.ABYSS;
        assertTrue(range.containsBlock(-2032));
        assertTrue(range.containsBlock(2031));
        assertFalse(range.containsBlock(-2033));
        assertFalse(range.containsBlock(2032));
        assertTrue(range.containsSection(-127));
        assertTrue(range.containsSection(126));
        assertFalse(range.containsSection(-128));
        assertFalse(range.containsSection(127));
        assertThrows(IllegalArgumentException.class, () -> range.requireBlock(2032));
        assertThrows(IllegalArgumentException.class, () -> new VerticalRange(1, 16));
        assertThrows(IllegalArgumentException.class, () -> new PageKey(0, 0, 0).section(16));
        assertThrows(ArithmeticException.class, () -> new PageKey(0, Integer.MAX_VALUE, 0).minBlockY());
    }
}

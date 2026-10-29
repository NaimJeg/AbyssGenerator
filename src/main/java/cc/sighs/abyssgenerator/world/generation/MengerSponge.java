package cc.sighs.abyssgenerator.world.generation;

/** Three-level Menger sponge sampled at voxel centres and tiled in all axes. */
public final class MengerSponge {
    public static final int EDGE = 48;
    public static final int DEPTH = 3;
    private static final int[] DIGITS = new int[EDGE];
    static {
        for (int i = 0; i < EDGE; i++) {
            int coordinate = ((2 * i + 1) * 27) / (2 * EDGE);
            int mask = 0;
            for (int depth = 0; depth < DEPTH; depth++, coordinate /= 3)
                if (coordinate % 3 == 1) mask |= 1 << depth;
            DIGITS[i] = mask;
        }
    }
    private MengerSponge() {}

    public static boolean isSolid(int x, int y, int z) {
        int a = DIGITS[Math.floorMod(x, EDGE)];
        int b = DIGITS[Math.floorMod(y, EDGE)];
        int c = DIGITS[Math.floorMod(z, EDGE)];
        return ((a & b) | (b & c) | (a & c)) == 0;
    }
}

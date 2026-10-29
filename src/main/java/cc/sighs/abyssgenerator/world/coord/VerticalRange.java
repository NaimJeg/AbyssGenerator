package cc.sighs.abyssgenerator.world.coord;

public record VerticalRange(int minY, int height) {
    public static final VerticalRange ABYSS = new VerticalRange(-2032, 4064);

    public VerticalRange {
        if (Math.floorMod(minY, 16) != 0 || height <= 0 || height % 16 != 0)
            throw new IllegalArgumentException("Range must contain complete sections");
        Math.addExact(minY, height);
    }

    public int maxYExclusive() { return Math.addExact(minY, height); }
    public boolean containsBlock(int y) { return y >= minY && y < maxYExclusive(); }
    public boolean containsSection(int y) { return y >= Math.floorDiv(minY, 16) && y < Math.floorDiv(maxYExclusive(), 16); }
    public void requireBlock(int y) {
        if (!containsBlock(y)) throw new IllegalArgumentException("Y=" + y + " outside [" + minY + "," + maxYExclusive() + ")");
    }
}

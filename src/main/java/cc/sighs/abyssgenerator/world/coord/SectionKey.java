package cc.sighs.abyssgenerator.world.coord;

/** Coordinates are dimension-local. Never use this as a cross-world key. */
public record SectionKey(int x, int y, int z) {
    public static SectionKey fromBlock(int x, int y, int z) {
        return new SectionKey(Math.floorDiv(x, 16), Math.floorDiv(y, 16), Math.floorDiv(z, 16));
    }

    public PageKey page() { return new PageKey(x, Math.floorDiv(y, 16), z); }
    public int indexInPage() { return Math.floorMod(y, 16); }
    public int minBlockY() { return Math.multiplyExact(y, 16); }
}

package cc.sighs.abyssgenerator.world.coord;

public record PageKey(int x, int y, int z) {
    public static final int HEIGHT = 256;
    public static final int SECTIONS = 16;

    public static int blockToPage(int y) { return Math.floorDiv(y, HEIGHT); }
    public static int localBlockY(int y) { return Math.floorMod(y, HEIGHT); }
    public int minBlockY() { return Math.multiplyExact(y, HEIGHT); }
    public SectionKey section(int index) {
        if (index < 0 || index >= SECTIONS) throw new IllegalArgumentException("Page index outside [0,16): " + index);
        return new SectionKey(x, Math.addExact(Math.multiplyExact(y, SECTIONS), index), z);
    }
}

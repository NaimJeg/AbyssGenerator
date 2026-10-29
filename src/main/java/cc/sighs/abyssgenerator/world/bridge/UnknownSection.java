package cc.sighs.abyssgenerator.world.bridge;

import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;

/** Immutable compatibility view, never a generated-empty record. */
public final class UnknownSection extends LevelChunkSection {
    public UnknownSection(PalettedContainerFactory factory) { super(factory); }
    private IllegalStateException immutable() { return new IllegalStateException("Unknown section: explicitly ensure readiness before writing"); }
    @Override public BlockState setBlockState(int x, int y, int z, BlockState state, boolean check) { throw immutable(); }
    @Override public BlockState setBlockState(int x, int y, int z, BlockState state) { throw immutable(); }
    @Override public void read(FriendlyByteBuf buffer) { throw immutable(); }
    @Override public void readBiomes(FriendlyByteBuf buffer) { throw immutable(); }
    @Override public void fillBiomesFromNoise(BiomeResolver resolver, Climate.Sampler sampler, int x, int y, int z) { throw immutable(); }
    // Legacy APIs expose mutable palettes. Return detached copies rather than allowing alias writes.
    @Override public PalettedContainer<BlockState> getStates() { return super.getStates().copy(); }
    @Override public PalettedContainerRO<Holder<Biome>> getBiomes() { return super.getBiomes().copy(); }
    @Override public LevelChunkSection copy() { return this; }
}

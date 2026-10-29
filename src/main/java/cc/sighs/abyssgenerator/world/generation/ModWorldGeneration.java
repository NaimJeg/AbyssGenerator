package cc.sighs.abyssgenerator.world.generation;

import cc.sighs.abyssgenerator.AbyssGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModWorldGeneration {
    private static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, AbyssGenerator.MODID);
    static { GENERATORS.register("fractal", () -> FractalChunkGenerator.CODEC); }
    private ModWorldGeneration() {}
    public static void register(IEventBus bus) { GENERATORS.register(bus); }
}

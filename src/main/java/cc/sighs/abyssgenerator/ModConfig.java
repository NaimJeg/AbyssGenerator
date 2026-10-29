package cc.sighs.abyssgenerator;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue HORIZONTAL_RADIUS = BUILDER.defineInRange("horizontalRadius", 4, 1, 16);
    public static final ModConfigSpec.IntValue VERTICAL_RADIUS = BUILDER.defineInRange("verticalRadius", 4, 1, 16);
    public static final ModConfigSpec.IntValue SIMULATION_RADIUS = BUILDER.defineInRange("simulationRadius", 2, 0, 16);
    public static final ModConfigSpec.IntValue RETENTION_MARGIN = BUILDER.defineInRange("retentionMargin", 1, 0, 4);
    public static final ModConfigSpec.IntValue UNLOAD_DELAY = BUILDER.defineInRange("unloadDelayTicks", 40, 0, 1200);
    public static final ModConfigSpec.IntValue GENERATION_THREADS = BUILDER.defineInRange("generationThreads",
            Math.min(4, Math.max(1, Runtime.getRuntime().availableProcessors() - 2)), 1, 16);
    public static final ModConfigSpec.IntValue QUEUE_LIMIT = BUILDER.defineInRange("queueLimit", 256, 1, 4096);
    public static final ModConfigSpec.DoubleValue INSTALL_BUDGET_MS = BUILDER.comment("Soft budget; one installation may exceed it.")
            .defineInRange("installBudgetMs", 2.0, 0.1, 50.0);
    public static final ModConfigSpec.IntValue SEND_BYTES_PER_TICK = BUILDER.defineInRange("sendBytesPerTick", 262144, 65536, 4194304);
    static final ModConfigSpec SPEC = BUILDER.build();
}

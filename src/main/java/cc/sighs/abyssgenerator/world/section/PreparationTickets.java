package cc.sighs.abyssgenerator.world.section;

import cc.sighs.abyssgenerator.AbyssGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.TicketType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

/** Nonpersistent and explicitly released; never share the vanilla spawn ticket. */
public final class PreparationTickets {
    private static final DeferredRegister<TicketType> TYPES = DeferredRegister.create(Registries.TICKET_TYPE, AbyssGenerator.MODID);
    public static final Supplier<TicketType> ADMIN = TYPES.register("section_preparation",
            () -> new TicketType(0, TicketType.FLAG_LOADING | TicketType.FLAG_KEEP_DIMENSION_ACTIVE));
    private PreparationTickets() {}
    public static void register(IEventBus bus) { TYPES.register(bus); }
}

package cc.sighs.abyssgenerator.mixin;

import cc.sighs.abyssgenerator.world.section.*;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements SectionManagerAccess, cc.sighs.abyssgenerator.world.generation.GenerationAuditAccess {
    @Unique private final cc.sighs.abyssgenerator.world.generation.GenerationAudit abyssgenerator$audit =
            new cc.sighs.abyssgenerator.world.generation.GenerationAudit();
    @Override public cc.sighs.abyssgenerator.world.generation.GenerationAudit abyssgenerator$generationAudit() {
        return abyssgenerator$audit;
    }
    @Unique private SectionManager abyssgenerator$manager;
    @Override public SectionManager abyssgenerator$sections() {
        if (abyssgenerator$manager == null) abyssgenerator$manager = new SectionManager((ServerLevel) (Object) this);
        return abyssgenerator$manager;
    }
}

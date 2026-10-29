/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.world.level.chunk.storage.SerializableChunkData
 *  net.minecraft.world.level.chunk.storage.SerializableChunkData$SectionData
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.jfyx2025.chunkcutter.mixin;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SerializableChunkData.class})
public abstract class SerializableChunkDataMixin {
    @Shadow
    private List<SerializableChunkData.SectionData> sectionData;
    private static final ThreadLocal<CompoundTag> chunkcutter$readTag = new ThreadLocal();

    @Redirect(method={"parse"}, at={@At(value="INVOKE", target="Lnet/minecraft/nbt/CompoundTag;getByteOr(Ljava/lang/String;B)B")})
    private static byte chunkcutter$captureReadTag(CompoundTag tag, String key, byte def) {
        if ("Y".equals(key)) {
            chunkcutter$readTag.set(tag);
        }
        return tag.getByteOr(key, def);
    }

    @ModifyVariable(method={"parse"}, at={@At(value="STORE")}, ordinal=1)
    private static int chunkcutter$readYAsInt(int y) {
        CompoundTag tag = chunkcutter$readTag.get();
        return tag != null ? tag.getIntOr("Y", y) : y;
    }

    @ModifyArg(method={"parse"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/storage/SerializableChunkData$SectionData;<init>(ILnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/DataLayer;Lnet/minecraft/world/level/chunk/DataLayer;)V")}, index=0)
    private static int chunkcutter$readSectionYAsInt(int y) {
        CompoundTag tag = chunkcutter$readTag.get();
        return tag != null ? tag.getIntOr("Y", y) : y;
    }

    @Inject(method={"parse"}, at={@At(value="RETURN")})
    private static void chunkcutter$clearReadTag(CallbackInfoReturnable<SerializableChunkData> cir) {
        chunkcutter$readTag.remove();
    }

    @Inject(method={"write"}, at={@At(value="RETURN")})
    private void chunkcutter$writeYAsInt(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag root = (CompoundTag)cir.getReturnValue();
        if (root == null) {
            return;
        }
        ListTag sections = root.getListOrEmpty("sections");
        int outIndex = 0;
        for (SerializableChunkData.SectionData section : this.sectionData) {
            if (section.chunkSection() == null && section.blockLight() == null && section.skyLight() == null) continue;
            if (outIndex >= sections.size()) break;
            CompoundTag sectionTag = sections.getCompound(outIndex).orElse(null);
            if (sectionTag != null) {
                sectionTag.putInt("Y", section.y());
            }
            ++outIndex;
        }
    }
}


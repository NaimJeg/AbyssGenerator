/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.Ticket
 *  net.minecraft.world.level.ChunkPos
 */
package com.jfyx2025.chunkcutter.cubic;

import java.util.function.BiConsumer;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;

public interface TicketSource {
    public void chunkcutter$forEachTicket(BiConsumer<ChunkPos, Ticket> var1);
}


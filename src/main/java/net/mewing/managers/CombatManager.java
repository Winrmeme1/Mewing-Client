/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.managers;

import net.mewing.Mewing;
import net.mewing.event.events.ReceivePacketEvent;
import net.mewing.event.events.TickEvent;
import net.mewing.event.events.TotemPopEvent;
import net.mewing.event.listeners.ReceivePacketListener;
import net.mewing.event.listeners.TickListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static net.mewing.MewingClient.MC;

public class CombatManager implements TickListener, ReceivePacketListener {
    public HashMap<String, Integer> popList = new HashMap<>();

    public CombatManager() {
        Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
        Mewing.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
    }

    @Override
    public void onReceivePacket(ReceivePacketEvent event) {
        if (event.GetPacket() instanceof ClientboundEntityEventPacket entityStatusS2CPacket) {
            if (entityStatusS2CPacket.getEventId() == EntityEvent.PROTECTED_FROM_DEATH) {
                Entity entity = entityStatusS2CPacket.getEntity(MC.level);

                if (!(entity instanceof Player)) return;

                if (popList == null) {
                    popList = new HashMap<>();
                }

                if (popList.get(entity.getName().getString()) == null) {
                    popList.put(entity.getName().getString(), 1);
                } else if (popList.get(entity.getName().getString()) != null) {
                    popList.put(entity.getName().getString(), popList.get(entity.getName().getString()) + 1);
                }

                Mewing.getInstance().eventManager.Fire(new TotemPopEvent((Player) entity, popList.get(entity.getName().getString())));
            }
        }
    }

    @Override
    public void onTick(TickEvent.Pre event) {

    }
    
    @Override
    public void onTick(TickEvent.Post event) {
        for (Player player : MC.level.players()) {
            if (player.getHealth() <= 0 && popList.containsKey(player.getName().getString()))
                popList.remove(player.getName().getString(), popList.get(player.getName().getString()));
        }
    }

    public int getPops(@NotNull Player entity) {
        if (popList.get(entity.getName().getString()) == null) return 0;
        return popList.get(entity.getName().getString());
    }
}
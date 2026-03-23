/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.misc;

import net.mewing.Mewing;
import net.mewing.event.events.SendPacketEvent;
import net.mewing.event.listeners.SendPacketListener;
import net.mewing.module.AntiCheat;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;

public class XCarry extends Module implements SendPacketListener {
	public XCarry() {
		super("XCarry");
		setCategory(Category.of("Misc"));
		setDescription("Allows you to store items in your crafting slot..");

		isDetectable(AntiCheat.Negativity);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(SendPacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if (packet instanceof ServerboundContainerClosePacket closeScreenPacket) {
            if (closeScreenPacket.getContainerId() == MC.player.inventoryMenu.containerId)
				event.cancel();
		}
	}
}

/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.event.events;

import java.util.ArrayList;

import net.mewing.event.listeners.AbstractListener;
import net.mewing.event.listeners.ReceivePacketListener;
import net.minecraft.network.protocol.Packet;

public class ReceivePacketEvent extends AbstractEvent {

	private final Packet<?> packet;

	public Packet<?> GetPacket() {
		return packet;
	}

	public ReceivePacketEvent(Packet<?> packet) {
		this.packet = packet;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			ReceivePacketListener readPacketListener = (ReceivePacketListener) listener;
			readPacketListener.onReceivePacket(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<ReceivePacketListener> GetListenerClassType() {
		return ReceivePacketListener.class;
	}
}

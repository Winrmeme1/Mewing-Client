/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.event.listeners;

import net.mewing.event.events.SendMovementPacketEvent;

public interface SendMovementPacketListener extends AbstractListener {
    void onSendMovementPacket(SendMovementPacketEvent.Pre event);
    void onSendMovementPacket(SendMovementPacketEvent.Post event);
}

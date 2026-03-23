/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.navigation.huds;

import net.mewing.gui.GuiManager;
import net.mewing.gui.Rectangle;
import net.mewing.gui.ResizeMode;
import net.mewing.gui.navigation.HudWindow;
import net.mewing.utils.render.Render2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingHud extends HudWindow {
	private static final Minecraft MC = Minecraft.getInstance();
	String pingText = null;

	public PingHud(int x, int y) {
		super("PingHud", x, y, 50, 24);
		minWidth = 50f;
		minHeight = 20f;
		maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		ClientPacketListener networkHandler = MC.getConnection();
		if (networkHandler != null && MC.player != null) {
			PlayerInfo entry = networkHandler.getPlayerInfo(MC.player.getUUID());
			if (entry != null) {
				int ping = entry.getLatency();
				pingText = "Ping: " + ping + " ms";
			} else {
				pingText = "Ping: ?";
			}
		} else
			pingText = null;
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		if (pingText != null && isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				Render2D.drawString(drawContext, pingText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}
	}
}

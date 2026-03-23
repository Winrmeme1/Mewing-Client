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

public class FPSHud extends HudWindow {

	private static final Minecraft MC = Minecraft.getInstance();

	public FPSHud(int x, int y) {
		super("FPSHud", x, y, 50, 24);
		minWidth = 50f;
		minHeight = 20f;
		maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				int fps = MC.getFps();
				String fpsText = "FPS: " + fps;
				Render2D.drawString(drawContext, fpsText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}
		super.draw(drawContext, partialTicks);
	}
}
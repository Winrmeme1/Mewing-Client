/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.navigation;

import java.util.List;

import net.mewing.Mewing;
import net.mewing.gui.Rectangle;
import net.mewing.gui.UIElement;
import net.mewing.gui.colors.Color;
import net.mewing.managers.SettingManager;
import net.mewing.settings.types.BooleanSetting;
import net.mewing.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;

public class HudWindow extends Window {
	private static final Color hoverColor = new Color(255, 0, 0);
	private static final Color dragColor = new Color(255, 0, 0, 165);
	public BooleanSetting activated;

	public CloseableWindow optionsWindow;

	public HudWindow(String ID, float x, float y) {
		this(ID, x, y, 180.0f, 50f);
	}

	public HudWindow(String ID, float x, float y, float width, float height) {
		super(ID, x, y, width, height);
		activated = BooleanSetting.builder().id(ID + "_activated").defaultValue(false)
				.onUpdate(val -> Mewing.getInstance().guiManager.setHudActive(this, val)).build();

		SettingManager.registerSetting(activated);
	}

	private void onActivatedChanged(Boolean state) {
		Mewing.getInstance().guiManager.setHudActive(this, state.booleanValue());
	}

	@Override
	public boolean isVisible() {
		return activated.getValue().booleanValue();
	}

	// Override to do nothing.. We want it to be visible based off of whether it is
	// activated.
	@Override
	public void setVisible(boolean state) {
		if (!state) {
			isMoving = false;
		}
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		if (isVisible()) {
			List<UIElement> children = getChildren();
			for (UIElement child : children) {
				child.draw(drawContext, partialTicks);
			}

			Rectangle pos = getActualSize();

			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();
			float height = pos.getHeight().floatValue();

			if (isMoving) {
				if (pos.isDrawable()) {
					Render2D.drawBox(drawContext, x, y, width, height, dragColor);
				}
			}
			if (Mewing.getInstance().guiManager.isClickGuiOpen()) {
				Render2D.drawBoxOutline(drawContext, x, y, width, height, hoverColor);
			}
		}
	}
}

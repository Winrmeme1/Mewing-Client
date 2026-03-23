/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.navigation.windows;

import java.util.ArrayList;

import net.mewing.gui.Thickness;
import net.mewing.gui.components.HudComponent;
import net.mewing.gui.components.SeparatorComponent;
import net.mewing.gui.components.StackPanelComponent;
import net.mewing.gui.components.StringComponent;
import net.mewing.gui.navigation.HudWindow;
import net.mewing.gui.navigation.Window;
import net.minecraft.client.gui.GuiGraphics;

public class ToggleHudsTab extends Window {
	public ToggleHudsTab(ArrayList<HudWindow> huds) {
		super("Toggle HUDs", 0, 0);

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(4f);
		stackPanel.addChild(new StringComponent("Toggle HUDs"));
		stackPanel.addChild(new SeparatorComponent());

		for (HudWindow hud : huds) {
			HudComponent hudComponent = new HudComponent(hud.getID(), hud);
			stackPanel.addChild(hudComponent);
		}

		addChild(stackPanel);
		setMinWidth(300.0f);
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
	}
}

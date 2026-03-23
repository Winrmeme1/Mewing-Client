/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.navigation.windows;

import java.util.Arrays;
import java.util.List;

import com.mojang.logging.LogUtils;

import net.mewing.Mewing;
import net.mewing.gui.GuiManager;
import net.mewing.gui.Thickness;
import net.mewing.gui.components.CheckboxComponent;
import net.mewing.gui.components.ColorPickerComponent;
import net.mewing.gui.components.KeybindComponent;
import net.mewing.gui.components.ListComponent;
import net.mewing.gui.components.SliderComponent;
import net.mewing.gui.components.StackPanelComponent;
import net.mewing.gui.components.StringComponent;
import net.mewing.gui.navigation.Window;
import net.minecraft.client.gui.GuiGraphics;

public class HudOptionsWindow extends Window {
	public HudOptionsWindow() {
		super("Hud Options", 600, 200);

		minWidth = 340.0f;

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(4f);
		List<String> fontNames = Mewing.getInstance().fontManager.fontRenderers.keySet().stream().toList();
		LogUtils.getLogger().info(Arrays.toString(fontNames.toArray()));

		// Keybinds Header
		stackPanel.addChild(new StringComponent("Keybinds", GuiManager.foregroundColor.getValue(), true));

		KeybindComponent clickGuiKeybindComponent = new KeybindComponent(Mewing.getInstance().guiManager.clickGuiButton);

		stackPanel.addChild(clickGuiKeybindComponent);

		// Hud Font Header
		stackPanel.addChild(new StringComponent("HUD Font", GuiManager.foregroundColor.getValue(), true));

		ListComponent listComponent = new ListComponent(fontNames, Mewing.getInstance().fontManager.fontSetting);
		stackPanel.addChild(listComponent);

		stackPanel.addChild(new StringComponent("HUD Colors", GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new ColorPickerComponent(GuiManager.foregroundColor));
		stackPanel.addChild(new ColorPickerComponent(GuiManager.backgroundColor));
		stackPanel.addChild(new ColorPickerComponent(GuiManager.borderColor));

		stackPanel.addChild(new StringComponent("Hud Styling", GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new SliderComponent(GuiManager.roundingRadius));

		stackPanel
				.addChild(new StringComponent("GUI / HUD Responsiveness", GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new SliderComponent(GuiManager.dragSmoothening));
		stackPanel.addChild(new CheckboxComponent(GuiManager.enableCustomTitle));
		stackPanel.addChild(new CheckboxComponent(GuiManager.enableTooltips));

		addChild(stackPanel);
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
	}
}

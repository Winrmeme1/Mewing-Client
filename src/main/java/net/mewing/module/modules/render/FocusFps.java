/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.render;

import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.FloatSetting;

public class FocusFps extends Module {
	private final FloatSetting fps = FloatSetting.builder().id("focusfps_fps").displayName("FPS")
			.description("The FPS for when the window is not in focus.").defaultValue(30f).minValue(1f).maxValue(45f)
			.step(1f).build();

	public FocusFps() {
		super("FocusFPS");
		setCategory(Category.of("Render"));
		setDescription("Limits the FPS of the game when it is not focused.");
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	public Float getFps() {
		return fps.getValue();
	}
}

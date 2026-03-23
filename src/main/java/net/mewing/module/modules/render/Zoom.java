/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.render;

import net.mewing.Mewing;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.events.TickEvent.Pre;
import net.mewing.event.listeners.TickListener;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.FloatSetting;
import net.minecraft.client.OptionInstance;

public class Zoom extends Module implements TickListener {

	private Integer lastFov = null;

	private final FloatSetting zoomFactor = FloatSetting.builder().id("zoom_factor").displayName("Factor")
			.description("The zoom factor that the zoom will use.").defaultValue(2f).minValue(1f).maxValue(3.6f)
			.step(0.1f).build();

	public Zoom() {
		super("Zoom");
		setCategory(Category.of("Render"));
		setDescription("Zooms the players camera to see further.");
		addSetting(zoomFactor);
	}

	@Override
	public void onDisable() {
		if (lastFov != null) {
			MC.options.fov().set(Math.max(30, Math.min(110, lastFov)));
			Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);
		}
	}

	@Override
	public void onEnable() {
		lastFov = MC.options.fov().get();
		Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		OptionInstance<Integer> fov = MC.options.fov();
		int newZoom = (int) Math.max(30, Math.min(110, lastFov / zoomFactor.getValue()));
		fov.set(newZoom);
	}
}

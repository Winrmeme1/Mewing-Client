/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.render;

import java.util.LinkedList;

import net.mewing.Mewing;
import net.mewing.event.events.Render3DEvent;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.events.TickEvent.Pre;
import net.mewing.event.listeners.Render3DListener;
import net.mewing.event.listeners.TickListener;
import net.mewing.gui.colors.Color;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.ColorSetting;
import net.mewing.utils.render.Render3D;
import net.minecraft.world.phys.Vec3;

public class Breadcrumbs extends Module implements Render3DListener, TickListener {

	private final ColorSetting color = ColorSetting.builder().id("breadcrumbs_color").displayName("Color")
			.description("Color").defaultValue(new Color(0, 1f, 1f)).build();

	private final float distanceThreshold = 1.0f; // Minimum distance to record a new position
	private float currentTick = 0;
	private final float timer = 10;
	private final LinkedList<Vec3> positions = new LinkedList<>();
	private final int maxPositions = 1000;

	public Breadcrumbs() {
		super("Breadcrumbs");
		setCategory(Category.of("Render"));
		setDescription("Shows breadcrumbs of where you last stepped;");
		addSetting(color);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);
		positions.clear();
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		Vec3 prevPosition = null;
		for (Vec3 position : positions) {
			if (prevPosition != null) {
				Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), prevPosition, position, color.getValue());
			}
			prevPosition = position;
		}
	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		currentTick++;
		if (timer == currentTick) {
			currentTick = 0;
			if (!Mewing.getInstance().moduleManager.freecam.state.getValue()) {
				Vec3 currentPosition = MC.player.position();
				if (positions.isEmpty() || positions.getLast().distanceToSqr(currentPosition) >= distanceThreshold
						* distanceThreshold) {
					if (positions.size() >= maxPositions) {
						positions.removeFirst();
					}
					positions.add(currentPosition);
				}
			}
		}
	}
}
/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.movement;

import net.mewing.Mewing;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.events.TickEvent.Pre;
import net.mewing.event.listeners.TickListener;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.minecraft.client.player.LocalPlayer;

public class Sneak extends Module implements TickListener {
	public Sneak() {
		super("Sneak");
		setCategory(Category.of("Movement"));
		setDescription("Makes the player appear like they're sneaking.");
	}

	@Override
	public void onDisable() {
		LocalPlayer player = MC.player;
		if (player != null) {
			MC.options.keyShift.setDown(false);
		}
		Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {
		LocalPlayer player = MC.player;
		if (player != null) {
			MC.options.keyShift.setDown(true);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
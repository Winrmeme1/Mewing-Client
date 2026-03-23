/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.misc;

import net.mewing.Mewing;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.events.TickEvent.Pre;
import net.mewing.event.listeners.TickListener;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;

public class AutoWalk extends Module implements TickListener {
	private final BooleanSetting automaticJump = BooleanSetting.builder().id("autowalk_automatic_jump")
			.displayName("Automatically Jump").description("Automatically jumps when you hit a wall.")
			.defaultValue(true).build();

	public AutoWalk() {
		super("AutoWalk");

		setCategory(Category.of("Misc"));
		setDescription("Automatically forward walks for you.");

		addSetting(automaticJump);
	}

	@Override
	public void onDisable() {
		MC.options.keyUp.setDown(false);
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
		MC.options.keyUp.setDown(true);
		if (MC.player.horizontalCollision && MC.player.onGround())
			MC.player.jumpFromGround();
	}

	@Override
	public void onTick(Post event) {

	}
}

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
import net.mewing.mixin.interfaces.ILivingEntity;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.FloatSetting;

public class NoJumpDelay extends Module implements TickListener {

	private final FloatSetting delay = FloatSetting.builder().id("nojumpdelay_delay").displayName("Delay")
			.description("NoJumpDelay Delay.").defaultValue(1f).minValue(0f).maxValue(20f).step(1f).build();

	public NoJumpDelay() {
		super("NoJumpDelay");
		setCategory(Category.of("Movement"));
		setDescription("Makes it so the user can jump very quickly.");

		addSetting(delay);
	}

	@Override
	public void onDisable() {
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
		ILivingEntity ent = (ILivingEntity) MC.player;
		if (ent.getJumpCooldown() > delay.getValue()) {
			ent.setJumpCooldown(delay.getValue().intValue());
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
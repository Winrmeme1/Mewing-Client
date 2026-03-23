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
import net.mewing.mixin.interfaces.IEntity;
import net.mewing.module.AntiCheat;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.FloatSetting;
import net.minecraft.world.phys.Vec3;

public class NoSlowdown extends Module implements TickListener {

	private final FloatSetting slowdownMultiplier = FloatSetting.builder().id("noslowdown_multiplier")
			.displayName("Multiplier").description("NoSlowdown walk speed multiplier.").defaultValue(0f).minValue(0f)
			.maxValue(1f).step(0.1f).build();

	public NoSlowdown() {
		super("NoSlowdown");
		setCategory(Category.of("Movement"));
		setDescription("Prevents the player from being slowed down by blocks.");

		addSetting(slowdownMultiplier);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Karhu
		);
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
		IEntity playerEntity = (IEntity) MC.player;

		if (!playerEntity.getMovementMultiplier().equals(Vec3.ZERO)) {
			float multiplier = slowdownMultiplier.getValue();
			if (multiplier == 0.0f) {
				playerEntity.setMovementMultiplier(Vec3.ZERO);
			} else {
				playerEntity.setMovementMultiplier(Vec3.ZERO.add(1, 1, 1).scale(1 / multiplier));
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}

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
import net.minecraft.world.phys.Vec3;

public class Safewalk extends Module implements TickListener {

	public Safewalk() {
		super("Safewalk");
		setCategory(Category.of("Movement"));
		setDescription("Permanently keeps player in sneaking mode.");
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
		double x = MC.player.getDeltaMovement().x;
		double y = MC.player.getDeltaMovement().y;
		double z = MC.player.getDeltaMovement().z;
		if (MC.player.onGround()) {
			double increment;
			for (increment = 0.05D; x != 0.0D;) {
				if (x < increment && x >= -increment) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= increment;
				} else {
					x += increment;
				}
			}
            while (z != 0.0D)
            {
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            while (x != 0.0D && z != 0.0D)
            {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
        }
		MC.player.setDeltaMovement(new Vec3(x, y, z));
	}

	@Override
	public void onTick(Post event) {

	}
}

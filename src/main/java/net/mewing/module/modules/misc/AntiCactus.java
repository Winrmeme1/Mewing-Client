/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.misc;

import net.mewing.module.Category;
import net.mewing.module.Module;

public class AntiCactus extends Module {

	public AntiCactus() {
		super("AntiCactus");

		setCategory(Category.of("Misc"));
		setDescription("Prevents blocks from hurting you.");
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
}
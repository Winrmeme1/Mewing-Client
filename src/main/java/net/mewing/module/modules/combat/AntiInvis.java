/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.combat;

import net.mewing.module.Category;
import net.mewing.module.Module;

public class AntiInvis extends Module {

    public AntiInvis() {
    	super("AntiInvis");
        setCategory(Category.of("Combat"));
        setDescription("Reveals players who are invisible.");
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
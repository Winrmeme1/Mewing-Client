/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.components;

import net.mewing.gui.UIElement;

public abstract class Component extends UIElement {
	public String header = null;

	public Component() {


	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		hovered = false;
	}
}

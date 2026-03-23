/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.components;

import net.mewing.gui.GuiManager;
import net.mewing.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class ImageComponent extends Component {

	public Identifier image;

	public ImageComponent() {
	}

	public ImageComponent(Identifier image) {
		this();
		this.image = image;
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		if (image != null) {
			float actualX = getActualSize().getX();
			float actualY = getActualSize().getY();
			float actualWidth = getActualSize().getWidth();
			float actualHeight = getActualSize().getHeight();

			Render2D.drawTexturedQuad(drawContext, image, actualX, actualY, actualWidth, actualHeight,
					GuiManager.foregroundColor.getValue());
		}
		super.draw(drawContext, partialTicks);
	}
}

/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.components;

import net.mewing.event.events.MouseClickEvent;
import net.mewing.gui.GuiManager;
import net.mewing.gui.Thickness;
import net.mewing.gui.colors.Color;
import net.mewing.utils.render.Render2D;
import net.mewing.utils.types.MouseAction;
import net.mewing.utils.types.MouseButton;
import net.minecraft.client.gui.GuiGraphics;

public class ButtonComponent extends Component {

	private Runnable onClick;

	/**
	 * Constructor for button component.
	 *
	 * @param onClick OnClick delegate that will run when the button is pressed.
	 */
	public ButtonComponent(Runnable onClick) {
		this.onClick = onClick;
		this.setPadding(new Thickness(4f));
	}

	/**
	 * Sets the OnClick delegate of the button.
	 *
	 * @param onClick Delegate to set.
	 */
	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

	/**
	 * Draws the button to the screen.
	 *
	 * @param drawContext  The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 */
	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();
		float actualHeight = getActualSize().getHeight();

		Color color = GuiManager.foregroundColor.getValue();
		if (hovered) {
			color = color.add(55, 55, 55);
		}

		Render2D.drawOutlinedRoundedBox(drawContext, actualX, actualY, actualWidth, actualHeight, GuiManager.roundingRadius.getValue(),
				GuiManager.borderColor.getValue(), color);

		super.draw(drawContext, partialTicks);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				if (onClick != null)
					onClick.run();
				event.cancel();
			}
		}
	}
}

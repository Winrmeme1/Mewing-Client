/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.components.widgets;

import java.util.function.Consumer;

import net.mewing.gui.GuiManager;
import net.mewing.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MewingImageButtonWidget extends AbstractButton {
	private Consumer<MewingImageButtonWidget> pressAction;
	private final Identifier image;
	private int u = 0;
	private int v = 0;
	private boolean background = true;

	public MewingImageButtonWidget(int x, int y, int width, int height, Identifier image) {
		super(x, y, width, height, Component.empty());

		this.image = image;
	}

	public MewingImageButtonWidget(int x, int y, int u, int v, int width, int height, Identifier image) {
		super(x, y, width, height, Component.empty());

		this.image = image;
		this.u = u;
		this.v = v;
	}

	public MewingImageButtonWidget(int x, int y, int u, int v, int width, int height, Identifier image,
			boolean background) {
		super(x, y, width, height, Component.empty());

		this.image = image;
		this.u = u;
		this.v = v;
		this.background = background;
	}

	public void setPressAction(Consumer<MewingImageButtonWidget> pressAction) {
		this.pressAction = pressAction;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		if (pressAction != null) {
			pressAction.accept(this);
		}
	}

	@Override
	protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (background) {
			Render2D.setup();
			try {
				Render2D.drawOutlinedRoundedBox(context, getX(), getY(), width, height,
						GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue(),
						GuiManager.backgroundColor.getValue());
			} finally {
				Render2D.end();
			}
		}

		context.blit(RenderPipelines.GUI_TEXTURED, image, getX(), getY(), (float) u, (float) v, width, height, width, height);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}
}
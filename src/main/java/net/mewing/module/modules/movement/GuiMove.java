/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.movement;

import com.mojang.blaze3d.platform.InputConstants;
import net.mewing.Mewing;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.events.TickEvent.Pre;
import net.mewing.event.listeners.TickListener;
import net.mewing.module.AntiCheat;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;

public class GuiMove extends Module implements TickListener {
	public GuiMove() {
		super("GuiMove");
		setCategory(Category.of("Movement"));
		setDescription("Lets the player move while inside of menus using arrow keys..");

		setDetectable(AntiCheat.Karhu);
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

	}

	@Override
	public void onTick(Post event) {
		if (MC.screen != null && !(MC.screen instanceof ChatScreen)) {
			for (KeyMapping k : new KeyMapping[] { MC.options.keyUp, MC.options.keyDown, MC.options.keyLeft,
					MC.options.keyRight, MC.options.keyJump, MC.options.keySprint })
				k.setDown(isKeyPressed(InputConstants.getKey(k.saveString()).getValue()));

			float deltaX = 0;
			float deltaY = 0;

			if (isKeyPressed(264))
				deltaY += 10f;

			if (isKeyPressed(265))
				deltaY -= 10f;

			if (isKeyPressed(262))
				deltaX += 10f;

			if (isKeyPressed(263))
				deltaX -= 10f;

			if (deltaX != 0 || deltaY != 0)
				MC.player.turn(deltaX, deltaY);
		}
	}

}

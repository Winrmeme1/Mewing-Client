/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mewing.mixin;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.event.events.KeyDownEvent;
import net.mewing.event.events.KeyUpEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mewing.MewingClient.MC;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(at = {@At("HEAD")}, method = {"keyPress(JILnet/minecraft/client/input/KeyEvent;)V"}, cancellable = true)
    private void onKeyPress(long window, int action, KeyEvent keyEvent, CallbackInfo ci) {
        MewingClient mewing = Mewing.getInstance();

        int key = keyEvent.key();
        int scancode = keyEvent.scancode();
        int modifiers = keyEvent.modifiers();

        if (action == GLFW.GLFW_PRESS) {
            if (mewing != null && mewing.eventManager != null) {
                KeyDownEvent event = new KeyDownEvent(window, key, scancode, action, modifiers);

                Mewing.getInstance().eventManager.Fire(event);

                if (event.isCancelled()) {
                    ci.cancel();
                }
            }

            if (MC.screen == null && MC.getOverlay() == null && !mewing.guiManager.isClickGuiOpen()) {
                if (key == java.awt.event.KeyEvent.VK_PERIOD) {
                    MC.setScreen(new ChatScreen("", false));
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (mewing != null && mewing.eventManager != null) {
                KeyUpEvent event = new KeyUpEvent(window, key, scancode, action, modifiers);

                Mewing.getInstance().eventManager.Fire(event);

                if (event.isCancelled()) {
                    ci.cancel();
                }
            }
        }
    }
}

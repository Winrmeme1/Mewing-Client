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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.event.events.PlayerHealthEvent;
import net.mewing.event.events.SendMovementPacketEvent;
import net.mewing.gui.GuiManager;
import net.mewing.mixin.interfaces.ICamera;
import net.mewing.module.modules.combat.AntiKnockback;
import net.mewing.module.modules.movement.Fly;
import net.mewing.module.modules.movement.Freecam;
import net.mewing.module.modules.movement.HighJump;
import net.mewing.module.modules.movement.Noclip;
import net.mewing.module.modules.movement.Step;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin {
	@Shadow
	private ClientPacketListener connection;

	@Shadow
	protected abstract void sendPosition();

	@Inject(at = { @At("HEAD") }, method = "setShowDeathScreen(Z)V")
	private void onShowDeathScreen(boolean state, CallbackInfo ci) {
		GuiManager hudManager = Mewing.getInstance().guiManager;

		if (state && hudManager.isClickGuiOpen()) {
			hudManager.setClickGuiOpen(false);
		}
	}

	@Inject(at = { @At("HEAD") }, method = "isControlledCamera()Z", cancellable = true)
	private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
		Freecam freecam = Mewing.getInstance().moduleManager.freecam;
		if (freecam.state.getValue()) {
			cir.setReturnValue(true);
		}
	}

	@Override
	public void onIsSpectator(CallbackInfoReturnable<Boolean> cir) {
		if (Mewing.getInstance().moduleManager.freecam.state.getValue()) {
			cir.setReturnValue(true);
		}
	}

	@Override
	public void onSetHealth(float health, CallbackInfo ci) {
		PlayerHealthEvent event = new PlayerHealthEvent(null, health);
		Mewing.getInstance().eventManager.Fire(event);
	}

	@Override
	protected void onGetOffGroundSpeed(CallbackInfoReturnable<Float> cir) {
		if (Mewing.getInstance().moduleManager.fly.state.getValue()) {
			Fly fly = Mewing.getInstance().moduleManager.fly;
			cir.setReturnValue((float) fly.getSpeed());
		} else if (Mewing.getInstance().moduleManager.noclip.state.getValue()) {
			Noclip noclip = Mewing.getInstance().moduleManager.noclip;
			cir.setReturnValue(noclip.getSpeed());
		}
	}

	@Override
	public void onGetStepHeight(CallbackInfoReturnable<Float> cir) {
		Step stepHack = Mewing.getInstance().moduleManager.step;
		if (stepHack.state.getValue()) {
			cir.setReturnValue(cir.getReturnValue());
		}
	}

	@Override
	public void onGetJumpVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
		MewingClient mewing = Mewing.getInstance();
		HighJump higherJump = mewing.moduleManager.higherjump;
		if (higherJump.state.getValue()) {
			cir.setReturnValue(higherJump.getJumpHeightMultiplier());
		}
	}

	@Override
	public void onTickNewAi(CallbackInfo ci) {
		if (Mewing.getInstance().moduleManager.freecam.state.getValue())
			ci.cancel();
	}

	@Override
	public void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		if (Mewing.getInstance().moduleManager.freecam.state.getValue()) {
			float f = (float) cursorDeltaY * 0.15f;
			float g = (float) cursorDeltaX * 0.15f;

			Minecraft mc = Minecraft.getInstance();
			Camera camera = mc.gameRenderer.getMainCamera();
			ICamera icamera = (ICamera) camera;

			float newYaw = camera.yRot() + g;
			float newPitch = Math.min(90, Math.max(camera.xRot() + f, -90));

			icamera.setCameraRotation(newYaw, newPitch);
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
	private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
		SendMovementPacketEvent.Pre sendMovementPacketPreEvent = new SendMovementPacketEvent.Pre();
		Mewing.getInstance().eventManager.Fire(sendMovementPacketPreEvent);
	}

	@Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
	private void onSendMovementPacketsHead(CallbackInfo info) {
		SendMovementPacketEvent.Pre sendMovementPacketPreEvent = new SendMovementPacketEvent.Pre();
		Mewing.getInstance().eventManager.Fire(sendMovementPacketPreEvent);
		if (sendMovementPacketPreEvent.isCancelled())
			info.cancel();
	}

	@Inject(method = "sendPosition", at = @At("TAIL"), cancellable = true)
	private void onSendMovementPacketsTail(CallbackInfo info) {
		SendMovementPacketEvent.Post sendMovementPacketPostEvent = new SendMovementPacketEvent.Post();
		Mewing.getInstance().eventManager.Fire(sendMovementPacketPostEvent);
		if (sendMovementPacketPostEvent.isCancelled())
			info.cancel();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
		SendMovementPacketEvent.Post sendMovementPacketPostEvent = new SendMovementPacketEvent.Post();

		Mewing.getInstance().eventManager.Fire(sendMovementPacketPostEvent);
	}

	@Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
	private void onPushOutOfBlocks(double x, double z, CallbackInfo ci) {
		AntiKnockback antiKnockback = Mewing.getInstance().moduleManager.antiknockback;

		if (antiKnockback.state.getValue() && antiKnockback.getNoPushBlocks()) {
			ci.cancel();
		}
	}
}

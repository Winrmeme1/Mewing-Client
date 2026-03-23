package net.mewing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.module.modules.movement.Freecam;
import net.mewing.module.modules.render.NoRender;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	private boolean initialized;

	@Shadow
	private Entity entity;

	@Shadow
	private Level level;

	@Shadow
	private boolean detached;

	@Shadow
	private float partialTickTime;

	@Inject(at = {
			@At("HEAD") }, method = "setup(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;ZZF)V", cancellable = true)
	private void onCameraSetup(Level level, Entity focusedEntity, boolean thirdPerson, boolean inverseView,
			float tickDelta, CallbackInfo ci) {
		MewingClient mewing = Mewing.getInstance();
		
		// Only cancel setup if freecam is active AND the camera entity has been
		// initialized at least once (otherwise extractCamera will NPE on null entity)
		if (mewing != null && mewing.moduleManager.freecam.state.getValue() && this.entity != null) {
			initialized = true;
			this.level = level;
			partialTickTime = tickDelta;
			this.detached = thirdPerson;
			ci.cancel();
		}
	}

	@Inject(at = {
			@At("HEAD") }, method = "getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", cancellable = true)
	private void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
		MewingClient mewing = Mewing.getInstance();
		if (mewing == null)
			return;

		Freecam freecam = mewing.moduleManager.freecam;
		NoRender norender = mewing.moduleManager.norender;

		if (freecam.state.getValue() || (norender.state.getValue() && norender.getNoLiquidOverlay())) {
			cir.setReturnValue(FogType.NONE);
		}
	}
}

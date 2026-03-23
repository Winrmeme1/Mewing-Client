package net.mewing.mixin.sodium;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.module.modules.render.Fullbright;
import net.mewing.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
	@Unique
	private static final int FULL_LIGHT = 15 | 15 << 4 | 15 << 8;

	@Shadow
	protected BlockAndTintGetter level;
	@Shadow
	@Final
	private BlockPos.MutableBlockPos pos;

	@Unique
	private XRay xray;

	@Unique
	private Fullbright fb;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(CallbackInfo info) {
		MewingClient mewing = Mewing.getInstance();

		xray = mewing.moduleManager.xray;
		fb = mewing.moduleManager.fullbright;
	}

	@ModifyVariable(method = "compute", at = @At(value = "TAIL"), name = "bl")
	private int compute_modifyBL(int light) {
		if (xray.state.getValue()) {
			BlockState state = level.getBlockState(pos);
			if (xray.isXRayBlock(state.getBlock()))
				return FULL_LIGHT;
		}

		return light;
	}
}
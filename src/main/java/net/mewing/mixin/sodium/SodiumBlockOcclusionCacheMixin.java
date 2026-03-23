package net.mewing.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class SodiumBlockOcclusionCacheMixin {

	@Inject(at = { @At("RETURN") }, method = "shouldDrawSide", cancellable = true)
	private void onShouldDrawSide(BlockState state, BlockGetter view, BlockPos pos, Direction facing,
								  CallbackInfoReturnable<Boolean> cir) {
		MewingClient mewing = Mewing.getInstance();
		XRay xray = mewing.moduleManager.xray;
		if (xray.state.getValue()) {
			cir.setReturnValue(xray.isXRayBlock(state.getBlock()));
		}
	}
}
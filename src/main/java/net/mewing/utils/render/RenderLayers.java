package net.mewing.utils.render;

import java.util.function.Function;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class RenderLayers {

	// 3D Render Layers using the new RenderSetup/RenderPipeline system
	public static final RenderType QUADS = RenderType.create(
			"mewing_quads",
			RenderSetup.builder(MewingRenderPipelines.QUADS).createRenderSetup());

	public static final RenderType LINES = RenderType.create(
			"mewing_lines",
			RenderSetup.builder(MewingRenderPipelines.LINES).createRenderSetup());

	// 2D Render Layers - using our custom pipelines
	public static final RenderType QUADS_GUI = RenderType.create(
			"mewing_quads_gui",
			RenderSetup.builder(MewingRenderPipelines.QUADS_GUI).createRenderSetup());

	public static final RenderType LINES_GUI = RenderType.create(
			"mewing_lines_gui",
			RenderSetup.builder(MewingRenderPipelines.LINES_GUI).createRenderSetup());

	public static final RenderType TRIS_GUI = RenderType.create(
			"mewing_tris_gui",
			RenderSetup.builder(MewingRenderPipelines.TRIS_GUI).createRenderSetup());

	// Textured GUI layers with texture binding
	public static final Function<Identifier, RenderType> TEXTURES_QUADS_GUI = Util
			.memoize((Function<Identifier, RenderType>) (texture -> RenderType.create(
					"mewing_textured_quads_gui",
					RenderSetup.builder(MewingRenderPipelines.TEXTURED_QUADS_GUI)
							.withTexture("Sampler0", texture)
							.createRenderSetup())));

	public static final Function<Identifier, RenderType> TEXTURES_TRIS_GUI = Util
			.memoize((Function<Identifier, RenderType>) (texture -> RenderType.create(
					"mewing_textured_tris_gui",
					RenderSetup.builder(MewingRenderPipelines.TEXTURED_TRIS_GUI)
							.withTexture("Sampler0", texture)
							.createRenderSetup())));

}

/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.render;

import net.mewing.Mewing;
import net.mewing.event.events.Render3DEvent;
import net.mewing.event.listeners.Render3DListener;
import net.mewing.gui.colors.Color;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.ColorSetting;
import net.mewing.settings.types.FloatSetting;
import net.mewing.utils.ModuleUtils;
import net.mewing.utils.render.Render3D;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.phys.AABB;

public class ChestESP extends Module implements Render3DListener {

	private final ColorSetting color = ColorSetting.builder().id("chestesp_color").displayName("Color")
			.description("Color").defaultValue(new Color(0, 1f, 1f, 0.3f)).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("chestesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public ChestESP() {
		super("ChestESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see Chests with an ESP.");

		addSettings(color, lineThickness);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		ModuleUtils.getTileEntities().forEach(blockEntity -> {
			if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity
					|| blockEntity instanceof BarrelBlockEntity) {
				AABB box = new AABB(blockEntity.getBlockPos());
				Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), box, color.getValue(),
						lineThickness.getValue().floatValue());
			}
		});
	}
}

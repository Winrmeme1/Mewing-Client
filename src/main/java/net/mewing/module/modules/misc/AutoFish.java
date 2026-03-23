/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.misc;

import net.mewing.Mewing;
import net.mewing.event.events.ReceivePacketEvent;
import net.mewing.event.listeners.ReceivePacketListener;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;
import net.mewing.utils.FindItemResult;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class AutoFish extends Module implements ReceivePacketListener {
	private final BooleanSetting autoSwitch = BooleanSetting.builder().id("autofish_autoswitch").displayName("Auto Switch")
			.description("Automatically switch to fishing rod before casting.").defaultValue(true).build();

	private final BooleanSetting autoToggle = BooleanSetting.builder().id("autofish_autotoggle").displayName("Auto Toggle")
			.description("Automatically toggles off if no fishing rod is found in the hotbar.").defaultValue(false)
			.build();

	public AutoFish() {
		super("AutoFish");

		setCategory(Category.of("Misc"));
		setDescription("Automatically fishes for you.");

		addSetting(autoSwitch);
		addSetting(autoToggle);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);

		FindItemResult rod = find(Items.FISHING_ROD);

		if (autoSwitch.getValue()) {
			if (rod.found() && rod.isHotbar()) {
				swap(rod.slot(), false);
			} else {
				if (!autoToggle.getValue())
					return;

				toggle();
			}
		}
	}

	@Override
	public void onToggle() {

	}

	private void castRod(int count) {
		FindItemResult rod = find(Items.FISHING_ROD);

		if (autoSwitch.getValue()) {
			if (rod.found() && rod.isHotbar()) {
				swap(rod.slot(), false);
			} else {
				if (!autoToggle.getValue())
					return;

				toggle();
			}
		}

		for (int i = 0; i < count; i++) {
			MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
		}
	}

	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		Packet<?> packet = readPacketEvent.GetPacket();

		if (packet instanceof ClientboundSoundPacket soundPacket) {
            if (soundPacket.getSound().value().equals(SoundEvents.FISHING_BOBBER_SPLASH)) {
				castRod(2);
			}
		}
	}
}

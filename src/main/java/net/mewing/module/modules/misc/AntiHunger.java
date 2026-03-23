/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.misc;

import net.mewing.Mewing;
import net.mewing.event.events.SendMovementPacketEvent;
import net.mewing.event.events.SendPacketEvent;
import net.mewing.event.listeners.SendMovementPacketListener;
import net.mewing.event.listeners.SendPacketListener;
import net.mewing.mixin.interfaces.IServerboundMovePlayerPacket;
import net.mewing.module.AntiCheat;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class AntiHunger extends Module implements SendPacketListener, SendMovementPacketListener {
	private boolean lastOnGround, ignorePacket;

	private final BooleanSetting sprint = BooleanSetting.builder().id("antihunger_sprint").displayName("Sprint")
			.description("Change sprint packets.").defaultValue(true).build();

	private final BooleanSetting onGround = BooleanSetting.builder().id("antihunger_onground").displayName("On Ground")
			.description("Fakes onGround.").defaultValue(true).build();

	public AntiHunger() {
		super("AntiHunger");
		setCategory(Category.of("Misc"));
		setDescription("Reduces the amount of hunger that is consumed.");

		addSetting(sprint);
		addSetting(onGround);

		setDetectable(
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Negativity,
				AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
		Mewing.getInstance().eventManager.RemoveListener(SendMovementPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(SendPacketListener.class, this);
		Mewing.getInstance().eventManager.AddListener(SendMovementPacketListener.class, this);

		lastOnGround = MC.player.onGround();
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		if (ignorePacket && event.GetPacket() instanceof ServerboundMovePlayerPacket) {
			ignorePacket = false;
			return;
		}

		if (MC.player != null) {
			if (MC.player.isPassenger() || MC.player.isInWater() || MC.player.isUnderWater())
				return;

			if (event.GetPacket() instanceof ServerboundMovePlayerPacket packet && onGround.getValue() && MC.player.onGround()
					&& MC.player.fallDistance <= 0.0 && !MC.gameMode.isDestroying()) {
				((IServerboundMovePlayerPacket) packet).setOnGround(false);
			}
		}

		if (event.GetPacket() instanceof ServerboundPlayerCommandPacket packet && sprint.getValue()) {
			if (packet.getAction() == ServerboundPlayerCommandPacket.Action.START_SPRINTING)
				event.cancel();
		}
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Pre event) {
		if (MC.player.onGround() && !lastOnGround && onGround.getValue()) {
			ignorePacket = true;
		}

		lastOnGround = MC.player.onGround();
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Post event) {

	}
}

/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.combat;

import io.netty.buffer.Unpooled;
import net.mewing.Mewing;
import net.mewing.event.events.SendPacketEvent;
import net.mewing.event.listeners.SendPacketListener;
import net.mewing.mixin.interfaces.IServerboundInteractPacket;
import net.mewing.module.AntiCheat;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class Criticals extends Module implements SendPacketListener {

	public enum InteractType {
		INTERACT, ATTACK, INTERACT_AT
	}

	private final BooleanSetting legit = BooleanSetting.builder().id("criticals_legit").displayName("Legit")
			.description("Whether or not we will use the 'legit' mode.").defaultValue(false).build();

	public Criticals() {
		super("Criticals");

		setCategory(Category.of("Combat"));
		setDescription("Makes all attacks into critical strikes.");

		addSetting(legit);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(SendPacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if (packet instanceof ServerboundInteractPacket playerInteractPacket) {
            IServerboundInteractPacket packetAccessor = (IServerboundInteractPacket) playerInteractPacket;

			FriendlyByteBuf packetBuf = new FriendlyByteBuf(Unpooled.buffer());
			packetAccessor.invokeWrite(packetBuf);
			packetBuf.readVarInt();
			InteractType type = packetBuf.readEnum(InteractType.class);

			if (type == InteractType.ATTACK) {
				Minecraft mc = Minecraft.getInstance();
				LocalPlayer player = mc.player;
				if (player.onGround() && !player.isInLava() && !player.isUnderWater()) {
					if (legit.getValue()) {
						player.jumpFromGround();
					} else {
						ClientPacketListener networkHandler = mc.getConnection();
						networkHandler.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
								mc.player.getY() + 0.03125D, mc.player.getZ(), false, false));
						networkHandler.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
								mc.player.getY() + 0.0625D, mc.player.getZ(), false, false));
						networkHandler.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
								mc.player.getY(), mc.player.getZ(), false, false));
					}
				}
			}
		}
	}
}

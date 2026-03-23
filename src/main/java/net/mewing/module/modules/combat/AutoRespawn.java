/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.combat;

import net.mewing.Mewing;
import net.mewing.event.events.PlayerDeathEvent;
import net.mewing.event.events.TickEvent;
import net.mewing.event.listeners.PlayerDeathListener;
import net.mewing.event.listeners.TickListener;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.FloatSetting;

public class AutoRespawn extends Module implements PlayerDeathListener, TickListener {

	private final FloatSetting respawnDelay = FloatSetting.builder().id("autorespawn_delay").displayName("Delay")
			.description("The delay between dying and automatically respawning.").defaultValue(0.0f).minValue(0.0f)
			.maxValue(100.0f).step(1.0f).build();

    private int tick;

	public AutoRespawn() {
		super("AutoRespawn");

		setCategory(Category.of("Combat"));
		setDescription("Automatically respawns when you die.");

		addSetting(respawnDelay);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Mewing.getInstance().eventManager.RemoveListener(PlayerDeathListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(PlayerDeathListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent readPacketEvent) {
		if (respawnDelay.getValue() == 0.0f) {
			respawn();
		} else {
			tick = 0;
			Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
		}
		readPacketEvent.cancel();
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		if (tick < respawnDelay.getValue()) {
			tick++;
		} else {
			respawn();
		}
	}

	private void respawn() {
		MC.player.respawn();
		MC.setScreen(null);
		Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}
}

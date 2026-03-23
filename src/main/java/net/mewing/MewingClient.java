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

/**
 * A class to represent Mewing Client and all of its functions.
 */
package net.mewing;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.mewing.api.IAddon;
import net.mewing.gui.GuiManager;
import net.mewing.gui.font.FontManager;
import net.mewing.managers.CombatManager;
import net.mewing.managers.CommandManager;
import net.mewing.managers.EntityManager;
import net.mewing.managers.EventManager;
import net.mewing.managers.ModuleManager;
import net.mewing.managers.SettingManager;
import net.mewing.managers.altmanager.AltManager;
import net.mewing.managers.macros.MacroManager;
import net.mewing.managers.proxymanager.ProxyManager;
import net.mewing.managers.rotation.RotationManager;
import net.mewing.mixin.interfaces.IMinecraft;
import net.mewing.module.Module;
import net.mewing.settings.friends.FriendsList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class MewingClient {
	public static final String MEWING_VERSION = "1.4.5";
	public static final KeyMapping.Category MEWING_CATEGORY =
			new KeyMapping.Category(Identifier.fromNamespaceAndPath("mewing", "category"));

	public static Minecraft MC;
	public static IMinecraft IMC;

	// Systems
	public RotationManager rotationManager;
	public ModuleManager moduleManager;
	public CommandManager commandManager;
	public AltManager altManager;
	public ProxyManager proxyManager;
	public GuiManager guiManager;
	public FontManager fontManager;
	public CombatManager combatManager;
	public SettingManager settingManager;
	public FriendsList friendsList;
	public EventManager eventManager;
	public MacroManager macroManager;
	public EntityManager entityManager;

	public static List<IAddon> addons = new ArrayList<>();
	public static Logger LOGGER;

	/**
	 * Initializes Mewing Client and creates sub-systems.
	 */
	public void Initialize() {
		// Gets instance of Minecraft
		MC = Minecraft.getInstance();
		IMC = (IMinecraft) MC;
		LOGGER = LogUtils.getLogger();
	}

	/**
	 * Initializes systems and loads any assets.
	 */
	public void loadAssets() {
		LOGGER.info("[Mewing] Starting Client");
		eventManager = new EventManager();

		// Register any addons.
		LogUtils.getLogger().info("[Mewing] Starting addon initialization");
		for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("mewing",
				IAddon.class)) {
			IAddon addon = entrypoint.getEntrypoint();

			try {
				LOGGER.info("[Mewing] Initializing addon: " + addon.getName());
				addon.onInitialize();
				LOGGER.info("[Mewing] Addon initialized: " + addon.getName());
			} catch (Throwable e) {
				LOGGER.error("Error initializing addon: " + addon.getName(), e.getMessage());
			}

			addons.add(addon);
		}

		LOGGER.info("[Mewing] Reading Settings");
		settingManager = new SettingManager();

		LOGGER.info("[Mewing] Reading Friends List");
		friendsList = new FriendsList();

		LOGGER.info("[Mewing] Initializing Rotation Manager");
		rotationManager = new RotationManager();

		LOGGER.info("[Mewing] Initializing Modules");
		moduleManager = new ModuleManager(addons);

		LOGGER.info("[Mewing] Initializing Commands");
		commandManager = new CommandManager(addons);

		LOGGER.info("[Mewing] Initializing Font Manager");
		fontManager = new FontManager();
		fontManager.Initialize();

		LOGGER.info("[Mewing] Initializing Combat Manager");
		combatManager = new CombatManager();

		LOGGER.info("[Mewing] Initializing Entity Manager");
		entityManager = new EntityManager();

		LOGGER.info("[Mewing] Initializing Macro Manager");
		macroManager = new MacroManager();

		LOGGER.info("[Mewing] Initializing GUI");
		guiManager = new GuiManager();
		guiManager.Initialize();

		LOGGER.info("[Mewing] Initializing Alt Manager");
		altManager = new AltManager();

		LOGGER.info("[Mewing] Initializing Proxy Manager");
		proxyManager = new ProxyManager();

		LOGGER.info("[Mewing] Registering Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				endClient();
			} catch (Exception e) {
				LOGGER.error("[Mewing] Error during shutdown: ", e);
			}
		}));

		LOGGER.info("[Mewing] Loading Settings");
		SettingManager.loadGlobalSettings();
		SettingManager.loadSettings();

		LOGGER.info("[Mewing] Mewing-chan initialized and ready to play!");
	}

	/**
	 * Called when the client is shutting down. Saves persistent data.
	 */
	public void endClient() {
		LOGGER.info("[Mewing] Shutting down");
		try {
			SettingManager.saveSettings();
			altManager.saveAlts();
			friendsList.save();
			macroManager.save();
			moduleManager.modules.forEach(Module::onDisable);
		} catch (Exception e) {
			LOGGER.error("[Mewing] Error saving data", e);
		}
	}
}

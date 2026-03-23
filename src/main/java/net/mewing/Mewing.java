/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing;

import net.fabricmc.api.ModInitializer;

/**
 * Initializes and provides access to the Mewing Client singleton.
 */
public class Mewing implements ModInitializer {
	private static MewingClient INSTANCE;

	@Override
	public void onInitialize() {
		INSTANCE = new MewingClient();
		INSTANCE.Initialize();
	}

	/**
	 * @return Singleton instance of MewingClient.
	 */
	public static MewingClient getInstance() {
		return INSTANCE;
	}
}

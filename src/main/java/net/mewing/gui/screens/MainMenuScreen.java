/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.screens;

import static net.mewing.MewingClient.MC;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.RealmsMainScreen;
import net.mewing.MewingClient;
import net.mewing.api.IAddon;
import net.mewing.gui.components.widgets.MewingButtonWidget;
import net.mewing.gui.screens.addons.AddonScreen;
import net.mewing.utils.render.TextureBank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class MainMenuScreen extends Screen {
	protected static final CubeMap MEWING_PANORAMA_RENDERER = new CubeMap(TextureBank.mainmenu_panorama);
	protected static final PanoramaRenderer MEWING_ROTATING_PANORAMA_RENDERER = new PanoramaRenderer(
			MEWING_PANORAMA_RENDERER);
	private static boolean panoramaRegistered = false;

	public static void registerPanoramaTextures(net.minecraft.client.renderer.texture.TextureManager textureManager) {
		if (!panoramaRegistered) {
			MEWING_PANORAMA_RENDERER.registerTextures(textureManager);
			panoramaRegistered = true;
		}
	}

	final int LOGO_HEIGHT = Math.max(58, height / 12);
	final int BUTTON_WIDTH = Math.max(150, width / 6);
	final int BUTTON_HEIGHT = Math.max(25, height / 20);
	final int SPACING = Math.max(5, height / 100);

	int smallScreenHeightOffset = 0;

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	private String fetchedVersion = null;

	public MainMenuScreen() {
		super(Component.nullToEmpty("Mewing Client Main Menu"));

		executor.execute(new Runnable() {
			@Override
			public void run() {
				fetchLatestVersion();
			}
		});
	}

	private static URI createURI(String url) {
		try {
			URI uri = new URI(url);
			return uri;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void fetchLatestVersion() {
		try {
			HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).followRedirects(Redirect.NORMAL)
					.build();

			HttpRequest request = HttpRequest
					.newBuilder(
							createURI("https://api.github.com/repos/coltonk9043/Mewing-MC-Hacked-Client/releases/latest"))
					.header("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
					.header("Accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded")
					.GET().build();

			HttpResponse<String> response;
			response = client.send(request, BodyHandlers.ofString());
			String responseString = response.body();

			int status = response.statusCode();
			if (status != HttpURLConnection.HTTP_OK) {
				throw new IllegalArgumentException("Device token could not be fetched. Invalid status code " + status);
			}

			JsonObject json = new Gson().fromJson(responseString, JsonObject.class);
			String tagName = json.get("tag_name").getAsString();
			if (tagName != null)
				fetchedVersion = tagName;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		super.init();

		if (height <= 650)
			smallScreenHeightOffset = 40;
		else
			smallScreenHeightOffset = 0;

		int columns = 2;
		int rows = 3;
		float widgetHeight = ((BUTTON_HEIGHT + SPACING) * rows);
		int startX = (int) ((width - (BUTTON_WIDTH * columns + SPACING * (columns - 1))) / 2.0f);
		int startY = (int) ((height - widgetHeight) / 2) + smallScreenHeightOffset;

		MewingButtonWidget singleplayerButton = new MewingButtonWidget(startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT,
				Component.nullToEmpty("Singleplayer"));
		singleplayerButton.setPressAction(b -> minecraft.setScreen(new SelectWorldScreen(this)));
		addRenderableWidget(singleplayerButton);

		MewingButtonWidget multiplayerButton = new MewingButtonWidget(startX + BUTTON_WIDTH + SPACING, startY, BUTTON_WIDTH,
				BUTTON_HEIGHT, Component.nullToEmpty("Multiplayer"));
		multiplayerButton.setPressAction(b -> minecraft.setScreen(new JoinMultiplayerScreen(this)));
		addRenderableWidget(multiplayerButton);

		MewingButtonWidget realmsButton = new MewingButtonWidget(startX, startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH,
				BUTTON_HEIGHT, Component.nullToEmpty("Realms"));
		realmsButton.setPressAction(b -> minecraft.setScreen(new RealmsMainScreen(this)));
		addRenderableWidget(realmsButton);

		MewingButtonWidget settingsButton = new MewingButtonWidget(startX + BUTTON_WIDTH + SPACING,
				startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Settings"));
		settingsButton.setPressAction(b -> minecraft.setScreen(new OptionsScreen(this, MC.options)));
		addRenderableWidget(settingsButton);

		MewingButtonWidget addonsButton = new MewingButtonWidget(startX, startY + ((BUTTON_HEIGHT + SPACING) * 2),
				BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Addons"));
		addonsButton.setPressAction(b -> minecraft.setScreen(new AddonScreen(this)));
		addRenderableWidget(addonsButton);

		MewingButtonWidget quitButton = new MewingButtonWidget(startX + BUTTON_WIDTH + SPACING,
				startY + ((BUTTON_HEIGHT + SPACING) * 2), BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Quit"));
		quitButton.setPressAction(b -> minecraft.destroy());
		addRenderableWidget(quitButton);
	}

	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		super.render(drawContext, mouseX, mouseY, delta);

		float widgetHeight = (BUTTON_HEIGHT + SPACING) * 5;
		int startX = (width - BUTTON_WIDTH) / 2;
		int startY = (int) ((height - widgetHeight) / 2 + smallScreenHeightOffset);

		int logoWidth = (int) (LOGO_HEIGHT * (1568.0 / 588.0));
		int logoX = (width - logoWidth) / 2;
		int logoY = startY - LOGO_HEIGHT - 10;
		drawContext.blit(RenderPipelines.GUI_TEXTURED, TextureBank.mainmenu_logo, logoX, logoY, 0, 0, logoWidth,
				LOGO_HEIGHT, 1568, 588, 1568, 588);

		drawContext.drawString(font, "Mewing " + MewingClient.MEWING_VERSION, 2, height - 10, 0xFF00BFFF);

		if (fetchedVersion != null && !fetchedVersion.equals(MewingClient.MEWING_VERSION)) {
			drawContext.drawString(font, "New version available: " + fetchedVersion, 2, height - 20, 0xFF00BFFF);
		}

		if (MewingClient.addons.isEmpty()) {
			String noAddonsText = "No addons loaded";
			int textWidth = font.width(noAddonsText);
			drawContext.drawString(font, noAddonsText, width - textWidth - 15, 10, 0xFFFFFFFF);
		} else {
			int yOffset = 10;
			for (IAddon addon : MewingClient.addons) {
				String addonName = addon.getName();
				String byText = " by ";
				String author = addon.getAuthor();

				int addonNameWidth = font.width(addonName);
				int byTextWidth = font.width(byText);
				int authorWidth = font.width(author);

				drawContext.drawString(font, addonName,
						width - addonNameWidth - byTextWidth - authorWidth - 20, yOffset, 0xFF00BFFF);

				drawContext.drawString(font, byText, width - byTextWidth - authorWidth - 15, yOffset,
						0xFFFFFFFF);

				drawContext.drawString(font, author, width - authorWidth - 10, yOffset, 0xFF87CEFA);

				yOffset += 10;
			}
		}
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
		renderPanorama(context, delta);
	}

	@Override
	protected void renderPanorama(GuiGraphics context, float delta) {
		if (!panoramaRegistered)
			return;

		try {
			MEWING_ROTATING_PANORAMA_RENDERER.render(context, width, height, true);
		} catch (IllegalStateException e) {
		}
	}
}

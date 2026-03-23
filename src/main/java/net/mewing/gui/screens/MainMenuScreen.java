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
import com.mojang.blaze3d.systems.RenderSystem;
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

	// Logo texture native size
	private static final int LOGO_TEX_W = 1568;
	private static final int LOGO_TEX_H = 588;

	public static void registerPanoramaTextures(net.minecraft.client.renderer.texture.TextureManager textureManager) {
		if (!panoramaRegistered) {
			MEWING_PANORAMA_RENDERER.registerTextures(textureManager);
			panoramaRegistered = true;
		}
	}

	// Button layout constants — computed in init() where width/height are valid
	private int BUTTON_WIDTH;
	private int BUTTON_HEIGHT;
	private int SPACING;

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
			return new URI(url);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void fetchLatestVersion() {
		try {
			HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).followRedirects(Redirect.NORMAL).build();
			HttpRequest request = HttpRequest
					.newBuilder(createURI("https://api.github.com/repos/coltonk9043/Mewing-MC-Hacked-Client/releases/latest"))
					.header("User-Agent", "Mozilla/5.0")
					.header("Accept", "application/json")
					.GET().build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			int status = response.statusCode();
			if (status != HttpURLConnection.HTTP_OK) return;
			JsonObject json = new Gson().fromJson(response.body(), JsonObject.class);
			String tagName = json.get("tag_name").getAsString();
			if (tagName != null) fetchedVersion = tagName;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		super.init();

		BUTTON_WIDTH  = Math.max(160, width / 5);
		BUTTON_HEIGHT = Math.max(24, height / 22);
		SPACING       = Math.max(6, height / 90);

		smallScreenHeightOffset = (height <= 650) ? 35 : 0;

		// --- 2-column, 3-row grid centred on screen ---
		int totalW  = BUTTON_WIDTH * 2 + SPACING;
		int totalH  = BUTTON_HEIGHT * 3 + SPACING * 2;
		int startX  = (width  - totalW) / 2;
		int startY  = (height - totalH) / 2 + smallScreenHeightOffset;

		// Row 0
		MewingButtonWidget singleplayerButton = new MewingButtonWidget(startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT,
				Component.nullToEmpty("Singleplayer"));
		singleplayerButton.setPressAction(b -> minecraft.setScreen(new SelectWorldScreen(this)));
		addRenderableWidget(singleplayerButton);

		MewingButtonWidget multiplayerButton = new MewingButtonWidget(startX + BUTTON_WIDTH + SPACING, startY, BUTTON_WIDTH,
				BUTTON_HEIGHT, Component.nullToEmpty("Multiplayer"));
		multiplayerButton.setPressAction(b -> minecraft.setScreen(new JoinMultiplayerScreen(this)));
		addRenderableWidget(multiplayerButton);

		// Row 1
		MewingButtonWidget realmsButton = new MewingButtonWidget(startX, startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH,
				BUTTON_HEIGHT, Component.nullToEmpty("Realms"));
		realmsButton.setPressAction(b -> minecraft.setScreen(new RealmsMainScreen(this)));
		addRenderableWidget(realmsButton);

		MewingButtonWidget settingsButton = new MewingButtonWidget(startX + BUTTON_WIDTH + SPACING,
				startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Settings"));
		settingsButton.setPressAction(b -> minecraft.setScreen(new OptionsScreen(this, MC.options)));
		addRenderableWidget(settingsButton);

		// Row 2
		MewingButtonWidget addonsButton = new MewingButtonWidget(startX, startY + (BUTTON_HEIGHT + SPACING) * 2,
				BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Addons"));
		addonsButton.setPressAction(b -> minecraft.setScreen(new AddonScreen(this)));
		addRenderableWidget(addonsButton);

		MewingButtonWidget quitButton = new MewingButtonWidget(startX + BUTTON_WIDTH + SPACING,
				startY + (BUTTON_HEIGHT + SPACING) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Quit"));
		quitButton.setPressAction(b -> minecraft.destroy());
		addRenderableWidget(quitButton);
	}

	@Override
	public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);

		// --- Dark blue gradient overlay so buttons are readable over panorama ---
		int overlayTop    = 0x88000A1A;  // dark navy, semi-transparent
		int overlayBottom = 0xCC000510;  // slightly darker at bottom
		ctx.fillGradient(0, 0, width, height, overlayTop, overlayBottom);

		// --- Logo ---
		int totalH  = BUTTON_HEIGHT * 3 + SPACING * 2;
		int startY  = (height - totalH) / 2 + smallScreenHeightOffset;

		// Scale logo to fit nicely — max 55% of screen width, max 80px tall
		int maxLogoW = (int) (width * 0.55f);
		int maxLogoH = 80;
		float ratio  = (float) LOGO_TEX_W / LOGO_TEX_H;
		int logoH    = Math.min(maxLogoH, (int) (maxLogoW / ratio));
		int logoW    = (int) (logoH * ratio);
		int logoX    = (width - logoW) / 2;
		int logoY    = startY - logoH - SPACING * 2;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		ctx.blit(RenderPipelines.GUI_TEXTURED,
				TextureBank.mainmenu_logo,
				logoX, logoY, logoW, logoH,
				0, 0, LOGO_TEX_W, LOGO_TEX_H,
				LOGO_TEX_W, LOGO_TEX_H);
		RenderSystem.disableBlend();

		// --- Version label bottom-left ---
		String versionText = "Mewing " + MewingClient.MEWING_VERSION;
		ctx.drawString(font, versionText, 4, height - 10, 0xFF00BFFF, true);

		if (fetchedVersion != null && !fetchedVersion.equals(MewingClient.MEWING_VERSION)) {
			ctx.drawString(font, "Update available: " + fetchedVersion, 4, height - 20, 0xFF87CEFA, true);
		}

		// --- Addon list top-right ---
		if (!MewingClient.addons.isEmpty()) {
			int yOffset = 10;
			for (IAddon addon : MewingClient.addons) {
				String line = addon.getName() + " by " + addon.getAuthor();
				int tw = font.width(line);
				ctx.drawString(font, line, width - tw - 10, yOffset, 0xFF00BFFF, true);
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
		if (!panoramaRegistered) return;
		try {
			MEWING_ROTATING_PANORAMA_RENDERER.render(context, width, height, true);
		} catch (IllegalStateException e) {
			// panorama not ready yet
		}
	}
}
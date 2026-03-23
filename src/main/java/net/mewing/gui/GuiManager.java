/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import org.joml.Matrix3x2fStack;
import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.event.events.KeyDownEvent;
import net.mewing.event.events.Render2DEvent;
import net.mewing.event.events.TickEvent;
import net.mewing.event.listeners.KeyDownListener;
import net.mewing.event.listeners.Render2DListener;
import net.mewing.event.listeners.TickListener;
import net.mewing.gui.GridDefinition.RelativeUnit;
import net.mewing.gui.colors.Color;
import net.mewing.gui.colors.RainbowColor;
import net.mewing.gui.colors.RandomColor;
import net.mewing.gui.components.GridComponent;
import net.mewing.gui.components.ImageComponent;
import net.mewing.gui.components.ModuleComponent;
import net.mewing.gui.components.SeparatorComponent;
import net.mewing.gui.components.StackPanelComponent;
import net.mewing.gui.components.StringComponent;
import net.mewing.gui.navigation.HudWindow;
import net.mewing.gui.navigation.NavigationBar;
import net.mewing.gui.navigation.Page;
import net.mewing.gui.navigation.Window;
import net.mewing.gui.navigation.huds.ArmorHud;
import net.mewing.gui.navigation.huds.CoordsHud;
import net.mewing.gui.navigation.huds.DayHud;
import net.mewing.gui.navigation.huds.FPSHud;
import net.mewing.gui.navigation.huds.ModuleArrayListHud;
import net.mewing.gui.navigation.huds.ModuleSelectorHud;
import net.mewing.gui.navigation.huds.NetherCoordsHud;
import net.mewing.gui.navigation.huds.PingHud;
import net.mewing.gui.navigation.huds.RadarHud;
import net.mewing.gui.navigation.huds.SpeedHud;
import net.mewing.gui.navigation.huds.TimeHud;
import net.mewing.gui.navigation.huds.WatermarkHud;
import net.mewing.gui.navigation.windows.AntiCheatWindow;
import net.mewing.gui.navigation.windows.AuthCrackerWindow;
import net.mewing.gui.navigation.windows.GoToWindow;
import net.mewing.gui.navigation.windows.HudOptionsWindow;
import net.mewing.gui.navigation.windows.MacroWindow;
import net.mewing.gui.navigation.windows.SettingsWindow;
import net.mewing.gui.navigation.windows.ToggleHudsTab;
import net.mewing.managers.SettingManager;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;
import net.mewing.settings.types.ColorSetting;
import net.mewing.settings.types.FloatSetting;
import net.mewing.settings.types.KeybindSetting;
import net.mewing.utils.input.CursorStyle;
import net.mewing.utils.input.Input;
import net.mewing.utils.render.Render2D;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class GuiManager implements KeyDownListener, TickListener, Render2DListener {
	private static final Minecraft MC = Minecraft.getInstance();
	private static CursorStyle currentCursor = CursorStyle.Default;
	private static String tooltip = null;

	public KeybindSetting clickGuiButton = KeybindSetting.builder().id("key.clickgui").displayName("ClickGUI Key")
			.defaultValue(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_RIGHT_SHIFT)).build();

	private final KeyMapping esc = new KeyMapping("key.esc", GLFW.GLFW_KEY_ESCAPE, MewingClient.MEWING_CATEGORY);

	private boolean clickGuiOpen = false;
	private static boolean isKeyboardInputActive = false;
	private final HashMap<Object, Window> pinnedHuds = new HashMap<Object, Window>();

	// Navigation Bar and Pages
	public NavigationBar clickGuiNavBar;
	public Page modulesPane = new Page("Modules");
	public Page toolsPane = new Page("Tools");
	public Page hudPane = new Page("Hud");

	// Global HUD Settings
	public static BooleanSetting enableCustomTitle = BooleanSetting.builder().id("enable_custom_title")
			.displayName("Enable Custom Title Screen").defaultValue(true).build();

	public static BooleanSetting enableTooltips = BooleanSetting.builder().id("enable_tooltips")
			.displayName("Enable Tooltips").defaultValue(true).build();

	public static ColorSetting foregroundColor = ColorSetting.builder().id("hud_foreground_color")
			.displayName("GUI Foreground Color").description("Color of the foreground.")
			.defaultValue(new Color(0, 191, 255)).build();

	public static ColorSetting borderColor = ColorSetting.builder().id("hud_border_color")
			.displayName("GUI Border Color").description("Color of the borders.").defaultValue(new Color(0, 80, 160))
			.build();

	public static ColorSetting backgroundColor = ColorSetting.builder().id("hud_background_color")
			.displayName("GUI Background Color").description("Color of the background.")
			.defaultValue(new Color(0, 30, 80, 50)).build();

	public static FloatSetting roundingRadius = FloatSetting.builder().id("hud_rounding_radius")
			.displayName("Corner Rounding").description("The radius of the rounding on hud.").defaultValue(6f)
			.minValue(0f).maxValue(10f).step(1f).build();

	public static FloatSetting dragSmoothening = FloatSetting.builder().id("gui_drag_smoothening")
			.displayName("Drag Smooth Speed").description("The value for the dragging smoothening").defaultValue(1.0f)
			.minValue(0.1f).maxValue(2.0f).step(0.1f).build();

	public static RainbowColor rainbowColor = new RainbowColor();
	public static RandomColor randomColor = new RandomColor();

	public ModuleSelectorHud moduleSelector;
	public ArmorHud armorHud;
	public RadarHud radarHud;
	public TimeHud timeHud;
	public DayHud dayHud;
	public ModuleArrayListHud moduleArrayListHud;
	public WatermarkHud watermarkHud;
	public CoordsHud coordsHud;
	public NetherCoordsHud netherCoordsHud;
	public FPSHud fpsHud;
	public PingHud pingHud;
	public SpeedHud speedHud;

	public GuiManager() {
		clickGuiNavBar = new NavigationBar();

		SettingManager.registerGlobalSetting(borderColor);
		SettingManager.registerGlobalSetting(backgroundColor);
		SettingManager.registerGlobalSetting(foregroundColor);
		SettingManager.registerGlobalSetting(roundingRadius);

		SettingManager.registerSetting(clickGuiButton);
		Mewing.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
		Mewing.getInstance().eventManager.AddListener(Render2DListener.class, this);
	}

	public void Initialize() {
		System.out.println("Initializing");
		toolsPane.addWindow(new AuthCrackerWindow());
		toolsPane.addWindow(new GoToWindow());
		toolsPane.addWindow(new MacroWindow());

		moduleSelector = new ModuleSelectorHud();
		armorHud = new ArmorHud(0, 0);
		radarHud = new RadarHud(0, 0);
		timeHud = new TimeHud(0, 0);
		dayHud = new DayHud(0, 0);
		moduleArrayListHud = new ModuleArrayListHud(0, 0);
		watermarkHud = new WatermarkHud(0, 0);
		coordsHud = new CoordsHud(0, 0);
		netherCoordsHud = new NetherCoordsHud(0, 0);
		fpsHud = new FPSHud(0, 0);
		pingHud = new PingHud(0, 0);
		speedHud = new SpeedHud(0, 0);

		ArrayList<HudWindow> huds = Lists.newArrayList(moduleSelector, armorHud, radarHud, timeHud, dayHud,
				moduleArrayListHud, watermarkHud, coordsHud, netherCoordsHud, fpsHud, pingHud, speedHud);
		hudPane.addWindow(new HudOptionsWindow());
		hudPane.addWindow(new ToggleHudsTab(huds));
		Map<String, Category> categories = Category.getAllCategories();
		float xOffset = 50;

		for (Category category : categories.values()) {
			Window tab = new Window(category.getName(), xOffset, 75.0f);
			StackPanelComponent stackPanel = new StackPanelComponent();
			stackPanel.setSpacing(6f);

			GridComponent gridComponent = new GridComponent();
			gridComponent.setHorizontalSpacing(4f);
			gridComponent.setIsHitTestVisible(false);
			gridComponent.addColumnDefinition(new GridDefinition(RelativeUnit.Auto)); 
			gridComponent.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative)); // Fill all remaining space

			ImageComponent img = new ImageComponent(category.getIcon());
			img.setIsHitTestVisible(false);
			img.setWidth(16f);
			img.setHeight(16f);
			gridComponent.addChild(img);

			StringComponent title = new StringComponent(category.getName());
			title.setVerticalAlignment(VerticalAlignment.Center);
			title.setIsHitTestVisible(false);
			gridComponent.addChild(title);

			stackPanel.addChild(gridComponent);

			SeparatorComponent separator = new SeparatorComponent();
			separator.setIsHitTestVisible(false);
			stackPanel.addChild(separator);

			// Loop through modules and add them to the correct category
			for (Module module : Mewing.getInstance().moduleManager.modules) {
				if (module.getCategory().equals(category)) {
					ModuleComponent button = new ModuleComponent(module);
					stackPanel.addChild(button);
				}
			}

			tab.addChild(stackPanel);
			tab.setMaxWidth(600f);
			modulesPane.addWindow(tab);

			xOffset += tab.getMinWidth() + 10;
		}

		modulesPane.addWindow(new SettingsWindow());
		modulesPane.addWindow(new AntiCheatWindow());

		clickGuiNavBar.addPane(modulesPane);
		clickGuiNavBar.addPane(toolsPane);
		clickGuiNavBar.addPane(hudPane);

		modulesPane.initialize();
		toolsPane.initialize();
		hudPane.initialize();

		clickGuiNavBar.setSelectedIndex(0);
	}

	public static CursorStyle getCursor() {
		return currentCursor;
	}

	public static void setCursor(CursorStyle cursor) {
		currentCursor = cursor;
		Input.setCursorStyle(currentCursor);
	}

	public static String getTooltip() {
		return tooltip;
	}

	public static void setTooltip(String tt) {
		if (tooltip != tt)
			tooltip = tt;
	}

	public static boolean isKeyboardInputActive() {
		return isKeyboardInputActive;
	}

	public static void setKeyboardInputActive(boolean state) {
		isKeyboardInputActive = state;
	}

	public void addWindow(Window hud, String pageName) {
		for (Page page : clickGuiNavBar.getPanes()) {
			if (page.getTitle().equals(pageName)) {
				page.addWindow(hud);
				page.moveToFront(hud);
				hud.initialize();
				break;
			}
		}
	}

	public void removeWindow(Window hud, String pageName) {
		for (Page page : clickGuiNavBar.getPanes()) {
			if (page.getTitle().equals(pageName)) {
				page.removeWindow(hud);
				break;
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (clickGuiButton.getValue().getValue() == event.GetKey() && MC.screen == null && !isKeyboardInputActive) {
			setClickGuiOpen(!clickGuiOpen);
			toggleMouse();
		}
	}

	public void setHudActive(HudWindow hud, boolean state) {
		if (state) {
			pinnedHuds.put(hud.getClass(), hud);
			hud.activated.silentSetValue(true);
			hudPane.addWindow(hud);
		} else {
			pinnedHuds.remove(hud.getClass());
			hud.activated.silentSetValue(false);
			hudPane.removeWindow(hud);
		}
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		/**
		 * Moves the selected Tab to where the user moves their mouse.
		 */
		if (clickGuiOpen) {
			clickGuiNavBar.update();
		}

		/**
		 * Updates each of the Tab GUIs that are currently on the screen.
		 */
		for (Window hud : pinnedHuds.values()) {
			hud.update();
		}

		if (esc.isDown() && clickGuiOpen) {
			clickGuiOpen = false;
			toggleMouse();
		}
	}

	@Override
	public void onRender(Render2DEvent event) {
		GuiGraphics drawContext = event.getDrawContext();
		float tickDelta = event.getRenderTickCounter().getGameTimeDeltaPartialTick(false);

		Matrix3x2fStack matrixStack = drawContext.pose();
		matrixStack.pushMatrix();

		int guiScale = MC.getWindow().calculateScale(MC.options.guiScale().get(), MC.isEnforceUnicode());
		matrixStack.scale(1.0f / guiScale, 1.0f / guiScale);

		com.mojang.blaze3d.platform.Window window = MC.getWindow();

		// Render ClickGUI and Topbar
		if (clickGuiOpen) {
			Render2D.drawBox(drawContext, 0, 0, window.getScreenWidth(), window.getScreenHeight(), new Color(26, 26, 26, 100));
			clickGuiNavBar.draw(drawContext, tickDelta);
		}

		// Render HUDS
		if (!clickGuiOpen) {
			for (Window hud : pinnedHuds.values()) {
				hud.draw(drawContext, tickDelta);
			}
		}

		// Draw Tooltip on top of all UI elements
		if (tooltip != null && enableTooltips.getValue()) {
			int mouseX = (int) MC.mouseHandler.xpos();
			int mouseY = (int) MC.mouseHandler.ypos();
			int tooltipWidth = Render2D.getStringWidth(tooltip) + 2;
			int tooltipHeight = 10;

			Render2D.drawRoundedBox(drawContext, mouseX + 12, mouseY + 12, (tooltipWidth + 4) * 2,
					(tooltipHeight + 4) * 2, roundingRadius.getValue(), backgroundColor.getValue().getAsSolid());
			Render2D.drawString(drawContext, tooltip, mouseX + 18, mouseY + 18, foregroundColor.getValue());
		}
		matrixStack.popMatrix();
	}

	/**
	 * Gets whether or not the Click GUI is currently open.
	 *
	 * @return State of the Click GUI.
	 */
	public boolean isClickGuiOpen() {
		return clickGuiOpen;
	}

	public void setClickGuiOpen(boolean state) {
		clickGuiOpen = state;
		setTooltip(null);
	}

	/**
	 * Locks and unlocks the Mouse.
	 */
	public void toggleMouse() {
		if (MC.mouseHandler.isMouseGrabbed()) {
			MC.mouseHandler.releaseMouse();
		} else {
			MC.mouseHandler.grabMouse();
		}
	}
}

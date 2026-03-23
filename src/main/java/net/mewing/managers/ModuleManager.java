/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.logging.LogUtils;

import net.mewing.Mewing;
import net.mewing.api.IAddon;
import net.mewing.event.events.KeyDownEvent;
import net.mewing.event.listeners.KeyDownListener;
import net.mewing.module.AntiCheat;
import net.mewing.module.Module;
import net.mewing.module.modules.combat.Aimbot;
import net.mewing.module.modules.combat.AntiInvis;
import net.mewing.module.modules.combat.AntiKnockback;
import net.mewing.module.modules.combat.AutoRespawn;
import net.mewing.module.modules.combat.AutoSoup;
import net.mewing.module.modules.combat.AutoTotem;
import net.mewing.module.modules.combat.BedAura;
import net.mewing.module.modules.combat.BowAimbot;
import net.mewing.module.modules.combat.Criticals;
import net.mewing.module.modules.combat.CrystalAura;
import net.mewing.module.modules.combat.KillAura;
import net.mewing.module.modules.combat.MaceAura;
import net.mewing.module.modules.combat.Nametags;
import net.mewing.module.modules.combat.Reach;
import net.mewing.module.modules.combat.TriggerBot;
import net.mewing.module.modules.misc.*;
import net.mewing.module.modules.movement.ClickTP;
import net.mewing.module.modules.movement.EntityControl;
import net.mewing.module.modules.movement.FastLadder;
import net.mewing.module.modules.movement.Fly;
import net.mewing.module.modules.movement.Freecam;
import net.mewing.module.modules.movement.Glide;
import net.mewing.module.modules.movement.GuiMove;
import net.mewing.module.modules.movement.HighJump;
import net.mewing.module.modules.movement.Jesus;
import net.mewing.module.modules.movement.Jetpack;
import net.mewing.module.modules.movement.NoFall;
import net.mewing.module.modules.movement.NoJumpDelay;
import net.mewing.module.modules.movement.NoSlowdown;
import net.mewing.module.modules.movement.Noclip;
import net.mewing.module.modules.movement.ReverseStep;
import net.mewing.module.modules.movement.Safewalk;
import net.mewing.module.modules.movement.Sneak;
import net.mewing.module.modules.movement.Speed;
import net.mewing.module.modules.movement.Spider;
import net.mewing.module.modules.movement.Sprint;
import net.mewing.module.modules.movement.Step;
import net.mewing.module.modules.movement.Strafe;
import net.mewing.module.modules.render.Breadcrumbs;
import net.mewing.module.modules.render.ChestESP;
import net.mewing.module.modules.render.EntityESP;
import net.mewing.module.modules.render.FocusFps;
import net.mewing.module.modules.render.Fullbright;
import net.mewing.module.modules.render.ItemESP;
import net.mewing.module.modules.render.NoRender;
import net.mewing.module.modules.render.POV;
import net.mewing.module.modules.render.PlayerESP;
import net.mewing.module.modules.render.BlockESP;
import net.mewing.module.modules.render.Tooltips;
import net.mewing.module.modules.render.Tracer;
import net.mewing.module.modules.render.Trajectory;
import net.mewing.module.modules.render.XRay;
import net.mewing.module.modules.render.Zoom;
import net.mewing.module.modules.world.AutoFarm;
import net.mewing.module.modules.world.AutoSign;
import net.mewing.module.modules.world.Nuker;
import net.mewing.module.modules.world.Scaffold;
import net.mewing.module.modules.world.Surround;
import net.mewing.module.modules.world.TileBreaker;
import net.mewing.settings.Setting;
import net.mewing.settings.types.EnumSetting;
import net.minecraft.client.Minecraft;

public class ModuleManager implements KeyDownListener {
	private static final Minecraft MC = Minecraft.getInstance();

	public ArrayList<Module> modules = new ArrayList<Module>();

	// Modules
	public Aimbot aimbot = new Aimbot();
	public AntiCactus anticactus = new AntiCactus();
	public AntiInvis antiinvis = new AntiInvis();
	public AntiKnockback antiknockback = new AntiKnockback();
	public AutoEat autoeat = new AutoEat();
	public AutoFarm autofarm = new AutoFarm();
	public AutoFish autofish = new AutoFish();
	public AntiHunger antihunger = new AntiHunger();
	public AutoOminousBottle autoOminousBottle = new AutoOminousBottle();
	public AutoShear autoShear = new AutoShear();
	public AutoSign autosign = new AutoSign();
	public AutoSoup autosoup = new AutoSoup();
	public AutoTotem autoTotem = new AutoTotem();
	public AutoRespawn autorespawn = new AutoRespawn();
	public AutoWalk autowalk = new AutoWalk();
	public BedAura bedAura = new BedAura();
	public BlockESP blockesp = new BlockESP();
	public BowAimbot bowaimbot = new BowAimbot();
	public Breadcrumbs breadcrumbs = new Breadcrumbs();
	public ChestESP chestesp = new ChestESP();
	public Criticals criticals = new Criticals();
	public CrystalAura crystalaura = new CrystalAura();
	public ClickTP clickTP = new ClickTP();
	public DiscordRPCModule discordRPC = new DiscordRPCModule();
	public EntityControl entityControl = new EntityControl();
	public EntityESP entityesp = new EntityESP();
	public EXPThrower expthrower = new EXPThrower();
	public FakePlayer fakeplayer = new FakePlayer();
	public FastLadder fastladder = new FastLadder();
	public FastPlace fastplace = new FastPlace();
	public FastBreak fastbreak = new FastBreak();
	public Fly fly = new Fly();
	public Freecam freecam = new Freecam();
	public Fullbright fullbright = new Fullbright();
	public ItemESP itemesp = new ItemESP();
	public NoRender norender = new NoRender();
	public FocusFps focusfps = new FocusFps();
	public Glide glide = new Glide();
	public GuiMove guimove = new GuiMove();
	public HighJump higherjump = new HighJump();
	public Jesus jesus = new Jesus();
	public Jetpack jetpack = new Jetpack();
	public KillAura killaura = new KillAura();
	public MaceAura maceaura = new MaceAura();
	public MCA mcf = new MCA();
	public Nametags nametags = new Nametags();
	public Noclip noclip = new Noclip();
	public NoFall nofall = new NoFall();
	public NoJumpDelay nojumpdelay = new NoJumpDelay();
	public NoSlowdown noslowdown = new NoSlowdown();
	public Nuker nuker = new Nuker();
	public PlayerESP playeresp = new PlayerESP();
	public POV pov = new POV();
	public RandomPlace randomplace = new RandomPlace();
	public Reach reach = new Reach();
	public ReverseStep reverseStep = new ReverseStep();
	public Safewalk safewalk = new Safewalk();
	public Scaffold scaffold = new Scaffold();
	public Sneak sneak = new Sneak();
	public Speed speed = new Speed();
	public Spider spider = new Spider();
	public Sprint sprint = new Sprint();
	public Step step = new Step();
	public Strafe strafe = new Strafe();
	public Surround surround = new Surround();
	public TileBreaker tilebreaker = new TileBreaker();
	public Timer timer = new Timer();
	public Tooltips tooltips = new Tooltips();
	public Tracer tracer = new Tracer();
	public Trajectory trajectory = new Trajectory();
	public TriggerBot triggerbot = new TriggerBot();
	public XCarry xCarry = new XCarry();
	public XRay xray = new XRay();
	public Zoom zoom = new Zoom();

	public EnumSetting<AntiCheat> antiCheat = EnumSetting.<AntiCheat>builder().id("mewing_anticheat")
			.displayName("Current AntiCheat")
			.description(
					"This setting will disable any modules or features that are known to be detected by a specific anticheat. ")
			.defaultValue(AntiCheat.Vanilla).onUpdate(s -> {
				for (Module module : modules) {
					if (module.isDetectable(s))
						module.state.setValue(false);
				}
			}).build();

	public ModuleManager(List<IAddon> addons) {
		try {
			// Attempts to find each field of type Module and add it to the module list.
			for (Field field : ModuleManager.class.getDeclaredFields()) {
				if (!Module.class.isAssignableFrom(field.getType()))
					continue;
				Module module = (Module) field.get(this);
				addModule(module);
			}

			// Gets each Addon and adds their modules to the client.
			addons.stream().filter(Objects::nonNull).forEach(addon -> {
				addon.modules().forEach(module -> {
					addModule(module);
				});
			});
		} catch (Exception e) {
			LogUtils.getLogger().error("Error initializing Mewing modules: " + e.getMessage());
		}

		// Registers all Module settings to the settings manager.
		for (Module module : modules) {
			for (Setting<?> setting : module.getSettings()) {
				SettingManager.registerSetting(setting);
			}
		}

		Mewing.getInstance().eventManager.AddListener(KeyDownListener.class, this);
	}

	public void addModule(Module module) {
		modules.add(module);
	}

	public void disableAll() {
		for (Module module : modules) {
			module.state.setValue(false);
		}
	}

	public Module getModuleByName(String string) {
		for (Module module : modules) {
			if (module.getName().equalsIgnoreCase(string)) {
				return module;
			}
		}
		return null;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (MC.screen == null) {
			for (Module module : modules) {
				if (module.isDetectable(antiCheat.getValue()))
					continue;

				Key binding = module.getBind().getValue();
				if (binding.getValue() == event.GetKey()) {
					module.toggle();
				}
			}
		}
	}
}

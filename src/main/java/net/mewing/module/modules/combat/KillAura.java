/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.combat;

import java.util.ArrayList;

import net.mewing.Mewing;
import net.mewing.event.events.TickEvent;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.listeners.TickListener;
import net.mewing.managers.rotation.RotationMode;
import net.mewing.managers.rotation.goals.EntityGoal;
import net.mewing.module.AntiCheat;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;
import net.mewing.settings.types.EnumSetting;
import net.mewing.settings.types.FloatSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class KillAura extends Module implements TickListener {
	private enum Priority {
		LOWESTHP, CLOSEST
	}

	private final Priority priority = Priority.CLOSEST; // why isnt this a setting???

	private final FloatSetting radius = FloatSetting.builder().id("killaura_radius").displayName("Radius")
			.description("Radius that KillAura will target entities.").defaultValue(5f).minValue(0.1f).maxValue(10f)
			.step(0.1f).build();

	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("killaura_target_animals")
			.displayName("Target Animals").description("Target animals.").defaultValue(false).build();

	private final BooleanSetting targetMonsters = BooleanSetting.builder().id("killaura_target_monsters")
			.displayName("Target Monsters").description("Target Monsters.").defaultValue(true).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("killaura_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("killaura_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private final FloatSetting randomness = FloatSetting.builder().id("killaura_randomness").displayName("Randomness")
			.description("The randomness of the delay between when KillAura will hit a target.").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1.0f).build();

	private final BooleanSetting legit = BooleanSetting.builder().id("killaura_legit").displayName("Legit")
			.description(
					"Whether a raycast will be used to ensure that KillAura will not hit a player outside of the view")
			.defaultValue(false).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("killaura_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("killaura_max_rotation")
			.displayName("Max Rotation").description("The max speed that KillAura will rotate").defaultValue(10.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("killaura_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("killaura_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private LivingEntity entityToAttack;

	public KillAura() {
		super("KillAura");

		setCategory(Category.of("Combat"));
		setDescription("Attacks anything within your personal space.");

		addSetting(radius);
		addSetting(targetAnimals);
		addSetting(targetMonsters);
		addSetting(targetPlayers);
		addSetting(targetFriends);
		addSetting(legit);
		addSetting(randomness);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);

		setDetectable(AntiCheat.Matrix); // NPC

	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Mewing.getInstance().rotationManager.setGoal(null);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(TickEvent.Pre event) {
		int randomnessValue = randomness.getValue().intValue();
		boolean state = randomnessValue == 0
				|| (Math.round(Math.random() * Math.round(randomness.max_value))) % randomnessValue == 0;

		ArrayList<Entity> hitList = new ArrayList<Entity>();
		boolean found = false;

		// Add all potential entities to the 'hitlist'
		if (targetAnimals.getValue() || targetMonsters.getValue()) {
			for (Entity entity : Mewing.getInstance().entityManager.getEntities()) {
				if (MC.player.distanceToSqr(entity) > radius.getValueSqr())
					continue;
				if ((entity instanceof Animal && targetAnimals.getValue())
						|| (entity instanceof Enemy && targetMonsters.getValue())) {
					hitList.add(entity);
				}
			}
		}

		// Add all potential players to the 'hitlist'
		if (targetPlayers.getValue()) {
			for (Player player : Mewing.getInstance().entityManager.getPlayers()) {
				if (!targetFriends.getValue() && Mewing.getInstance().friendsList.contains(player))
					continue;

				if (player == MC.player || MC.player.distanceToSqr(player) > (radius.getValueSqr())) {
					continue;
				}
				hitList.add(player);
			}
		}

		// For each entity, get the entity that matches a criteria.
		for (Entity entity : hitList) {
			LivingEntity le = (LivingEntity) entity;
			if (entityToAttack == null) {
				entityToAttack = le;
				found = true;
			} else {
				if (priority == Priority.LOWESTHP) {
					if (le.getHealth() <= entityToAttack.getHealth()) {
						entityToAttack = le;
						found = true;
					}
				} else if (priority == Priority.CLOSEST) {
					if (MC.player.distanceToSqr(le) <= MC.player.distanceToSqr(entityToAttack)) {
						entityToAttack = le;
						found = true;
					}
				}
			}
		}

		// If the entity is found, we want to attach it.
		if (found) {
			EntityGoal rotation = EntityGoal.builder().goal(entityToAttack).mode(rotationMode.getValue())
					.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
					.yawRandomness(yawRandomness.getValue()).build();
			Mewing.getInstance().rotationManager.setGoal(rotation);

			if (MC.player.getAttackStrengthScale(0) == 1) {

				if (state) {
					if (legit.getValue()) {
						HitResult ray = MC.hitResult;

						if (ray != null && ray.getType() == HitResult.Type.ENTITY) {
							EntityHitResult entityResult = (EntityHitResult) ray;
							Entity ent = entityResult.getEntity();

							if (ent == entityToAttack) {
								MC.player.swing(InteractionHand.MAIN_HAND);
								MC.gameMode.attack(MC.player, entityToAttack);
							}
						}
					} else {
						MC.player.swing(InteractionHand.MAIN_HAND);
						MC.gameMode.attack(MC.player, entityToAttack);
					}
				}
			}
		} else {
			Mewing.getInstance().rotationManager.setGoal(null);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}

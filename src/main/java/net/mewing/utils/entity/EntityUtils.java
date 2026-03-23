/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.utils.entity;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.GameType;

public class EntityUtils {
	protected static final MewingClient MEWING_CLIENT = Mewing.getInstance();
	protected static final Minecraft MC = MewingClient.MC;

	/**
	 * Predicates
	 */
	public static final Predicate<Entity> IS_ATTACKABLE = e -> e != null && !e.isRemoved()
			&& (e instanceof LivingEntity && ((LivingEntity) e).getHealth() > 0 || e instanceof EndCrystal
					|| e instanceof ShulkerBullet)
			&& e != MC.player && !(e instanceof FakePlayerEntity) && !MEWING_CLIENT.friendsList.contains(e.getUUID());
	public static final Predicate<Animal> IS_VALID_ANIMAL = a -> a != null && !a.isRemoved() && a.getHealth() > 0;

	public static final Predicate<Player> IS_PLAYER = p -> p != null && !p.isRemoved() && p.getHealth() > 0
			&& !MEWING_CLIENT.friendsList.contains(p.getUUID()) && p != MC.player && !(p instanceof FakePlayerEntity);

	public static Stream<Entity> getAttackableEntities() {
		return StreamSupport.stream(Mewing.getInstance().entityManager.getEntities().spliterator(), true).filter(IS_ATTACKABLE);
	}

	public static Stream<Animal> getValidAnimals() {
		return StreamSupport.stream(Mewing.getInstance().entityManager.getEntities().spliterator(), true).filter(Animal.class::isInstance)
				.map(e -> (Animal) e).filter(IS_VALID_ANIMAL);
	}

	public static GameType getGameMode(Player player) {
		if (player == null)
			return null;
		PlayerInfo playerListEntry = MC.getConnection().getPlayerInfo(player.getUUID());
		if (playerListEntry == null)
			return null;
		return playerListEntry.getGameMode();
	}
}

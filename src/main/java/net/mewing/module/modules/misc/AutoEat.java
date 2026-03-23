/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.misc;

import net.mewing.Mewing;
import net.mewing.MewingClient;
import net.mewing.event.events.FoodLevelEvent;
import net.mewing.event.events.ItemUsedEvent;
import net.mewing.event.events.PlayerHealthEvent;
import net.mewing.event.listeners.FoodLevelListener;
import net.mewing.event.listeners.ItemUsedListener;
import net.mewing.event.listeners.PlayerHealthListener;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.mewing.settings.types.BooleanSetting;
import net.mewing.settings.types.FloatSetting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoEat extends Module implements FoodLevelListener, PlayerHealthListener, ItemUsedListener {
	private ItemStack lastUsedItemStack = null;
	private int previousSlot = -1;
	private boolean isEating = false;

	private final BooleanSetting swapBack = BooleanSetting.builder().id("autoeat_swap_back").displayName("Swap Back")
			.description("Whether the player's slot will be switched back to their previous slot after eating.")
			.defaultValue(true).build();

	private final BooleanSetting fillToFull = BooleanSetting.builder().id("autoeat_fill_to_hull")
			.displayName("Fill To Full").description("Whether to fill the player's hunger to full.").defaultValue(true)
			.build();

	private final FloatSetting hungerSetting = FloatSetting.builder().id("autoeat_hunger").displayName("Hunger")
			.description("Determines when AutoEat will trigger.").defaultValue(10f).minValue(1f).maxValue(20f).step(1f)
			.build();

	private final FloatSetting healthSetting = FloatSetting.builder().id("autoeat_health").displayName("Health")
			.description("Determines when AutoEat will trigger based on health.").defaultValue(10f).minValue(1f)
			.maxValue(20f).step(1f).build();

	private final BooleanSetting prioritizeGapples = BooleanSetting.builder().id("autoeat_prioritize_gapples")
			.displayName("Prioritize Gapples").description("Prioritizes enchanted golden apples and golden apples.")
			.defaultValue(true).build();

	public AutoEat() {
		super("AutoEat");

		setCategory(Category.of("Misc"));
		setDescription("Automatically eats the best food in your inventory.");

		addSetting(fillToFull);
		addSetting(swapBack);
		addSetting(hungerSetting);
		addSetting(healthSetting);
		addSetting(prioritizeGapples);
	}

	@Override
	public void onDisable() {
		Mewing.getInstance().eventManager.RemoveListener(FoodLevelListener.class, this);
		Mewing.getInstance().eventManager.RemoveListener(PlayerHealthListener.class, this);
		Mewing.getInstance().eventManager.RemoveListener(ItemUsedListener.class, this);
	}

	@Override
	public void onEnable() {
		Mewing.getInstance().eventManager.AddListener(FoodLevelListener.class, this);
		Mewing.getInstance().eventManager.AddListener(PlayerHealthListener.class, this);
		Mewing.getInstance().eventManager.AddListener(ItemUsedListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	public void setHunger(int hunger) {
		hungerSetting.setValue((float) hunger);
	}

	public void setHealth(float health) {
		healthSetting.setValue(health);
	}

	private boolean shouldEat() {
		if (isEating && fillToFull.getValue()) {
			return MC.player.getFoodData().needsFood();
		} else {
			int foodLevel = MC.player.getFoodData().getFoodLevel();
			return foodLevel <= hungerSetting.getValue();
		}
	}

	private boolean healthBelowThreshold() {
		float health = MC.player.getHealth();
		return health <= healthSetting.getValue();
	}

	private boolean isCurrentlyHandEdible() {
		Item item = MC.player.getInventory().getSelectedItem().getItem();
		FoodProperties food = item.components().get(DataComponents.FOOD);
		return food != null;
	}

	private void eatIfNecessary() {
		if (MC.player == null)
			return;

		if (shouldEat() || healthBelowThreshold()) {
			// Eat what is in the current hand.
			if (isCurrentlyHandEdible()) {
				MC.options.keyUse.setDown(true);
				isEating = true;
			} else {
				// Else find the hand to eat and start eating.
				int foodSlot = -1;
				FoodProperties bestFood = null;

				for (int i = 0; i < 9; i++) {
					Item item = MC.player.getInventory().getItem(i).getItem();
					FoodProperties food = item.components().get(DataComponents.FOOD);
					if (food == null)
						continue;

					if (prioritizeGapples.getValue()) {
						if (item == Items.ENCHANTED_GOLDEN_APPLE) {
							bestFood = food;
							foodSlot = i;
							break;
						} else if (item == Items.GOLDEN_APPLE) {
							bestFood = food;
							foodSlot = i;
						}
					}

					if (bestFood != null) {
						if (food.nutrition() > bestFood.nutrition() && item != Items.GOLDEN_APPLE
								&& item != Items.ENCHANTED_GOLDEN_APPLE) {
							bestFood = food;
							foodSlot = i;
						}
					} else {
						bestFood = food;
						foodSlot = i;
					}
				}

				if (bestFood != null) {
					if (swapBack.getValue()) {
						previousSlot = MC.player.getInventory().getSelectedSlot();
						MewingClient.LOGGER.info("[Mewing] Setting previous slot to: " + previousSlot);
					}

					lastUsedItemStack = MC.player.getInventory().getItem(foodSlot);
					isEating = true;
					MewingClient.LOGGER.info("[Mewing] Eating Slot: " + foodSlot);
					MC.player.getInventory().setSelectedSlot(foodSlot);
					MC.options.keyUse.setDown(true);
				}
			}
		}
	}

	@Override
	public void onFoodLevelChanged(FoodLevelEvent readPacketEvent) {
		eatIfNecessary();
	}

	@Override
	public void onHealthChanged(PlayerHealthEvent readPacketEvent) {
		eatIfNecessary();
	}

	@Override
	public void onItemUsed(ItemUsedEvent.Pre event) {

	}

	@Override
	public void onItemUsed(ItemUsedEvent.Post event) {
		MewingClient.LOGGER.info("[Mewing] Item POST");
		if (lastUsedItemStack != null && ItemStack.isSameItem(event.getItemStack(), lastUsedItemStack)) {
			MewingClient.LOGGER.info("[Mewing] EATING Item was used");
			boolean shouldContinueEating = shouldEat();
			if (!shouldContinueEating) {
				MC.options.keyUse.setDown(false);
				lastUsedItemStack = null;
				MewingClient.LOGGER.info("[Mewing]No longer eating : " + previousSlot);
				isEating = false;
				if (swapBack.getValue() && previousSlot != -1) {
					MewingClient.LOGGER.info("[Mewing] Swapping back to : " + previousSlot);
					MC.player.getInventory().setSelectedSlot(previousSlot);
					previousSlot = -1;
				}
			}
		}
	}
}
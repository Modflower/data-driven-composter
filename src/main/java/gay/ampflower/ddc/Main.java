/* Copyright 2022 Ampflower
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package gay.ampflower.ddc;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class Main {
	public static final Logger logger = LoggerFactory.getLogger("data-driven-composter/ddc");

	/**
	 * Backup map of the vanilla registry for hot reloading.
	 *
	 * @since 0.1.0
	 */
	private static Object2FloatMap<ItemConvertible> vanillaCompostableItems;

	/**
	 * Initialises the mod and the lifecycle hooks.
	 *
	 * @since 0.1.0
	 */
	public static void init() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			// Using an ArrayMap for compact memory size.
			vanillaCompostableItems = new Object2FloatArrayMap<>(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE);

			// Generate settings if they aren't already present.
			if (!Config.instance.isReady()) {
				Config.instance.generateSettings();
			}

			ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.clear();

			hotLoad();
		});

		// TODO: Figure out a decent way to go about hot datapack reloading, if this
		// isn't the best way to go about this.
		// This is somewhat hacky, but intends to make sure that other implementations
		// such as QSL has the ability to register its own additions via its internal
		// registries.
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> {
			ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.clear();
			Config.reload();
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> hotLoad());

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			// Redeploy the vanilla registry. This intends to help avoid memory leaks.
			if (vanillaCompostableItems != null) {
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.clear();
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.putAll(vanillaCompostableItems);

				vanillaCompostableItems = null;
			} else if (Config.instance.disableDefaultVanillaRegistry) {
				logger.warn("The vanillaCompostableItems map is missing. This shouldn't happen!");
			}
		});
	}

	/**
	 * Loads in the new composter registry, along with merging any datapacks
	 * registrations where applicable.
	 *
	 * @since 0.1.0
	 */
	public static void hotLoad() {
		// Clear the map and redeploy the vanilla registry if it isn't meant to be
		// cleared.
		// This intends to clear any interference that may otherwise be caused by
		// hot reloading.
		if (Config.instance.disableDefaultVanillaRegistry) {
			// The registry is already cleared by the outer method prior to reloading.
			// This only clears if datapack clearing is also enabled.
			if (Config.instance.disableDatapackRegistry) {
				logger.info("[DDC] Evoking {} as `disableDatapackRegistry` for the composter is enabled.",
						ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE);
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.clear();
			} else {
				logger.info("[DDC] Evoking vanilla registry entries manually to account for other mods.");
				// Capture a working map to avoid spamming the logs when our logger is enabled.
				var intermediary = new Object2FloatOpenHashMap<>(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE);
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.clear();

				// Manually clear out all *exact* entries from the intermediate step.
				Object2FloatMaps.fastForEach(vanillaCompostableItems,
						e -> intermediary.remove(e.getKey(), e.getFloatValue()));

				// Set the intermediate step back into working.
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.putAll(intermediary);
			}
		} else {
			// Same as above, but redeploys the vanilla registry.
			// Not sure why you want this, but here you go.
			if (Config.instance.disableDatapackRegistry) {
				logger.info(
						"[DDC] Evoking {} in favour of vanilla registry base as `disableDatapackRegistry` for the composter is enabled.",
						ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE);
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.clear();
				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.putAll(vanillaCompostableItems);
			} else {
				logger.info("[DDC] Merging {} with priority over the vanilla composter registry.",
						ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE);
				logger.warn(
						"[DDC] This may make for a missed removal if any of the datapacks originally intended for that in the composter.");
				var intermediary = new Object2FloatOpenHashMap<>(vanillaCompostableItems);

				// Should a datapack define its own composter information, let it override
				// Vanilla.
				intermediary.keySet().removeAll(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.keySet());

				ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.putAll(intermediary);
			}
		}

		// Register our data to the vanilla registry.
		register();
	}

	/**
	 * Registers the custom composter registry.
	 *
	 * @since 0.1.0
	 */
	public static void register() {
		var compostableItems = new Object2FloatOpenHashMap<ItemConvertible>();

		Config.instance.compostableItems.forEach((k, v) -> {
			var item = Registries.ITEM.getOrEmpty(k);
			if (item.isEmpty()) {
				logger.warn("{} -> {} not preset at current time.", k, v);
			} else {
				compostableItems.put(item.get(), (float) v);
			}
		});

		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.putAll(compostableItems);
	}
}

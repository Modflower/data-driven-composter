/* Copyright 2022 Ampflower
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package gay.ampflower.ddc;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.ComposterBlock;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Configuration for Data Driven Composters.
 *
 * @author Ampflower
 * @implNote This is initialised at Minecraft's bootstrap via
 *           {@link gay.ampflower.ddc.mixins.MixinComposterBlock#ddc$redirect(Object2FloatMap)}.
 *           Reading {@link #compostableItems} and other maps will require a
 *           successful call to {@link #isReady()} beforehand.
 * @since 0.0.0
 **/
@SuppressWarnings("JavadocReference")
public class Config {
	private static final Path config = FabricLoader.getInstance().getConfigDir().resolve("ddc.json");
	/**
	 * An instance of GSON that allows for using complex keys in maps, pretty
	 * printing for ease of reading and editing, lenient parsing to allow for
	 * comments &amp; any errors that maybe introduced by the end user, and an
	 * {@link Identifier} type adaptor to read & write identifiers correctly.
	 */
	private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting()
			.setLenient().registerTypeAdapter(Identifier.class, new IdentifierTypeAdaptor()).create();
	public static Config instance;

	/**
	 * Whether the config is being initialised for the first time.
	 */
	private transient boolean generating;

	/**
	 * Disables {@link ComposterBlock#registerDefaultCompostableItems()} to allow
	 * for fully custom entries.
	 * <p>
	 * May break any mods that rely on this method to function.
	 */
	public boolean disableDefaultVanillaRegistry = false;

	/**
	 * Disables datapack registration of composter entries to allow for fully custom
	 * entries.
	 * <p>
	 * May break any mods that rely on the composter accepting their items.
	 *
	 * @since 0.1.0
	 */
	public boolean disableDatapackRegistry = false;

	/**
	 * Logs all calls to
	 * {@link ComposterBlock#registerCompostableItem(float, net.minecraft.item.ItemConvertible)}
	 */
	public boolean logAllDirectRegistration = FabricLoader.getInstance().isDevelopmentEnvironment();

	/**
	 * Items that can be composted by a composter, giving a chance of it to
	 * increment the level.
	 *
	 * @implNote This is intended to be read on server start and datapack reload.
	 * @since 0.1.0
	 */
	@SerializedName("compostableItems")
	public Map<Identifier, Float> compostableItems;

	static {
		reload();
	}

	public static void reload() {
		Config instance = null;
		if (Files.exists(config)) {
			try (var reader = Files.newBufferedReader(config)) {
				instance = GSON.fromJson(reader, Config.class);
			} catch (IOException ioe) {
				Main.logger.warn("Unable to read config, regenerating...", ioe);
			}
		}

		if (instance == null) {
			if (Config.instance == null) {
				instance = new Config();
				instance.generating = true;
			} else {
				Main.logger.warn("Configuration already in memory, writing back to disc...");
				Config.instance.write();
				return;
			}
		}

		Config.instance = instance;
	}

	/* Private as you shouldn't be directly initialising this. */
	private Config() {
	}

	/**
	 * Initialises the config if applicable once it's loaded.
	 */
	public boolean isReady() {
		return !generating;
	}

	/**
	 * Generates the default config using any registered values.
	 */
	public void generateSettings() {
		if (generating) {
			compostableItems = new Object2FloatOpenHashMap<>();
			ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE
					.forEach((item, value) -> compostableItems.put(Registries.ITEM.getId(item.asItem()), value));

			write();
		}
	}

	private void write() {
		try (var writer = Files.newBufferedWriter(config)) {
			GSON.toJson(this, writer);
		} catch (IOException ioe) {
			Main.logger.warn("Unable to write config?", ioe);
		}
	}

	/**
	 * {@link Identifier} type adaptor to allow for reading &amp; writing of
	 * identifiers as keys &amp; values.
	 */
	private static class IdentifierTypeAdaptor implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
		@Override
		public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return Identifier.tryParse(json.getAsString());
		}

		@Override
		public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
}

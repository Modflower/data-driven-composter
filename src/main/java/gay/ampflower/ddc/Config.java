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
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
	public static final Config INSTANCE;
	/**
	 * Whether initialisation is deferred.
	 * <p>
	 * Registries are initialised after this is read, so it is important that any
	 * registry calls are guarded as Identifier and transferred when ready.
	 */
	private transient boolean deferred;

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
	 * Logs all calls to
	 * {@link ComposterBlock#registerCompostableItem(float, net.minecraft.item.ItemConvertible)}
	 */
	public boolean logAllDirectRegistration = FabricLoader.getInstance().isDevelopmentEnvironment();

	/**
	 * Raw version of {@link #compostableItems} to act as a container for GSON.
	 */
	@SerializedName("compostableItems")
	private Map<Identifier, Float> compostableItemsRaw;

	/**
	 * Items that can be composted by a composter, giving a chance of it to
	 * increment the level.
	 *
	 * @implNote This is <em>not ready</em> unless {@link #isReady()} is called and
	 *           returns {@code true}.
	 */
	public transient Object2FloatMap<ItemConvertible> compostableItems;

	static {
		Config instance = null;
		if (Files.exists(config)) {
			try (var reader = Files.newBufferedReader(config)) {
				instance = GSON.fromJson(reader, Config.class);
				instance.deferred = true;
			} catch (IOException ioe) {
				Main.logger.warn("Unable to read config, regenerating...", ioe);
			}
		}
		if (instance == null) {
			instance = new Config();
			instance.generating = true;
		}
		INSTANCE = instance;
	}

	/* Private as you shouldn't be directly initialising this. */
	private Config() {
	}

	/**
	 * Initialises the config if applicable once it's loaded.
	 */
	public boolean isReady() {
		if (generating) {
			return false;
		}
		if (deferred) {
			deferred = false;
			compostableItems = new Object2FloatOpenHashMap<>();
			compostableItemsRaw.forEach((k, v) -> compostableItems.put(Registry.ITEM.get(k), (float) v));
			compostableItemsRaw = null;
		}
		return true;
	}

	/**
	 * Generates the default config using any registered values.
	 */
	public void generateSettings() {
		if (generating) {
			compostableItemsRaw = new Object2FloatOpenHashMap<>();
			ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE
					.forEach((item, value) -> compostableItemsRaw.put(Registry.ITEM.getId(item.asItem()), value));

			try (var writer = Files.newBufferedWriter(config)) {
				GSON.toJson(this, writer);
			} catch (IOException ioe) {
				Main.logger.warn("Unable to write config?", ioe);
			}
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

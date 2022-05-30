/* Copyright 2022 Ampflower
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package gay.ampflower.ddc;

import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.*;

/**
 * Logs any and all puts to the map.
 *
 * @author Ampflower
 * @since 0.0.0
 **/
public class LoggingObject2FloatMap<K> implements Object2FloatMap<K> {
	private final Object2FloatMap<K> backing;

	public LoggingObject2FloatMap(Object2FloatMap<K> backing) {
		this.backing = backing;
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public boolean isEmpty() {
		return backing.isEmpty();
	}

	@Override
	public double applyAsDouble(K operand) {
		return backing.applyAsDouble(operand);
	}

	@Override
	public float put(K key, float value) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Put detected for {}: {} -> {}", ic, Registry.ITEM.getId(ic.asItem()), value,
					new Throwable());
		} else {
			Main.logger.info("Put detected for {} -> {}", key, value, new Throwable());
		}
		return backing.put(key, value);
	}

	@Override
	public float getFloat(Object o) {
		return backing.getFloat(o);
	}

	@Override
	public void defaultReturnValue(float v) {
		backing.defaultReturnValue(v);
	}

	@Override
	public float defaultReturnValue() {
		return backing.defaultReturnValue();
	}

	@Override
	public Object2ByteFunction<K> andThenByte(Float2ByteFunction after) {
		return backing.andThenByte(after);
	}

	@Override
	public Byte2FloatFunction composeByte(Byte2ObjectFunction<K> before) {
		return backing.composeByte(before);
	}

	@Override
	public Object2ShortFunction<K> andThenShort(Float2ShortFunction after) {
		return backing.andThenShort(after);
	}

	@Override
	public Short2FloatFunction composeShort(Short2ObjectFunction<K> before) {
		return backing.composeShort(before);
	}

	@Override
	public Object2IntFunction<K> andThenInt(Float2IntFunction after) {
		return backing.andThenInt(after);
	}

	@Override
	public Int2FloatFunction composeInt(Int2ObjectFunction<K> before) {
		return backing.composeInt(before);
	}

	@Override
	public Object2LongFunction<K> andThenLong(Float2LongFunction after) {
		return backing.andThenLong(after);
	}

	@Override
	public Long2FloatFunction composeLong(Long2ObjectFunction<K> before) {
		return backing.composeLong(before);
	}

	@Override
	public Object2CharFunction<K> andThenChar(Float2CharFunction after) {
		return backing.andThenChar(after);
	}

	@Override
	public Char2FloatFunction composeChar(Char2ObjectFunction<K> before) {
		return backing.composeChar(before);
	}

	@Override
	public Object2FloatFunction<K> andThenFloat(Float2FloatFunction after) {
		return backing.andThenFloat(after);
	}

	@Override
	public Float2FloatFunction composeFloat(Float2ObjectFunction<K> before) {
		return backing.composeFloat(before);
	}

	@Override
	public Object2DoubleFunction<K> andThenDouble(Float2DoubleFunction after) {
		return backing.andThenDouble(after);
	}

	@Override
	public Double2FloatFunction composeDouble(Double2ObjectFunction<K> before) {
		return backing.composeDouble(before);
	}

	@Override
	public <T> Object2ObjectFunction<K, T> andThenObject(Float2ObjectFunction<? extends T> after) {
		return backing.andThenObject(after);
	}

	@Override
	public <T> Object2FloatFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
		return backing.composeObject(before);
	}

	@Override
	public <T> Object2ReferenceFunction<K, T> andThenReference(Float2ReferenceFunction<? extends T> after) {
		return backing.andThenReference(after);
	}

	@Override
	public <T> Reference2FloatFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
		return backing.composeReference(before);
	}

	@Override
	public ObjectSet<Entry<K>> object2FloatEntrySet() {
		return backing.object2FloatEntrySet();
	}

	@Override
	public ObjectSet<K> keySet() {
		return backing.keySet();
	}

	@Override
	public FloatCollection values() {
		return backing.values();
	}

	@Override
	public Float apply(K key) {
		return backing.apply(key);
	}

	@NotNull
	@Override
	public <V> Function<V, Float> compose(@NotNull Function<? super V, ? extends K> before) {
		return backing.compose(before);
	}

	@Override
	public boolean containsKey(Object o) {
		return backing.containsKey(o);
	}

	@Override
	public void putAll(@NotNull Map<? extends K, ? extends Float> m) {
		Main.logger.info("Put detected for given map {}", m, new Throwable());
		backing.putAll(m);
	}

	@Override
	public boolean containsValue(float v) {
		return backing.containsValue(v);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super Float> consumer) {
		backing.forEach(consumer);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> function) {
		backing.replaceAll(function);
	}

	@Override
	public Float computeIfAbsent(K key, @NotNull Function<? super K, ? extends Float> mappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), mappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, mappingFunction,
					new Throwable());
		}
		return backing.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public Float computeIfPresent(K key,
			@NotNull BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, remappingFunction,
					new Throwable());
		}
		return backing.computeIfPresent(key, remappingFunction);
	}

	@Override
	public Float compute(K key, @NotNull BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, remappingFunction,
					new Throwable());
		}
		return backing.compute(key, remappingFunction);
	}

	@Override
	public float getOrDefault(Object key, float defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public float removeFloat(Object key) {
		return backing.removeFloat(key);
	}

	@Override
	public float putIfAbsent(K key, float value) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} -> {}", ic, Registry.ITEM.getId(ic.asItem()), value,
					new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} -> {}", key, value, new Throwable());
		}
		return backing.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, float value) {
		return backing.remove(key, value);
	}

	@Override
	public boolean replace(K key, float oldValue, float newValue) {
		return backing.replace(key, oldValue, newValue);
	}

	@Override
	public float replace(K key, float value) {
		return backing.replace(key, value);
	}

	@Override
	public float computeIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), mappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, mappingFunction,
					new Throwable());
		}
		return backing.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public float computeIfAbsent(K key, Object2FloatFunction<? super K> mappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), mappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, mappingFunction,
					new Throwable());
		}
		return backing.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, remappingFunction,
					new Throwable());
		}
		return backing.computeFloatIfPresent(key, remappingFunction);
	}

	@Override
	public float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Potential put detected for {}: {} with given function {}", ic,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Potential put detected for {} with given function {}", key, remappingFunction,
					new Throwable());
		}
		return backing.computeFloat(key, remappingFunction);
	}

	@Override
	public float merge(K key, float value,
			BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Put detected for {}: {} -> {} with given function {}", ic, value,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Put detected for {} -> {} with given function {}", key, value, remappingFunction,
					new Throwable());
		}
		return backing.merge(key, value, remappingFunction);
	}

	@Override
	public float mergeFloat(K key, float value, FloatBinaryOperator remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Put detected for {}: {} -> {} with given function {}", ic, value,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Put detected for {} -> {} with given function {}", key, value, remappingFunction,
					new Throwable());
		}
		return backing.mergeFloat(key, value, remappingFunction);
	}

	@Override
	public float mergeFloat(K key, float value, DoubleBinaryOperator remappingFunction) {
		if (key instanceof ItemConvertible ic) {
			Main.logger.info("Put detected for {}: {} -> {} with given function {}", ic, value,
					Registry.ITEM.getId(ic.asItem()), remappingFunction, new Throwable());
		} else {
			Main.logger.info("Put detected for {} -> {} with given function {}", key, value, remappingFunction,
					new Throwable());
		}
		return backing.mergeFloat(key, value, remappingFunction);
	}
}

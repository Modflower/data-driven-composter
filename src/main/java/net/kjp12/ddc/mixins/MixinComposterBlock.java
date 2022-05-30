/* Copyright 2022 KJP12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package net.kjp12.ddc.mixins;// Created 2022-28-05T14:24:55

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.kjp12.ddc.Config;
import net.kjp12.ddc.LoggingObject2FloatMap;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects hooks to allow for the composter's input to be overwritten.
 *
 * @author KJP12
 * @since 0.0.0
 **/
@Mixin(ComposterBlock.class)
public class MixinComposterBlock {
	@Shadow
	@Final
	@Mutable
	public static Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE;

	@Inject(method = "registerDefaultCompostableItems", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;defaultReturnValue(F)V", shift = At.Shift.AFTER), cancellable = true)
	private static void ddc$cancelDefaultRegistration(CallbackInfo ci) {
		// This is the earliest that the config can be bootstrapped, and the latest as
		// it is immediately used.
		if (Config.INSTANCE.isReady()) {
			if (Config.INSTANCE.disableDefaultVanillaRegistry) {
				ci.cancel();
				// This must be called now due to the method not complete normally at this
				// point.
				register();
			}
		}
	}

	@Inject(method = "registerDefaultCompostableItems", at = @At("RETURN"))
	private static void ddc$saveDefaultRegistration(CallbackInfo ci) {
		if (Config.INSTANCE.isReady()) {
			// Execute this at return to allow for overwriting vanilla entries.
			register();
		}
		// Checks internally.
		Config.INSTANCE.generateSettings();
	}

	@Unique
	private static void register() {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.putAll(Config.INSTANCE.compostableItems);
	}

	@Redirect(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/block/ComposterBlock;ITEM_TO_LEVEL_INCREASE_CHANCE:Lit/unimi/dsi/fastutil/objects/Object2FloatMap;"))
	private static void ddc$redirectToLoggingMap(Object2FloatMap<ItemConvertible> value) {
		ITEM_TO_LEVEL_INCREASE_CHANCE = Config.INSTANCE.logAllDirectRegistration ? new LoggingObject2FloatMap<>(value)
				: value;
	}
}

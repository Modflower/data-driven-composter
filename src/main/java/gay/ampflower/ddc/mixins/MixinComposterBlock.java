/* Copyright 2022 Ampflower
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package gay.ampflower.ddc.mixins;

import gay.ampflower.ddc.Config;
import gay.ampflower.ddc.LoggingObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects hooks to allow for the composter's input to be overwritten.
 *
 * @author Ampflower
 * @since 0.0.0
 **/
@Mixin(ComposterBlock.class)
public class MixinComposterBlock {
	@Shadow
	@Final
	@Mutable
	public static Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE;

	@Redirect(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/block/ComposterBlock;ITEM_TO_LEVEL_INCREASE_CHANCE:Lit/unimi/dsi/fastutil/objects/Object2FloatMap;"))
	private static void ddc$redirectToLoggingMap(Object2FloatMap<ItemConvertible> value) {
		ITEM_TO_LEVEL_INCREASE_CHANCE = Config.instance.logAllDirectRegistration ? new LoggingObject2FloatMap<>(value)
				: value;
	}
}

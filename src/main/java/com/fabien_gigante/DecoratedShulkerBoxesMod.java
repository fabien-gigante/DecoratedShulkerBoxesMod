package com.fabien_gigante;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ActionResult;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.cauldron.CauldronBehavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecoratedShulkerBoxesMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("decorated-shulker-boxes");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Decorated Shulker Boxes - Mod starting...");

		// Override cauldron behavior to also clean the secondary color of shulker boxes
		CauldronBehavior CLEAN_SHULKER_BOX = ((state, world, pos, player, hand, stack) -> {
			ItemActionResult ret = CauldronBehavior.CLEAN_SHULKER_BOX.interact(state, world, pos, player, hand, stack);
			if (ret.toActionResult() != ActionResult.PASS && !world.isClient) {
				ItemStack itemStack = player.getStackInHand(hand);
                NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(itemStack);
				if (nbt != null) {
					DecoratedShulkerBoxEntity.putNbtSecondaryColor(nbt, null);
					//ItemStack item = DecoratedShulkerBoxEntity.getNbtDisplayedItem(nbt);
					//if (item != null) player.dropItem(item, false);
					DecoratedShulkerBoxEntity.putNbtDisplayedItem(nbt, null);
				}
			}
			return ret;
		});

		// Replace vanilla behavior by overriden behavior
		Map<Item, CauldronBehavior> map = CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map();
		map.replaceAll((item, behavior) -> behavior == CauldronBehavior.CLEAN_SHULKER_BOX ? CLEAN_SHULKER_BOX : behavior);
	}
}
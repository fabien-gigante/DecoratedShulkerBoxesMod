package com.fabien_gigante;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface ISlotListener {
	public default boolean isValidInput(Slot slot, ItemStack stack) { return false; }
	public default void onTakeOutput(Player player, ItemStack stack) {}
}

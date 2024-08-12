package com.fabien_gigante;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public interface IScreenHandlerSlotListener {
    public default boolean isValidInput(Slot slot, ItemStack stack) { return false; }
    public default void onTakeOutput(PlayerEntity player, ItemStack stack) {}
}

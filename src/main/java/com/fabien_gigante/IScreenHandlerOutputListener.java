package com.fabien_gigante;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IScreenHandlerOutputListener {
    public abstract void onTakeOutput(PlayerEntity player, ItemStack stack);
}

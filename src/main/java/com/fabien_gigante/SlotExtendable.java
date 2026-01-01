package com.fabien_gigante;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface SlotExtendable {
	public void allowItemCondition(Predicate<ItemStack> condition);
	public void registerItemTakenAction(BiConsumer<Player, ItemStack> consumer);
}

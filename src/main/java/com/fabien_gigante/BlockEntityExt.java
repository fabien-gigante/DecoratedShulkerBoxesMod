package com.fabien_gigante;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface BlockEntityExt  {
	@SuppressWarnings("deprecation")
	public static NbtCompound getBlockEntityNbt(ItemStack stack) {
		NbtComponent component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
		return component != null ? component.getNbt() : null;
	}
}

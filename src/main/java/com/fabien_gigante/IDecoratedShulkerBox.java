package com.fabien_gigante;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;

// Interface to decoration information 
public interface IDecoratedShulkerBox {
	public static final String NBTKEY_SECONDARY_COLOR = "SecondaryColor";
	public static final String NBTKEY_DISPLAYED_ITEM = "DisplayedItem";

	public default boolean hasDecorations() { return hasSecondaryColor() || hasDisplayedItem(); }

	// Secondary color accessors
	public boolean hasSecondaryColor();
	public DyeColor getSecondaryColor();
	public void setSecondaryColor(DyeColor color);

	// Displayed item accessors
	public boolean hasDisplayedItem();
	public ItemStack getDisplayedItem();
	public void setDisplayedItem(ItemStack stack);

	// Combined setter for easy copy
	public default void setDecorations(IDecoratedShulkerBox source) { 
		if (source == null) return;
		setSecondaryColor(source.getSecondaryColor());
		setDisplayedItem(source.getDisplayedItem());
	}

	// Check shulker inventory content
	public boolean isEmpty();

	// Nbt helper functions for implementation classes : secondary color
	
	public static boolean hasNbtSecondaryColor(NbtCompound nbt) {
		return nbt != null && nbt.contains(NBTKEY_SECONDARY_COLOR);
	}
	public static DyeColor getNbtSecondaryColor(NbtCompound nbt) {
		if (!hasNbtSecondaryColor(nbt)) return null;
		return DyeColor.byId(nbt.getInt(NBTKEY_SECONDARY_COLOR));
	}
	public static void putNbtSecondaryColor(NbtCompound nbt, DyeColor color) {
		if (color != null) nbt.putInt(NBTKEY_SECONDARY_COLOR, color.getId());
		else nbt.remove(NBTKEY_SECONDARY_COLOR);
	}

	// Nbt helper functions for implementation classes : displayed item

	public static boolean hasNbtDisplayedItem(NbtCompound nbt) {
		return nbt != null && nbt.contains(NBTKEY_DISPLAYED_ITEM);
	}
	public static ItemStack getNbtDisplayedItem(RegistryWrapper.WrapperLookup registries, NbtCompound nbt) {
		if (!hasNbtDisplayedItem(nbt)) return null;
		return ItemStack.fromNbt(registries, nbt.get(NBTKEY_DISPLAYED_ITEM)).get();
	}
	public static void putNbtDisplayedItem(RegistryWrapper.WrapperLookup registries, NbtCompound nbt, ItemStack stack) {
		if (stack == null) { nbt.remove(NBTKEY_DISPLAYED_ITEM); return; }
		nbt.put(NBTKEY_DISPLAYED_ITEM, stack.encode(registries));
	}
}
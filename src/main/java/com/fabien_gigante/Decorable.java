package com.fabien_gigante;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

// Interface to decoration information 
public interface Decorable extends Dyeable {
	// Component accessors
	public DecoratedBoxComponent getDecorations();
	public void setDecorations(DecoratedBoxComponent decorations);

	// Secondary color accessors
	public default boolean hasSecondaryColor() { return getSecondaryColor() != null; }
	public default DyeColor getSecondaryColor() { return getDecorations().secondaryColor(); }
	public default void setSecondaryColor(DyeColor color) { setDecorations(getDecorations().setSecondaryColor(color == getColor() ? null : color)); }

	// Displayed item accessors
	public default boolean hasDisplayedItem() { return getDisplayedItem() != null; }
	public default ItemStack getDisplayedItem() { return getDecorations().displayedItem(); }
	public default void setDisplayedItem(ItemStack stack) { setDecorations(getDecorations().setDisplayedItem(stack)); }

	// Check inventory content if any
	public boolean hasContent();
}
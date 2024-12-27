package com.fabien_gigante;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

// Interface to decoration information 
public interface IDecoratedBox {
	// Component accessors
	public DecoratedBoxComponent getDecorations();
	public void setDecorations(DecoratedBoxComponent decorations);

	// Primary color accessor, if any
	public DyeColor getColor();

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
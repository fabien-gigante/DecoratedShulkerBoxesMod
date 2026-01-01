package com.fabien_gigante;

import net.minecraft.world.item.DyeColor;

// Interface to color information 
public interface Dyeable {
	public DyeColor getColor();
	public default void setColor(DyeColor color) { throw new UnsupportedOperationException(); }
}
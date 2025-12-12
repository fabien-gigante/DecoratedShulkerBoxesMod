package com.fabien_gigante;

import net.minecraft.world.item.DyeColor;

// Interface to color information 
public interface IDyed {
	public void setColor(DyeColor color);
	public DyeColor getColor();
}
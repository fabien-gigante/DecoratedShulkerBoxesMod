package com.fabien_gigante;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public interface ShulkerBoxBlockEntityExt {
	public static final String NBTKEY_SECONDARY_COLOR = "SecondaryColor";

	public DyeColor getSecondaryColor();
	public void setSecondaryColor(DyeColor color);

	public static DyeColor getNbtSecondaryColor(NbtCompound nbt) {
		return nbt.contains(NBTKEY_SECONDARY_COLOR) ? DyeColor.byId(nbt.getInt(NBTKEY_SECONDARY_COLOR)) : null;
	}
	public static void putNbtSecondaryColor(NbtCompound nbt, DyeColor color) {
		if (color != null) nbt.putInt(NBTKEY_SECONDARY_COLOR, color.getId());
		else nbt.remove(NBTKEY_SECONDARY_COLOR);
	}

	public void readNbtSecondaryColor(NbtCompound nbt);
	public void writeNbtSecondaryColor(NbtCompound nbt);
}

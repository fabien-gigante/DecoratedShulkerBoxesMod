package com.fabien_gigante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;

public class DecoratedShulkerBoxesModClient implements ClientModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static @Nullable DyeColor lastUsedShulkerBoxColor = null;
	
	// Client-side mod entry point
	@Override
	public void onInitializeClient() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting (client)...");
		for(MenuType<ShulkerBoxMenu> type : DecoratedShulkerBoxesMod.MENU_TYPES.values())
			MenuScreens.register(type, ShulkerBoxScreen::new);
	}
}
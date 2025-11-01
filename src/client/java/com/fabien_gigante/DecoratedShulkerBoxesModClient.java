package com.fabien_gigante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ShulkerBoxScreenHandler;

public class DecoratedShulkerBoxesModClient implements ClientModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Client-side mod entry point
	@Override
	public void onInitializeClient() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting (client)...");
		for(ScreenHandlerType<ShulkerBoxScreenHandler> type : DecoratedShulkerBoxesMod.SCREEN_HANDLER_TYPES.values())
			HandledScreens.register(type, ShulkerBoxScreen::new);
	}
}
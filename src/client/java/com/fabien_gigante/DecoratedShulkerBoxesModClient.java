package com.fabien_gigante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;

public class DecoratedShulkerBoxesModClient implements ClientModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Client-side mod entry point
	@Override
	public void onInitializeClient() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting (client)...");
	}
}
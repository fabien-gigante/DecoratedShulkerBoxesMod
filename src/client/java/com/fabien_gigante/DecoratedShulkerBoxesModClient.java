package com.fabien_gigante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;

public class DecoratedShulkerBoxesModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("decorated-shulker-boxes");
	
	// Client-side mod entry point
	@Override
	public void onInitializeClient() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting (client)...");
	}
}
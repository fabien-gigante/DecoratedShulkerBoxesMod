package com.fabien_gigante;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;

public class DecoratedShulkerBoxesMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("decorated-shulker-boxes");
	
	// Server-side mod entry point
	@Override
	public void onInitialize() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting...");
		DecoratedShulkerBoxCauldronBehavior.init();
	}
}
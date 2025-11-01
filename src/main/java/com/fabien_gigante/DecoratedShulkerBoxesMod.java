package com.fabien_gigante;

import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class DecoratedShulkerBoxesMod implements ModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
    public static final ComponentType<DecoratedBoxComponent> DECORATED_BOX_TYPE = DecoratedBoxComponent.TYPE;
	public static final Map<DyeColor,ScreenHandlerType<ShulkerBoxScreenHandler>> SCREEN_HANDLER_TYPES = new HashMap<>();

	// Server-side mod entry point
	@Override
	public void onInitialize() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting...");
		registerScreens();
		updateLootTables();
	}

	private void registerScreens() {
		for(DyeColor color : DyeColor.values()) {
			var type = new ScreenHandlerType<>( (syncId, playerInventory) -> {
				var handler = new ShulkerBoxScreenHandler(syncId, playerInventory);
				if (handler instanceof IDyed dyed) dyed.setColor(color);
				return handler;
			}, FeatureFlags.VANILLA_FEATURES);
			SCREEN_HANDLER_TYPES.put(color, type);
			Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, ShulkerBoxBlock.get(color).getTranslationKey()), type);
		}
	}

	private void updateLootTables() {
		Set<RegistryKey<LootTable>> SHULKER_BOX_LOOT_TABLES = 
			Stream.concat(Stream.of((DyeColor)null), Arrays.stream(DyeColor.values()))
			.map(color -> ShulkerBoxBlock.get(color).getLootTableKey().orElseThrow())
			.collect(Collectors.toSet());
		LootTableEvents.MODIFY.register((key, builder, source, lookup) -> {
			if (source.isBuiltin() && SHULKER_BOX_LOOT_TABLES.contains(key))
				builder.apply(
					CopyComponentsLootFunction.blockEntity(LootContextParameters.BLOCK_ENTITY)
					.include(DECORATED_BOX_TYPE)
				);
		});
	}
}
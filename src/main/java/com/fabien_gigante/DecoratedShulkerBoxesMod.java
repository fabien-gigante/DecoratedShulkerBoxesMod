package com.fabien_gigante;

import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class DecoratedShulkerBoxesMod implements ModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
    public static final DataComponentType<DecoratedBoxComponent> DECORATED_BOX_TYPE = DecoratedBoxComponent.TYPE;
	public static final Map<DyeColor,MenuType<ShulkerBoxMenu>> SCREEN_HANDLER_TYPES = new HashMap<>();

	// Server-side mod entry point
	@Override
	public void onInitialize() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting...");
		registerScreens();
		updateLootTables();
	}

	private void registerScreens() {
		for(DyeColor color : DyeColor.values()) {
			var type = new MenuType<>( (syncId, playerInventory) -> {
				var handler = new ShulkerBoxMenu(syncId, playerInventory);
				if (handler instanceof IDyed dyed) dyed.setColor(color);
				return handler;
			}, FeatureFlags.VANILLA_SET);
			SCREEN_HANDLER_TYPES.put(color, type);
			Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(MOD_ID, ShulkerBoxBlock.getBlockByColor(color).getDescriptionId()), type);
		}
	}

	private void updateLootTables() {
		Set<ResourceKey<LootTable>> SHULKER_BOX_LOOT_TABLES = 
			Stream.concat(Stream.of((DyeColor)null), Arrays.stream(DyeColor.values()))
			.map(color -> ShulkerBoxBlock.getBlockByColor(color).getLootTable().orElseThrow())
			.collect(Collectors.toSet());
		LootTableEvents.MODIFY.register((key, builder, source, lookup) -> {
			if (source.isBuiltin() && SHULKER_BOX_LOOT_TABLES.contains(key))
				builder.apply(
					CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
					.include(DECORATED_BOX_TYPE)
				);
		});
	}
}
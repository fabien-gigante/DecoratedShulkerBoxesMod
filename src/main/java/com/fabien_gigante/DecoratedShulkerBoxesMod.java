package com.fabien_gigante;

import java.util.Set;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class DecoratedShulkerBoxesMod implements ModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
    public static final DataComponentType<DecoratedBoxComponent> DECORATED_BOX_TYPE = DecoratedBoxComponent.TYPE;
	public static final Map<DyeColor,MenuType<ShulkerBoxMenu>> SCREEN_HANDLER_TYPES = new HashMap<>();
	
	public static final ShulkerBoxBlock[] COLORED_SHULKER_BOXES = { (ShulkerBoxBlock)Blocks.WHITE_SHULKER_BOX, (ShulkerBoxBlock)Blocks.ORANGE_SHULKER_BOX, (ShulkerBoxBlock)Blocks.MAGENTA_SHULKER_BOX, (ShulkerBoxBlock)Blocks.LIGHT_BLUE_SHULKER_BOX, (ShulkerBoxBlock)Blocks.YELLOW_SHULKER_BOX, (ShulkerBoxBlock)Blocks.LIME_SHULKER_BOX, (ShulkerBoxBlock)Blocks.PINK_SHULKER_BOX, (ShulkerBoxBlock)Blocks.GRAY_SHULKER_BOX, (ShulkerBoxBlock)Blocks.LIGHT_GRAY_SHULKER_BOX, (ShulkerBoxBlock)Blocks.CYAN_SHULKER_BOX, (ShulkerBoxBlock)Blocks.PURPLE_SHULKER_BOX, (ShulkerBoxBlock)Blocks.BLUE_SHULKER_BOX, (ShulkerBoxBlock)Blocks.BROWN_SHULKER_BOX, (ShulkerBoxBlock)Blocks.GREEN_SHULKER_BOX, (ShulkerBoxBlock)Blocks.RED_SHULKER_BOX, (ShulkerBoxBlock)Blocks.BLACK_SHULKER_BOX };

	// Server-side mod entry point
	@Override
	public void onInitialize() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting...");
		registerScreens();
		updateLootTables();
	}

	private void registerScreens() {
		for(ShulkerBoxBlock shulker : COLORED_SHULKER_BOXES) {
			var type = new MenuType<>( (syncId, playerInventory) -> {
				var handler = new ShulkerBoxMenu(syncId, playerInventory);
				if (handler instanceof Dyeable dyed) dyed.setColor(shulker.getColor());
				return handler;
			}, FeatureFlags.VANILLA_SET);
			SCREEN_HANDLER_TYPES.put(shulker.getColor(), type);
			Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(MOD_ID, shulker.getDescriptionId()), type);
		}
	}

	private void updateLootTables() {
		Set<ResourceKey<LootTable>> SHULKER_BOX_LOOT_TABLES = 
			Stream.of(COLORED_SHULKER_BOXES).map(shulker -> shulker.getLootTable().orElseThrow()).collect(Collectors.toSet());
		LootTableEvents.MODIFY.register((key, builder, source, lookup) -> {
			if (source.isBuiltin() && SHULKER_BOX_LOOT_TABLES.contains(key))
				builder.apply(
					CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
					.include(DECORATED_BOX_TYPE)
				);
		});
	}
}
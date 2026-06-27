package com.fabien_gigante;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
	
	// Server-side mod entry point
	@Override
	public void onInitialize() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting...");
		registerScreens();
		registerRecipes() ;
		updateLootTables();
	}

	private void registerRecipes() {
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(MOD_ID, "decorated_box_recipe"), DecoratedBoxRecipe.SERIALIZER);
	}

	private void registerScreens() {
		Blocks.DYED_SHULKER_BOX.forEach(block -> {
			ShulkerBoxBlock shulker = (ShulkerBoxBlock)block;
			var type = new MenuType<>( (syncId, playerInventory) -> {
				var handler = new ShulkerBoxMenu(syncId, playerInventory);
				if (handler instanceof Dyeable dyed) dyed.setColor(shulker.getColor());
				return handler;
			}, FeatureFlags.VANILLA_SET);
			SCREEN_HANDLER_TYPES.put(shulker.getColor(), type);
			Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(MOD_ID, block.getDescriptionId()), type);
		});
	}

	private void updateLootTables() {
		Set<ResourceKey<LootTable>> lootTables = new HashSet<>();
		Blocks.DYED_SHULKER_BOX.forEach(shulker -> lootTables.add(shulker.getLootTable().orElseThrow()));
		LootTableEvents.MODIFY.register((key, builder, source, lookup) -> {
			if (source.isBuiltin() && lootTables.contains(key))
				builder.apply(
					CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
					.include(DECORATED_BOX_TYPE)
				);
		});
	}
}
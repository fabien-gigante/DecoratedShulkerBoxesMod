package com.fabien_gigante;

import java.util.Set;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.loot.LootTable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class DecoratedShulkerBoxesMod implements ModInitializer {
	public static final String MOD_ID = "decorated-shulker-boxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
    public static final ComponentType<DecoratedBoxComponent> DECORATED_BOX_TYPE = DecoratedBoxComponent.TYPE;

	// Server-side mod entry point
	@Override
	public void onInitialize() {
		LOGGER.info("Decorated Shulker Boxes - Mod starting...");
		updateLootTables();
	}

	private void updateLootTables() {
		Set<RegistryKey<LootTable>> SHULKER_BOX_LOOT_TABLES = 
			Stream.concat(Stream.of((DyeColor)null), Arrays.stream(DyeColor.values()))
			.map(color -> ShulkerBoxBlock.get(color).getLootTableKey().orElseThrow())
			.collect(Collectors.toSet());
		LootTableEvents.MODIFY.register((key, builder, source, lookup) -> {
			if (source.isBuiltin() && SHULKER_BOX_LOOT_TABLES.contains(key))
				builder.apply(
					CopyComponentsLootFunction
					.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
					.include(DECORATED_BOX_TYPE)
				);    
		});
	}
}
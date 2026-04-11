package com.fabien_gigante;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DecoratedBoxRecipe extends CustomRecipe {
    public static final DecoratedBoxRecipe INSTANCE = new DecoratedBoxRecipe();
    public static final RecipeSerializer<DecoratedBoxRecipe> SERIALIZER = new RecipeSerializer<>(MapCodec.unit(INSTANCE), StreamCodec.unit(INSTANCE));
    private static final Item[] SHULKER_BY_COLOR = { Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX };

    public DecoratedBoxRecipe() {}

	// Helpers to search the recipe inputs
	private static Stream<ItemStack> find(CraftingInput input, TagKey<Item> tag) {
		return input.items().stream().filter(stack -> stack.is(tag));
	}
	private static ItemStack single(CraftingInput input, TagKey<Item> tag) {
		Iterator<ItemStack> it = find(input, tag).iterator();
        if (!it.hasNext()) return ItemStack.EMPTY;
        ItemStack result = it.next();
        return it.hasNext() ? ItemStack.EMPTY : result;
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 3) return false;
        ItemStack shulker = single(input, ItemTags.SHULKER_BOXES); 
        if (shulker.isEmpty()) return false;
		List<ItemStack> dyes = find(input, ItemTags.DYES).toList();
		return dyes.size() == 2 && dyes.get(0).getItem() != dyes.get(1).getItem();
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
        ItemStack shulker = single(input, ItemTags.SHULKER_BOXES);
        List<DyeColor> dyes = find(input, ItemTags.DYES).map(stack -> stack.get(DataComponents.DYE)).toList();
		ItemStack decorated = new ItemStack(SHULKER_BY_COLOR[dyes.get(0).getId()]);
        decorated.applyComponents(shulker.getComponentsPatch());
		new DecoratedBoxItemStack(decorated).setSecondaryColor(dyes.get(1));
		return decorated;
	}

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}

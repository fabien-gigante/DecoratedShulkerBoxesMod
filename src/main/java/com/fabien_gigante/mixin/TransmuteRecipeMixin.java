package com.fabien_gigante.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.fabien_gigante.DecoratedShulkerBoxItemStack;
import com.fabien_gigante.IDecoratedShulkerBox;

import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.TransmuteRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

@Mixin(TransmuteRecipe.class)
public class TransmuteRecipeMixin {
	@Shadow Ingredient input, material;
	@Shadow RegistryEntry<Item> result;

	// Helpers to search the recipe inventory
	private static Stream<ItemStack> search(CraftingRecipeInput input, Predicate<ItemStack> condition) {
		return input.getStacks().stream().filter(condition);
	}
	private static <T> T onlyIfElse(T a, boolean b, T c) { return b ? a : c; }
	private static ItemStack single(CraftingRecipeInput input, Predicate<ItemStack> condition) {
		Iterator<ItemStack> it = search(input,condition).iterator();
		return it.hasNext() ? onlyIfElse(it.next(), !it.hasNext(), ItemStack.EMPTY) : ItemStack.EMPTY ;
	}

	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public boolean matches(CraftingRecipeInput input, World world) {
		// Matches standard recipe
		if (single(input, (stack) -> this.input.test(stack)) == ItemStack.EMPTY) return false;
		if (single(input, (stack) -> this.material.test(stack)) == ItemStack.EMPTY) return false;
		if (input.getStackCount() == 2) return true;
		// Matches extra dye recipe
		if (input.getStackCount() != 3 || !this.result.isIn(ItemTags.SHULKER_BOXES)) return false;
		List<ItemStack> dyes = search(input, (stack) -> stack.getItem() instanceof DyeItem).toList();
		return dyes.size() == 2 && this.material.test(dyes.get(0)) && !this.material.test(dyes.get(1));
	}

	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public ItemStack craft(CraftingRecipeInput input, WrapperLookup wrapperLookup) {
		// Reproduce vanilla behavior
		ItemStack inputItem = single(input, (stack) -> this.input.test(stack));
		ItemStack craftedItem = inputItem.copyComponentsToNewStack(this.result.value(), 1);
		// Additional behavior for extra dye recipe
		if (this.result.isIn(ItemTags.SHULKER_BOXES)) {
			ItemStack extraDye = single(input, (stack) -> stack.getItem() instanceof DyeItem && !this.material.test(stack));
			DyeColor secondaryColor = extraDye.getItem() instanceof DyeItem dye ? dye.getColor() : null;
			IDecoratedShulkerBox decorated = DecoratedShulkerBoxItemStack.from(wrapperLookup, craftedItem);
			if (decorated != null) decorated.setSecondaryColor(secondaryColor);
		}
		return craftedItem;
	}
}

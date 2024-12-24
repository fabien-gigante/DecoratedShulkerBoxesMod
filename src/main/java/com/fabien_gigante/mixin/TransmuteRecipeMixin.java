package com.fabien_gigante.mixin;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

@Mixin(TransmuteRecipe.class)
public class TransmuteRecipeMixin {
	@Shadow Ingredient input, material;
	@Shadow RegistryEntry<Item> result;
	@Shadow String group;

	// Helpers to search the recipe inventory for shulker box and dyes
	@Unique
	private static Stream<ItemStack> search(CraftingRecipeInput input, Predicate<ItemStack> condition) {
		return input.getStacks().stream().filter(condition);
	}
	@Unique
	private static long count(CraftingRecipeInput input, Predicate<ItemStack> condition) {
		return search(input, condition).count();
	}
	@Unique
	private static ItemStack first(CraftingRecipeInput input, Predicate<ItemStack> condition) {
		return search(input, condition).findFirst().orElse(ItemStack.EMPTY);
	}

	private boolean canHaveExtraDye() { return group.equals("shulker_box_dye"); }

	// Match recipes with exactly 1 shulker box and exactly 1 or 2 dyes
	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public boolean matches(CraftingRecipeInput input, World world) {
		if (count(input, (stack) -> this.input.test(stack)) != 1) return false;
		if (count(input, (stack) -> this.material.test(stack)) != 1) return false;
		if (input.getStackCount() == 2) return true;
		if (!canHaveExtraDye() || input.getStackCount() != 3) return false;
		List<ItemStack> dyes = search(input, (stack) -> stack.getItem() instanceof DyeItem).toList();
		return dyes.size() == 2 && this.material.test(dyes.get(0)) && !this.material.test(dyes.get(1));
	}

	// Perform the dyed shulker box craft using the provided dyes
	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public ItemStack craft(CraftingRecipeInput input, WrapperLookup wrapperLookup) {
		// Reproduce vanilla behavior
		ItemStack inputItem = first(input, (stack) -> this.input.test(stack));
		ItemStack craftedItem = inputItem.copyComponentsToNewStack(this.result.value(), 1);

		// Additional behavior for secondary color and extra item
		if (this.canHaveExtraDye()) {
			ItemStack extraDye = first(input, (stack) -> stack.getItem() instanceof DyeItem && !this.material.test(stack));
			DyeColor secondaryColor = extraDye.getItem() instanceof DyeItem dye ? dye.getColor() : null;
			IDecoratedShulkerBox decorated = DecoratedShulkerBoxItemStack.from(wrapperLookup, craftedItem);
			if (decorated != null) decorated.setSecondaryColor(secondaryColor);
		}

		return craftedItem;
	}
}

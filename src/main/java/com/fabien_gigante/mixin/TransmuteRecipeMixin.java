package com.fabien_gigante.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import com.fabien_gigante.DecoratedBoxItemStack;

@Mixin(TransmuteRecipe.class)
public class TransmuteRecipeMixin {
	@Shadow @Final Ingredient input, material;
	@Shadow @Final TransmuteResult result;

	// Helpers to search the recipe inventory
	private static Stream<ItemStack> search(CraftingInput input, Predicate<ItemStack> condition) {
		return input.items().stream().filter(condition);
	}
	private static <T> T onlyIfElse(T a, boolean b, T c) { return b ? a : c; }
	private static ItemStack single(CraftingInput input, Predicate<ItemStack> condition) {
		Iterator<ItemStack> it = search(input,condition).iterator();
		return it.hasNext() ? onlyIfElse(it.next(), !it.hasNext(), ItemStack.EMPTY) : ItemStack.EMPTY ;
	}

	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public boolean matches(CraftingInput input, Level world) {
		// Matches standard recipe
		if (single(input, this.input::test) == ItemStack.EMPTY) return false;
		if (single(input, this.material::test) == ItemStack.EMPTY) return false;
		if (input.ingredientCount() == 2) return true;
		// Matches extra dye recipe
		if (input.ingredientCount() != 3 || !this.result.item.is(ItemTags.SHULKER_BOXES)) return false;
		List<ItemStack> dyes = search(input, (stack) -> stack.getItem() instanceof DyeItem).toList();
		return dyes.size() == 2 && this.material.test(dyes.get(0)) && !this.material.test(dyes.get(1));
	}

	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public ItemStack assemble(CraftingInput input, Provider wrapperLookup) {
		// Reproduce vanilla behavior
		ItemStack inputItem = single(input, this.input::test);
		ItemStack craftedItem = this.result.apply(inputItem);
		// Additional behavior for extra dye recipe
		if (this.result.item.is(ItemTags.SHULKER_BOXES)) {
			ItemStack extraDye = single(input, (stack) -> stack.getItem() instanceof DyeItem && !this.material.test(stack));
			DyeColor secondaryColor = extraDye.getItem() instanceof DyeItem dye ? dye.getDyeColor() : null;
			new DecoratedBoxItemStack(craftedItem).setSecondaryColor(secondaryColor);
		}
		return craftedItem;
	}
}

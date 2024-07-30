package com.fabien_gigante.mixin;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.fabien_gigante.BlockEntityExt;
import com.fabien_gigante.DecoratedShulkerBoxEntity;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

@Mixin(ShulkerBoxColoringRecipe.class)
public class ShulkerBoxColoringRecipeMixin {

    // Helpers to search the recipe inventory for shulker box and dyes
    private static Stream<ItemStack> search(CraftingRecipeInput input, Predicate<ItemStack> condition) {
        return input.getStacks().stream().filter(condition);
    }
    private static final Predicate<ItemStack> isShulkerBox = (stack) -> Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
    private static final Predicate<ItemStack> isDye = (stack) -> stack.getItem() instanceof DyeItem;
    private static final Predicate<ItemStack> isExtraItem = (stack) -> !stack.isEmpty() && !isShulkerBox.test(stack) && !isDye.test(stack);

    // Match recipes with exactly 1 shulker box,  exactly 1 or 2 dyes, and exactly 0 or 1 extra item
    /** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
    @Overwrite
    public boolean matches(CraftingRecipeInput input, World world) {
        long nShulkerBoxes = search(input, isShulkerBox).count();
        if (nShulkerBoxes != 1) return false;
        long nDyes = search(input, isDye).count();
        long nExtra = input.getStackCount() - nShulkerBoxes - nDyes ;
        return (nDyes == 1 || nDyes == 2) && (nExtra <= 1);
    }

    // Perform the dyed shulker box craft using the provided dyes and optional extra item
    /** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
    @Overwrite
    public ItemStack craft(CraftingRecipeInput input, WrapperLookup wrapperLookup) {
        ItemStack shulkerBox = search(input, isShulkerBox).findFirst().orElse(ItemStack.EMPTY);
        List<DyeColor> colors = search(input, isDye).map((stack) -> ((DyeItem) stack.getItem()).getColor()).toList();
        DyeColor primaryColor = colors.size() > 0 ? colors.get(0) : null;
        DyeColor secondaryColor = colors.size() > 1 ? colors.get(1) : null;
        ItemStack extraItem = search(input, isExtraItem).findFirst().orElse(null);

        // Reproduce vanilla behavior
        Block block = ShulkerBoxBlock.get(primaryColor);
        ItemStack dyedBox = shulkerBox.copyComponentsToNewStack(block, 1);

        // Additional behavior for secondary color and extra item
        boolean hasDecorations = (extraItem != null) || (secondaryColor != null && secondaryColor != primaryColor);
        NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(dyedBox);
        nbt = (nbt != null) ? nbt.copy() : hasDecorations ? new NbtCompound() : null;
        if (nbt != null) {
            DecoratedShulkerBoxEntity.putNbtSecondaryColor(nbt, secondaryColor != primaryColor ? secondaryColor : null);
            if (extraItem != null) DecoratedShulkerBoxEntity.putNbtDisplayedItem(nbt, extraItem);
            BlockItem.setBlockEntityData(dyedBox, BlockEntityType.SHULKER_BOX, nbt);
        }
        return dyedBox;
    }

}

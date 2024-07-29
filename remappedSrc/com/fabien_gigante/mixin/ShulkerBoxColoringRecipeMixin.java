package com.fabien_gigante.mixin;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.fabien_gigante.ShulkerBoxBlockEntityExt;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ShulkerBoxColoringRecipe.class)
public class ShulkerBoxColoringRecipeMixin {

    // Helpers to search the recipe inventory for shulker box and dyes
    private static Stream<ItemStack> search(Inventory inventory, Predicate<ItemStack> condition) {
        return IntStream.range(0, inventory.size()).mapToObj(k -> inventory.getStack(k)).filter(condition);
    }
    private static final Predicate<ItemStack> isShulkerBox = (stack) -> Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
    private static final Predicate<ItemStack> isDye = (stack) -> stack.getItem() instanceof DyeItem;

    // Match recipes with exactly 1 shulker box and exactly 1 or 2 dyes
    /** @reason using overwrite because behavior change not possible by simple code injection @author fabien **/
    @Overwrite
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        long nShulkerBoxes = search(recipeInputInventory, isShulkerBox).count();
        long nDyes = search(recipeInputInventory, isDye).count();
        return nShulkerBoxes == 1  && (nDyes == 1 || nDyes == 2);
    }

    // Perform the dyed shulker box craft using the provided dyes
    /** @reason using overwrite because behavior change not possible by simple code injection @author fabien **/
    @Overwrite
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack shulkerBox = search(recipeInputInventory, isShulkerBox).findFirst().orElse(ItemStack.EMPTY);
        List<DyeColor> dyes = search(recipeInputInventory, isDye).map((stack) -> ((DyeItem) stack.getItem()).getColor()).toList();
        DyeColor primaryDye = dyes.size() > 0 ? dyes.get(0) : DyeColor.WHITE;
        DyeColor secondaryDye = dyes.size() > 1 ? dyes.get(1) : null;

        // Reproduce vanilla behavior
        ItemStack dyedBox = ShulkerBoxBlock.getItemStack(primaryDye);
        if (shulkerBox.hasNbt()) dyedBox.setNbt(shulkerBox.getNbt().copy());

        // Additional behavior for secondary color
        NbtCompound nbt = BlockItem.getBlockEntityNbt(dyedBox);
        if (nbt != null)
            ShulkerBoxBlockEntityExt.putNbtSecondaryColor(nbt, secondaryDye);
        else if (secondaryDye != null && secondaryDye != primaryDye) {
            ShulkerBoxBlockEntity blockEntity = new ShulkerBoxBlockEntity(primaryDye, BlockPos.ORIGIN, Blocks.SHULKER_BOX.getDefaultState());
            ((ShulkerBoxBlockEntityExt) blockEntity).setSecondaryColor(secondaryDye);
            blockEntity.setStackNbt(dyedBox);
        }

        return dyedBox;
    }

}

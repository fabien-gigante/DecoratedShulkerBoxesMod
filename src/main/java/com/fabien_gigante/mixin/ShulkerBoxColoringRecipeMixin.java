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

    private static Stream<ItemStack> search(Inventory inventory, Predicate<ItemStack> condition) {
        return IntStream.range(0, inventory.size()).mapToObj(k -> inventory.getStack(k)).filter(condition);
    }

    private static final Predicate<ItemStack> isShulker = (stack) -> Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
    private static final Predicate<ItemStack> isDye = (stack) -> stack.getItem() instanceof DyeItem;

     /** @reason Allow coloring with 2 dyes @author fabien **/
    @Overwrite
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        long nShulkers = search(recipeInputInventory, isShulker).count();
        if (nShulkers != 1) return false;
        long nDyes = search(recipeInputInventory, isDye).count();
        return nDyes == 1 || nDyes == 2;
    }

    /** @reason Allow coloring with 2 dyes @author fabien **/
    @Overwrite
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack shulker = search(recipeInputInventory, isShulker).findFirst().orElse(ItemStack.EMPTY);
        List<DyeColor> dyes =  search(recipeInputInventory, isDye).limit(2).map((stack)->((DyeItem)stack.getItem()).getColor()).toList();
        DyeColor mainDye = dyes.size()>0 ? dyes.get(0) : DyeColor.WHITE;
        DyeColor secondaryDye = dyes.size()>1 ? dyes.get(1) : null;

        ItemStack craft = ShulkerBoxBlock.getItemStack(mainDye);
        if (shulker.hasNbt()) craft.setNbt(shulker.getNbt().copy());

        NbtCompound nbt = BlockItem.getBlockEntityNbt(craft);
        if (nbt != null) 
            ShulkerBoxBlockEntityExt.putNbtSecondaryColor(nbt, secondaryDye);
        else if (secondaryDye != null) {
            ShulkerBoxBlockEntity blockEntity = new ShulkerBoxBlockEntity(mainDye, BlockPos.ORIGIN, Blocks.SHULKER_BOX.getDefaultState());
            ((ShulkerBoxBlockEntityExt)blockEntity).setSecondaryColor(secondaryDye);
            blockEntity.setStackNbt(craft);
        }
        return craft;
    }

}

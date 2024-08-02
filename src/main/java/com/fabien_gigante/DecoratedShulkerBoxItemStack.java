package com.fabien_gigante;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public class DecoratedShulkerBoxItemStack implements IDecoratedShulkerBox {
    ItemStack stack;
    private DecoratedShulkerBoxItemStack(ItemStack stack) { this.stack = stack; }
    public ItemStack getItemStack() { return this.stack; }

    public static boolean isShulkerBoxItemStack(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    public static @Nullable DecoratedShulkerBoxItemStack create(ItemStack stack) {
        return isShulkerBoxItemStack(stack) ? new DecoratedShulkerBoxItemStack(stack) : null;
    }

    @Override
    public DyeColor getSecondaryColor() {
        NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(stack);
        return IDecoratedShulkerBox.getNbtSecondaryColor(nbt);
    }

    @Override
    public void setSecondaryColor(DyeColor color) {
        NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(stack);
        nbt = (nbt != null) ? nbt.copy() : (color != null) ? new NbtCompound() : null;
        if (nbt != null) {
            IDecoratedShulkerBox.putNbtSecondaryColor(nbt, color);
            BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
        }
    }

    @Override
    public ItemStack getDisplayedItem() {
        NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(stack);
        return IDecoratedShulkerBox.getNbtDisplayedItem(nbt);
    }

    @Override
    public void setDisplayedItem(ItemStack displayedItem) {
        if (displayedItem != null && displayedItem.isEmpty()) displayedItem = null;
        NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(stack);
        nbt = (nbt != null) ? nbt.copy() : (displayedItem != null) ? new NbtCompound() : null;
        if (nbt != null) {
            IDecoratedShulkerBox.putNbtDisplayedItem(nbt, displayedItem);
            BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
        }
    }

    @Override
    public boolean hasSecondaryColor() {  return getSecondaryColor()!=null; }

    @Override
    public boolean hasDisplayedItem() { return getDisplayedItem()!=null; }
}
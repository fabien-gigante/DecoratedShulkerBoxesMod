package com.fabien_gigante;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

// ItemStack wrapper for decorated shulker box item stack
public class DecoratedShulkerBoxItemStack implements IDecoratedShulkerBox {
    WrapperLookup registries;
    ItemStack stack;
    private DecoratedShulkerBoxItemStack(WrapperLookup registries, ItemStack stack) {  this.registries = registries; this.stack = stack; }
    public ItemStack getItemStack() { return this.stack; }

    
    public static boolean isShulkerBoxItemStack(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    public static @Nullable DecoratedShulkerBoxItemStack from(WrapperLookup registries, ItemStack stack) {
        return isShulkerBoxItemStack(stack) ? new DecoratedShulkerBoxItemStack(registries, stack) : null;
    }
    public static @Nullable DecoratedShulkerBoxItemStack from(World world, ItemStack stack) {
        return from(world.getRegistryManager(), stack);
    }

	@SuppressWarnings("deprecation")
	private NbtCompound getBlockEntityData() {
		NbtComponent component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
		return component != null ? component.getNbt() : null;
	}    

    public DyeColor getSecondaryColor() {
        NbtCompound nbt = getBlockEntityData();
        return IDecoratedShulkerBox.getNbtSecondaryColor(nbt);
    }

    public void setSecondaryColor(DyeColor color) {
        NbtCompound nbt = getBlockEntityData();
        nbt = (nbt != null) ? nbt.copy() : (color != null) ? new NbtCompound() : null;
        if (nbt != null) {
            IDecoratedShulkerBox.putNbtSecondaryColor(nbt, color);
            BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
        }
    }

    public ItemStack getDisplayedItem() {
        NbtCompound nbt = getBlockEntityData();
        return IDecoratedShulkerBox.getNbtDisplayedItem(this.registries, nbt);
    }

    public void setDisplayedItem(ItemStack displayedItem) {
        if (displayedItem != null && displayedItem.isEmpty()) displayedItem = null;
        NbtCompound nbt = getBlockEntityData();
        nbt = (nbt != null) ? nbt.copy() : (displayedItem != null) ? new NbtCompound() : null;
        if (nbt != null) {
            IDecoratedShulkerBox.putNbtDisplayedItem(this.registries, nbt, displayedItem);
            BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
        }
    }

    public boolean hasSecondaryColor() {  return getSecondaryColor()!=null; }

    public boolean hasDisplayedItem() { return getDisplayedItem()!=null; }

    public boolean isEmpty() {
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        return (container == null || !container.streamNonEmpty().findAny().isPresent() );
    }
}
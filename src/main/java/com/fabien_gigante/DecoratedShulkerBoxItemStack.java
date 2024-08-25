package com.fabien_gigante;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// ItemStack wrapper for decorated shulker box item stack
public class DecoratedShulkerBoxItemStack implements IDecoratedShulkerBox {
	WrapperLookup registries;
	ItemStack stack;

	private DecoratedShulkerBoxItemStack(WrapperLookup registries, ItemStack stack) {  this.registries = registries; this.stack = stack; }

	// Construction
	public static @Nullable DecoratedShulkerBoxItemStack from(WrapperLookup registries, ItemStack stack) {
		return isShulkerBox(stack) ? new DecoratedShulkerBoxItemStack(registries, stack) : null;
	}
	public static @Nullable DecoratedShulkerBoxItemStack from(World world, ItemStack stack) {
		return from(world.getRegistryManager(), stack);
	}
	public static @Nullable DecoratedShulkerBoxItemStack from(Entity entity, ItemStack stack) {
		return from(entity.getWorld(), stack);
	}

	// Return the underlying ItemStack
	public ItemStack getItemStack() { return this.stack; }

	// Helper function to check if an ItemStack is a shulker box
	public static boolean isShulkerBox(ItemStack stack) {
		return stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
	}

	// Helper method to get the nbt of the block entity data component
	@SuppressWarnings("deprecation")
	private NbtCompound getBlockEntityData() {
		NbtComponent component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
		return component != null ? component.getNbt() : null;
	}
	
	// Implements IDecoratedShulkerBox : for secondary color

	public DyeColor getColor() {
		return ShulkerBoxBlock.getColor(this.stack.getItem());
	}

	public boolean hasSecondaryColor() {  return IDecoratedShulkerBox.hasNbtSecondaryColor(getBlockEntityData()); }
	public DyeColor getSecondaryColor() { return IDecoratedShulkerBox.getNbtSecondaryColor(getBlockEntityData()); }

	public void setSecondaryColor(DyeColor color) {
		if (color == getColor()) color = null;
		NbtCompound nbt = getBlockEntityData();
		nbt = (nbt != null) ? nbt.copy() : (color != null) ? new NbtCompound() : null;
		if (nbt != null) {
			IDecoratedShulkerBox.putNbtSecondaryColor(nbt, color);
			BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
		}
	}

	// Implements IDecoratedShulkerBox : for displayed item

	public boolean hasDisplayedItem() { return IDecoratedShulkerBox.hasNbtDisplayedItem(getBlockEntityData()); }
	public ItemStack getDisplayedItem() { return IDecoratedShulkerBox.getNbtDisplayedItem(this.registries, getBlockEntityData()); }

	public void setDisplayedItem(ItemStack displayedItem) {
		if (displayedItem != null && displayedItem.isEmpty()) displayedItem = null;
		NbtCompound nbt = getBlockEntityData();
		nbt = (nbt != null) ? nbt.copy() : (displayedItem != null) ? new NbtCompound() : null;
		if (nbt != null) {
			IDecoratedShulkerBox.putNbtDisplayedItem(this.registries, nbt, displayedItem);
			BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
		}
	}

	// Implements IDecoratedShulkerBox : for inventory
	public boolean isEmpty() {
		ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
		return (container == null || !container.iterateNonEmpty().iterator().hasNext() );
	}

	// Drops the displayed item (if any) to the ground towards the player
	public void dropDisplayedItem(World world, BlockPos from, PlayerEntity player) {
		if (!hasDisplayedItem()) return;
		Vec3d vec = from.toCenterPos(), dir = player.getEyePos().subtract(vec).normalize();
		vec = vec.add(dir.multiply(.75)); dir = dir.multiply(.05).add(0,.1,0);
		var entity = new ItemEntity(world, vec.x, vec.y, vec.z, getDisplayedItem(), dir.x, dir.y, dir.z);
		entity.setToDefaultPickupDelay();
		world.spawnEntity(entity);
	}
}
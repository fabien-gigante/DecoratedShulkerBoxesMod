package com.fabien_gigante;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// ItemStack wrapper for decorated shulker box item stack
public class DecoratedBoxItemStack implements IDecoratedBox {
	private ItemStack stack;
	public DecoratedBoxItemStack(ItemStack stack) { this.stack = stack; }

	// Return the underlying ItemStack
	public ItemStack getItemStack() { return this.stack; }

	// Implements IDecorated
	public DyeColor getColor() { 
		return Block.getBlockFromItem(this.stack.getItem()) instanceof ShulkerBoxBlock shulker ? shulker.getColor() : null ;
	}
	public DecoratedBoxComponent getDecorations() { 
		return stack.getOrDefault(DecoratedBoxComponent.TYPE, DecoratedBoxComponent.DEFAULT);
	}
	public void setDecorations(DecoratedBoxComponent deco) {
		stack.set(DecoratedBoxComponent.TYPE, deco.orNull());
	}
	public boolean hasContent() {
		ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
		if (container != null) return container.iterateNonEmpty().iterator().hasNext();
		BundleContentsComponent bundle = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundle != null) return !bundle.isEmpty();
		return false;
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
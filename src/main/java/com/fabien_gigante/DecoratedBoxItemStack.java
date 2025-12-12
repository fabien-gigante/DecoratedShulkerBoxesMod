package com.fabien_gigante;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.Vec3;

// ItemStack wrapper for decorated shulker box item stack
public class DecoratedBoxItemStack implements IDecoratedBox {
	private final ItemStack stack;
	public DecoratedBoxItemStack(ItemStack stack) { this.stack = stack; }

	// Return the underlying ItemStack
	public ItemStack getItemStack() { return this.stack; }

	// Implements IDecorated
	public DyeColor getColor() { 
		return Block.byItem(this.stack.getItem()) instanceof ShulkerBoxBlock shulker ? shulker.getColor() : null ;
	}
	public DecoratedBoxComponent getDecorations() { 
		return stack.getOrDefault(DecoratedBoxComponent.TYPE, DecoratedBoxComponent.DEFAULT);
	}
	public void setDecorations(DecoratedBoxComponent decorations) {
		stack.set(DecoratedBoxComponent.TYPE, decorations.orNull());
	}
	public boolean hasContent() {
		ItemContainerContents container = stack.get(DataComponents.CONTAINER);
		if (container != null) return container.nonEmptyItems().iterator().hasNext();
		BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
		if (bundle != null) return !bundle.isEmpty();
		return false;
	}

	// Drops the displayed item (if any) to the ground towards the player
	public void dropDisplayedItem(Level world, BlockPos from, Player player) {
		if (!hasDisplayedItem()) return;
		Vec3 vec = from.getCenter(), dir = player.getEyePosition().subtract(vec).normalize();
		vec = vec.add(dir.scale(.75)); dir = dir.scale(.05).add(0,.1,0);
		var entity = new ItemEntity(world, vec.x, vec.y, vec.z, getDisplayedItem(), dir.x, dir.y, dir.z);
		entity.setDefaultPickUpDelay();
		world.addFreshEntity(entity);
	}
}
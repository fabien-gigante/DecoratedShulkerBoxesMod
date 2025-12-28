package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import com.fabien_gigante.DecoratedBoxItemStack;
import com.fabien_gigante.ISlotListener;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu implements ISlotListener {
	@Shadow @Final Container repairSlots;
	@Shadow @Final ContainerLevelAccess access;    

	protected GrindstoneMenuMixin(MenuType<?> type, int syncId) { super(type, syncId); }

	// Produce a unforged shulker box when possible
	@ModifyReturnValue(method = "computeResult", at = @At("RETURN"))
	private ItemStack modifyResult(ItemStack original, ItemStack firstInput, ItemStack secondInput) {
		if (original != ItemStack.EMPTY || !isValidShulkerBoxRecipe(firstInput, secondInput)) return original;
		ItemStack result = firstInput.copy();
		new DecoratedBoxItemStack(result).setDisplayedItem(null);
		return result;
	}

	// Allow forged shulker to be grinded (see GrindstoneScreenHandlerTopInputSlotMixin)
	public boolean isValidSlot(Slot slot, ItemStack stack) {
		return slot == this.getSlot(0) && isForgedShulkerBox(stack);
	}

	// Give back the previous decoration item to the player
	public void onTakeSlot(Player player, ItemStack stack) {
		ItemStack firstInput = repairSlots.getItem(0);
		if (isForgedShulkerBox(firstInput))
			this.access.execute((world,pos) -> new DecoratedBoxItemStack(firstInput).dropDisplayedItem(world, pos, player));
	}

	@Unique
	private boolean isForgedShulkerBox(ItemStack stack) {
		return new DecoratedBoxItemStack(stack).hasDisplayedItem();
	}

	@Unique
	private boolean isValidShulkerBoxRecipe(ItemStack firstInput, ItemStack secondInput) {
		return (secondInput == null || secondInput.isEmpty()) && isForgedShulkerBox(firstInput);
	}
}

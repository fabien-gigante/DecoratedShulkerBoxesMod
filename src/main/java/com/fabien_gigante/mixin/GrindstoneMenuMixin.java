package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.fabien_gigante.DecoratedBoxItemStack;
import com.fabien_gigante.ISlotListener;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
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
	@Unique @Final Player player;
	@Shadow @Final ContainerLevelAccess access;    

	protected GrindstoneMenuMixin(MenuType<?> type, int syncId) {
		super(type, syncId);
	}

	// Locally cache the player (as Anvil does)
	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
	private void onInit(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo info) {
		this.player = playerInventory.player;
	}

	// Produce a unforged shulker box when possible
	@Inject(method={"computeResult"}, at={@At(value="RETURN")}, cancellable = true)
	private void computeShulkerBox(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> ci) {
		ItemStack returnValue = ci.getReturnValue();
		if (returnValue != ItemStack.EMPTY) return;
		if (isValidShulkerBoxRecipe(firstInput, secondInput)) {
			returnValue = firstInput.copy();
			new DecoratedBoxItemStack(returnValue).setDisplayedItem(null);
			ci.setReturnValue(returnValue);
		} else if (isValidLodestoneTrackerRecipe(firstInput, secondInput)) {
			returnValue = firstInput.copy();
			returnValue.remove(DataComponents.LODESTONE_TRACKER);
			ci.setReturnValue(returnValue);
		}
	}

	// Allow forged shulker and lodestone compass to be grinded (see GrindstoneScreenHandlerTopInputSlotMixin)
	public boolean isValid(Slot slot, ItemStack stack) {
		return slot == this.getSlot(0) && (isForgedShulkerBox(stack) || hasLodestoneTracker(stack));
	}

	// Give back the previous decoration item to the player
	public void onTake(Player player, ItemStack stack) {
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

	@Unique
	private boolean hasLodestoneTracker(ItemStack stack) {
		return stack.get(DataComponents.LODESTONE_TRACKER) != null;
	}

	@Unique
	private boolean isValidLodestoneTrackerRecipe(ItemStack firstInput, ItemStack secondInput) {
		return (secondInput == null || secondInput.isEmpty()) && hasLodestoneTracker(firstInput);
	}	
}

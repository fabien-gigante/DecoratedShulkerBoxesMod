package com.fabien_gigante.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.DecoratedShulkerBoxItemStack;
import com.fabien_gigante.IScreenHandlerSlotListener;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler implements IScreenHandlerSlotListener {
	@Shadow @Final Inventory input;
	@Unique PlayerEntity player;
	@Shadow @Final ScreenHandlerContext context;    

	protected GrindstoneScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	// Locally cache the player (as Anvil does)
	@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
	private void onInit(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo info) {
		this.player = playerInventory.player;
	}

	// Produce a unforged shulker box when possible
	@Inject(method={"getOutputStack"}, at={@At(value="RETURN")}, cancellable = true)
	private void getOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> ci) {
		ItemStack returnValue = ci.getReturnValue();
		if (returnValue != ItemStack.EMPTY || !isValidShulkerBoxRecipe(firstInput, secondInput)) return;
		returnValue = firstInput.copy();
		var decorated = DecoratedShulkerBoxItemStack.from(player, returnValue);
		decorated.setDisplayedItem(null);
		ci.setReturnValue(returnValue);
	}

	// Allow forged shulker to be grinded (see GrindstoneScreenHandlerTopInputSlotMixin)
	public boolean isValidInput(Slot slot, ItemStack stack) {
		return slot == this.getSlot(0) && isForgedShulkerBox(stack);
	}

	// Give back the previous decoration item to the player
	public void onTakeOutput(PlayerEntity player, ItemStack stack) {
		ItemStack firstInput = input.getStack(0);
		if (isForgedShulkerBox(firstInput)) {
			DecoratedShulkerBoxItemStack shulker = DecoratedShulkerBoxItemStack.from(player, firstInput);
			if (shulker != null) this.context.run((world,pos) -> shulker.dropDisplayedItem(world, pos, player));
		}
	}

	@Unique
	private boolean isForgedShulkerBox(ItemStack stack) {
		var decorated = DecoratedShulkerBoxItemStack.from(player, stack);
		return decorated != null && decorated.hasDisplayedItem();
	}

	@Unique
	private boolean isValidShulkerBoxRecipe(ItemStack firstInput, ItemStack secondInput) {
		return (secondInput == null || secondInput.isEmpty()) && isForgedShulkerBox(firstInput);
	}
}

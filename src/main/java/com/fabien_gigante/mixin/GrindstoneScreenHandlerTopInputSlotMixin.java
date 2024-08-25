package com.fabien_gigante.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.slot.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.IScreenHandlerSlotListener;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$2")
public class GrindstoneScreenHandlerTopInputSlotMixin extends Slot {
	@Unique
	private GrindstoneScreenHandler grindstoneHandler;

	public GrindstoneScreenHandlerTopInputSlotMixin(Inventory inventory, int index, int x, int y) { super(inventory, index, x, y); }

	// Cache grindstone parent
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(GrindstoneScreenHandler grindstoneScreenHandler, Inventory inventory, int i, int j, int k, CallbackInfo ci) {
		this.grindstoneHandler = grindstoneScreenHandler;
	}

	// Grindstone parent can allow additional items as input 
	@Inject(method = "canInsert", at = @At(value = "TAIL"), cancellable=true)
	private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue( ci.getReturnValue() || ((IScreenHandlerSlotListener)grindstoneHandler).isValidInput(this, stack));
	}
}

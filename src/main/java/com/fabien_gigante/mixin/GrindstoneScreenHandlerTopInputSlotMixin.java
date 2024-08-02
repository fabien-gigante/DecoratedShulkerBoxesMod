package com.fabien_gigante.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.DecoratedShulkerBoxItemStack;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$2")
public class GrindstoneScreenHandlerTopInputSlotMixin {

    @Inject(method = "canInsert", at = @At(value = "TAIL"), cancellable=true)
    private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue( ci.getReturnValue() || isForgedShulkerBox(stack));
    }

    private static boolean isForgedShulkerBox(ItemStack stack) {
        var decorated = DecoratedShulkerBoxItemStack.create(stack);
        return decorated != null && decorated.hasDisplayedItem();
    }    
}

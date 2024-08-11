package com.fabien_gigante.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.fabien_gigante.DecoratedShulkerBoxItemStack;
import com.fabien_gigante.IScreenHandlerOutputListener;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin implements IScreenHandlerOutputListener {
    @Shadow @Final Inventory input;

    @Inject(method={"getOutputStack"}, at={@At(value="RETURN")}, cancellable = true)
    private void getOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack returnValue = ci.getReturnValue();
        if (returnValue != ItemStack.EMPTY || !isShulkerBoxRecipe(firstInput, secondInput)) return;
        returnValue = firstInput.copy();
        var decorated = DecoratedShulkerBoxItemStack.from(returnValue);
        decorated.setDisplayedItem(null);
        ci.setReturnValue(returnValue);
    }

    @Override
    public void onTakeOutput(PlayerEntity player, ItemStack stack) {
        ItemStack firstInput = input.getStack(0);
        if (isForgedShulkerBox(firstInput)) {
            DecoratedShulkerBoxItemStack shulker = DecoratedShulkerBoxItemStack.from(firstInput);
            ItemStack removedItem = shulker != null ? shulker.getDisplayedItem() : null;
            if (removedItem != null) player.dropItem(removedItem,false);
        }
    }

    private static boolean isForgedShulkerBox(ItemStack stack) {
        var decorated = DecoratedShulkerBoxItemStack.from(stack);
        return decorated != null && decorated.hasDisplayedItem();
    }

    private static boolean isShulkerBoxRecipe(ItemStack firstInput, ItemStack secondInput) {
        return (secondInput == null || secondInput.isEmpty()) && isForgedShulkerBox(firstInput);
    }
}

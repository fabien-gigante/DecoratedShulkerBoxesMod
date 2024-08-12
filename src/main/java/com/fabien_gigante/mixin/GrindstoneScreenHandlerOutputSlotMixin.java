package com.fabien_gigante.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.IScreenHandlerSlotListener;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$4")
public class GrindstoneScreenHandlerOutputSlotMixin {
    @Shadow @Final GrindstoneScreenHandler field_16780;

    // Call back to the parent (similar to what the Anvil does in vanilla)
    @Inject(method = "onTakeItem", at = @At(value = "HEAD"))
    private void onTakeItem(PlayerEntity player, ItemStack resultStack, CallbackInfo ci) {
        ((IScreenHandlerSlotListener)field_16780).onTakeOutput(player, resultStack);
    }
}

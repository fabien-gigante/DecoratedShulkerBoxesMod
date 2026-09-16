package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.DecoratedBoxComponent;
import com.fabien_gigante.DecoratedBoxItemStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.TransmuteRecipe;

@Mixin(TransmuteRecipe.class)
public abstract class TransmuteRecipeMixin  {
    @Inject(method = "computeResult", at = @At("RETURN"), cancellable = true)
    private void modifyResult(ItemStack inputIngredient, int materialCount, CallbackInfoReturnable<ItemStack> cir) {
        TransmuteRecipe self = (TransmuteRecipe)(Object)this;
        ItemStack result = cir.getReturnValue();
        if (result == null || !self.group().equals("shulker_box_dye")) return;

        DecoratedBoxItemStack decoratedBox = new DecoratedBoxItemStack(result);
        DecoratedBoxComponent decorations = decoratedBox.getDecorations();
        if (decorations != null && decorations.secondaryColor() != null) {
            decoratedBox.setDecorations(decorations.setSecondaryColor(null));
            cir.setReturnValue(decoratedBox.getItemStack());
        }
    }
}

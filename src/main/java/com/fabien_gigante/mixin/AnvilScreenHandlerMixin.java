package com.fabien_gigante.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.DecoratedShulkerBoxItemStack;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    private int repairItemUsage;
    @Shadow @Final
    private Property levelCost;
    @Shadow @Nullable
    private String newItemName;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method={"updateResult"}, at={@At(value="HEAD")}, cancellable=true)
    public void updateResult(CallbackInfo ci) {
        if (!this.isShulkerBoxRecipe()) return;
        ItemStack forged = this.input.getStack(0).copy(), ingredient = this.input.getStack(1);
        if (ingredient != null && !ingredient.isEmpty())
            DecoratedShulkerBoxItemStack.from(forged).setDisplayedItem(ingredient);
        renameItem(forged);
        this.output.setStack(0, forged);
        this.levelCost.set(1);
        this.repairItemUsage = 1;
        this.sendContentUpdates();
        ci.cancel();
    }

    public void renameItem(ItemStack stack) {
        if (this.newItemName == null || StringHelper.isBlank((String)this.newItemName))
            stack.remove(DataComponentTypes.CUSTOM_NAME);
        else if (!this.newItemName.equals(stack.getName().getString()))
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal((String)this.newItemName));
    }

    @Inject(method={"onTakeOutput"}, at={@At(value="HEAD")})
    public void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!this.isShulkerBoxRecipe()) return;
        ItemStack ingredient = this.input.getStack(1);
        if (ingredient == null || ingredient.isEmpty()) return;
        DecoratedShulkerBoxItemStack input = DecoratedShulkerBoxItemStack.from(this.input.getStack(0));
        ItemStack recovered = input != null ? input.getDisplayedItem() : null;
        if (recovered != null) player.dropItem(recovered,false);
    }

    private boolean isShulkerBoxRecipe() {
        return DecoratedShulkerBoxItemStack.isShulkerBoxItemStack(this.input.getStack(0));
    }
}

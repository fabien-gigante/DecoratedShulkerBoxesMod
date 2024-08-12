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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.DecoratedShulkerBoxItemStack;
import com.fabien_gigante.IScreenHandlerSlotListener;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler implements IScreenHandlerSlotListener {
    @Shadow
    private int repairItemUsage;
    @Shadow @Final
    private Property levelCost;
    @Shadow @Nullable
    private String newItemName;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    // Produce a decorated shulker box when possible
    @Inject(method={"updateResult"}, at={@At(value="HEAD")}, cancellable=true)
    public void updateResult(CallbackInfo ci) {
        if (!this.isValidShulkerBoxRecipe()) return;
        ItemStack forged = this.input.getStack(0).copy(), ingredient = this.input.getStack(1);
        if (ingredient != null && !ingredient.isEmpty())
            DecoratedShulkerBoxItemStack.from(this.player.getWorld(), forged).setDisplayedItem(ingredient.copyWithCount(1));
        renameItem(forged);
        this.output.setStack(0, forged);
        this.levelCost.set(1);
        this.repairItemUsage = 1;
        this.sendContentUpdates();
        ci.cancel();
    }

    @Unique
    public void renameItem(ItemStack stack) {
        if (this.newItemName == null || StringHelper.isBlank((String)this.newItemName))
            stack.remove(DataComponentTypes.CUSTOM_NAME);
        else if (!this.newItemName.equals(stack.getName().getString())) // Test not relevant if client and server have different locales ?
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal((String)this.newItemName));
    }

    // Give back the previous decoration item to the player if needed
    @Inject(method={"onTakeOutput"}, at={@At(value="HEAD")})
    public void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!this.isValidShulkerBoxRecipe()) return;
        ItemStack ingredient = this.input.getStack(1);
        if (ingredient == null || ingredient.isEmpty()) return;
        DecoratedShulkerBoxItemStack input = DecoratedShulkerBoxItemStack.from(player.getWorld(), this.input.getStack(0));
        ItemStack recovered = input != null ? input.getDisplayedItem() : null;
        if (recovered != null) player.dropItem(recovered,false);
    }

    @Unique
    private boolean isValidShulkerBoxRecipe() {
        ItemStack forged = this.input.getStack(0), ingredient = this.input.getStack(1);
        if (!DecoratedShulkerBoxItemStack.isShulkerBoxItemStack(forged)) return false;
        // Avoid some forms of recursion
        var decoration = DecoratedShulkerBoxItemStack.from(player.getWorld(), ingredient);
        return decoration == null || (!decoration.hasDisplayedItem() && decoration.isEmpty());
    }
}

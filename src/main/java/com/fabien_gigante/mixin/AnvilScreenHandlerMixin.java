package com.fabien_gigante.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

import com.fabien_gigante.BlockEntityExt;
import com.fabien_gigante.DecoratedShulkerBoxEntity;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    private int repairItemUsage;
    @Shadow
    @Final
    private Property levelCost;
    @Shadow
    @Nullable
    private String newItemName;
    @Unique
    private ItemStack removedItem;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method={"updateResult"}, at={@At(value="HEAD")}, cancellable=true)
    public void updateResult(CallbackInfo ci) {
        if (!this.isShulkerBoxRecipe()) return;
        ItemStack forgedShulkerBox = this.input.getStack(0).copy();
        ItemStack displayedItem = this.input.getStack(1);
        if (displayedItem.isEmpty()) displayedItem = null;
        NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(forgedShulkerBox);
        nbt = (nbt != null) ? nbt.copy() : (displayedItem != null) ? new NbtCompound() : null;
        if (nbt != null) {
            removedItem = DecoratedShulkerBoxEntity.getNbtDisplayedItem(nbt);
            DecoratedShulkerBoxEntity.putNbtDisplayedItem(nbt, displayedItem);
            BlockItem.setBlockEntityData(forgedShulkerBox, BlockEntityType.SHULKER_BOX, nbt);
        } else removedItem = null;
        if (this.newItemName == null || StringHelper.isBlank((String)this.newItemName))
            forgedShulkerBox.remove(DataComponentTypes.CUSTOM_NAME);
        else if (!this.newItemName.equals(forgedShulkerBox.getName().getString()))
            forgedShulkerBox.set(DataComponentTypes.CUSTOM_NAME, Text.literal((String)this.newItemName));
        this.output.setStack(0, forgedShulkerBox);
        this.levelCost.set(1);
        this.repairItemUsage = 1;
        this.sendContentUpdates();
        ci.cancel();
    }

    @Inject(method={"onTakeOutput"}, at={@At(value="HEAD")})
    public void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        //if (this.isShulkerBoxRecipe()) this.input.getStack(1).increment(1);
        if (this.isShulkerBoxRecipe() && removedItem != null) 
            player.dropItem(removedItem,false);
    }

    private boolean isShulkerBoxRecipe() {
        Item item = this.input.getStack(0).getItem();
        return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }
}

package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.DecoratedBoxItemStack;
import com.fabien_gigante.SlotExtendable;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu {
	@Shadow @Final Container repairSlots;
	@Shadow @Final ContainerLevelAccess access;    

	protected GrindstoneMenuMixin(MenuType<?> type, int syncId) { super(type, syncId); }

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",  at=@At("TAIL"))
	private void onInit(int syncId, Inventory inventory, ContainerLevelAccess access, CallbackInfo ci) {
		((SlotExtendable)this.getSlot(0)).allowItemCondition(stack -> isForgedShulkerBox(stack));
		((SlotExtendable)this.getSlot(2)).registerItemTakenAction((player, stack) -> onTakeOutputSlot(player, stack));
	}

	// Produce a unforged shulker box when possible
	@ModifyReturnValue(method = "computeResult", at = @At("RETURN"))
	private ItemStack modifyResult(ItemStack original, ItemStack firstInput, ItemStack secondInput) {
		if (original != ItemStack.EMPTY || !isValidShulkerBoxRecipe(firstInput, secondInput)) return original;
		ItemStack result = firstInput.copy();
		new DecoratedBoxItemStack(result).setDisplayedItem(null);
		return result;
	}

	// Give back the previous decoration item to the player
	@Unique
	public void onTakeOutputSlot(Player player, ItemStack stack) {
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
}

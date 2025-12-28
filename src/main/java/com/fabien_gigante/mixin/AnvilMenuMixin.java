package com.fabien_gigante.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.fabien_gigante.DecoratedBoxItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
	@Shadow	private int repairItemCountCost;
	@Shadow @Final private DataSlot cost;
	@Shadow @Nullable private String itemName;

	protected AnvilMenuMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
		super(type, syncId, playerInventory, context, forgingSlotsManager);
	}
   
	// Produce a decorated shulker box when possible
	@Inject(method={"createResult"}, at={@At(value="HEAD")}, cancellable=true)
	public void createShulkerBox(CallbackInfo ci) {
		if (!this.isValidShulkerBoxRecipe()) return;
		ItemStack forged = this.inputSlots.getItem(0).copy(), ingredient = this.inputSlots.getItem(1);
		if (ingredient != null && !ingredient.isEmpty())
			new DecoratedBoxItemStack(forged).setDisplayedItem(ingredient.copyWithCount(1));
		renameItem(forged, ingredient);
		this.resultSlots.setItem(0, forged);
		this.cost.set(1);
		this.repairItemCountCost = 1;
		this.broadcastChanges();
		ci.cancel();
	}

	@Unique
	public void renameItem(ItemStack stack, ItemStack ingredient) {
		boolean hasEmptyName = this.itemName == null || StringUtil.isBlank((String)this.itemName);
		if (hasEmptyName) stack.remove(DataComponents.CUSTOM_NAME);
		else stack.set(DataComponents.CUSTOM_NAME, Component.literal((String)this.itemName));
	}

	// Give back the previous decoration item to the player if needed
	@Inject(method={"onTake"}, at={@At(value="HEAD")})
	public void onTakeShulkerBox(Player player, ItemStack stack, CallbackInfo ci) {
		if (!this.isValidShulkerBoxRecipe()) return;
		ItemStack ingredient = this.inputSlots.getItem(1);
		if (ingredient == null || ingredient.isEmpty()) return;
		DecoratedBoxItemStack decorated = new DecoratedBoxItemStack(this.inputSlots.getItem(0));
		this.access.execute((world,pos) -> decorated.dropDisplayedItem(world, pos, player));
	}

	@Unique
	private boolean isValidShulkerBoxRecipe() {
		ItemStack forged = this.inputSlots.getItem(0), ingredient = this.inputSlots.getItem(1);
		if (!forged.is(ItemTags.SHULKER_BOXES)) return false;
		// Avoid nested storage
		var decorated = new DecoratedBoxItemStack(ingredient);
		return !decorated.hasDisplayedItem() && !decorated.hasContent();
	}
}

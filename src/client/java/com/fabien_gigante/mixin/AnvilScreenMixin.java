package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends AbstractContainerScreen<AnvilMenu> {
    public AnvilScreenMixin(AnvilMenu abstractContainerMenu, Inventory inventory, Component component) { super(abstractContainerMenu, inventory, component); }

    @Shadow private EditBox name;
    String shulkerBoxName = "", displayedItemName = "";

    @Inject(method="slotChanged", at=@At("HEAD"), cancellable = true)
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack, CallbackInfo ci) {
        if (i == 0)
            this.shulkerBoxName = itemStack.is(ItemTags.SHULKER_BOXES) && !itemStack.has(DataComponents.CUSTOM_NAME) ? itemStack.getHoverName().getString() : "";
        if (i == 1)
            this.displayedItemName = itemStack.isEmpty() ? "" : itemStack.getHoverName().getString();
        if ((i == 0 || i == 1) && !this.shulkerBoxName.isEmpty() && this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
            String name = this.displayedItemName.isEmpty() ? this.shulkerBoxName : Component.translatable("decorated.shulkerbox.name", this.displayedItemName).getString();
            this.name.setValue(name);
            this.name.setEditable(true);
            this.setFocused(this.name);
            ci.cancel();
        }
    }
}
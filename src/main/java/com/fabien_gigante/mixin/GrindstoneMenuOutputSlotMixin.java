package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.fabien_gigante.ISlotListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = "net/minecraft/world/inventory/GrindstoneMenu$4")
public class GrindstoneMenuOutputSlotMixin {
	@Shadow @Final GrindstoneMenu field_16780;

	// Call back to the parent (similar to what the Anvil does in vanilla)
	@Inject(method = "onTake", at = @At(value = "HEAD"))
	private void onTake(Player player, ItemStack resultStack, CallbackInfo ci) {
		((ISlotListener)field_16780).onTakeOutput(player, resultStack);
	}
}

package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.component.type.MapIdComponent;

@Mixin(ItemFrameEntityRenderer.class)
public class ItemFrameEntityRendererMixin {
	// Item frames "out of the world" should render a filled map as the map item itself
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;getMapId(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/component/type/MapIdComponent;"))
	private MapIdComponent getMapId(ItemFrameEntity itemFrameEntity, ItemStack itemStack) {
		return itemFrameEntity.getWorld() == null ? null : itemFrameEntity.getMapId(itemStack);
	}
}

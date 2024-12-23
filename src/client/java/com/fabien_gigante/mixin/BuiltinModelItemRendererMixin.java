package com.fabien_gigante.mixin;

import com.fabien_gigante.IDecoratedShulkerBox;
import net.minecraft.client.MinecraftClient;
import com.fabien_gigante.DecoratedShulkerBoxItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
	// Vanilla item renderer delegates shulker box item rending to the shulker box block entity renderer
	// Slightly override this logic to set the decorations (secondary color and displayed item) on the block entity first
	@SuppressWarnings("resource")
	@Inject(
		method = "render", 
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z", shift = At.Shift.BEFORE), 
		locals = LocalCapture.CAPTURE_FAILHARD)
	private void decorateShulkerBox(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci, Item item, Block block, BlockEntity blockEntity) {
		if (blockEntity instanceof ShulkerBoxBlockEntity) {
			blockEntity.readComponents(stack); // only needed for fallback implementation (see ShulkerBoxBlockEntityMixin.getDisplayedItem)
			IDecoratedShulkerBox source = DecoratedShulkerBoxItemStack.from(MinecraftClient.getInstance().world, stack);
			((IDecoratedShulkerBox)blockEntity).setDecorations(source);
		}
	}
}

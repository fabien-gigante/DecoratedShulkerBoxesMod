package com.fabien_gigante.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.joml.Math;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.IDecoratedShulkerBox;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class ShulkerBoxBlockEntityRendererMixin {
	@Shadow
	private ShulkerEntityModel<?> model;

	@Unique
	private static ItemFrameEntity ITEM_FRAME_ENTITY = new ItemFrameEntity(null, BlockPos.ORIGIN, Direction.DOWN);
	static { ITEM_FRAME_ENTITY.setSilent(true); ITEM_FRAME_ENTITY.setInvisible(true); }

 	// Redirect the model rendering to only render the lid (and not the entire model)
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
	private void renderLid(ShulkerEntityModel<?> model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		model.getLid().render(matrices, vertices, light, overlay);
	}
	
	// Then render the base separately, using the secondary color
    @Inject(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", shift=At.Shift.AFTER))
	private void renderBase(ShulkerBoxBlockEntity shulker, float f, MatrixStack matrices, VertexConsumerProvider provider, int i, int j, CallbackInfo ci) {
		DyeColor secondaryColor = ((IDecoratedShulkerBox) shulker).getSecondaryColor();
		SpriteIdentifier texture = secondaryColor != null
				? TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getId())
				: TexturedRenderLayers.SHULKER_TEXTURE_ID;
		VertexConsumer vertexConsumer = texture.getVertexConsumer(provider, RenderLayer::getEntityCutoutNoCull);
		ModelPart base = this.model.getParts().iterator().next();
		base.render(matrices, vertexConsumer, i, j);
	}

	// Finally, render the displayed item on top
	@Inject(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", shift=At.Shift.AFTER))
    private void renderDisplayedItem(ShulkerBoxBlockEntity shulker, float f, MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, CallbackInfo ci) {
		ItemStack stack = ((IDecoratedShulkerBox)shulker).getDisplayedItem();
        if (stack == null) return;
		ITEM_FRAME_ENTITY.setHeldItemStack(stack, false);
		matrices.push();
		float λ = shulker.getAnimationProgress(f);
		float yOffset = 7f / 16f - λ / 2f;
		matrices.translate(0, yOffset, 0);
		if (!shulker.hasWorld()) matrices.scale(1.5f, 1.5f, 1.5f);
		matrices.multiply(new Quaternionf().rotationY(1.5f * (float)Math.PI * λ));
		MinecraftClient.getInstance().getEntityRenderDispatcher().render(ITEM_FRAME_ENTITY, 0.0, 0.0, 0.0, 0, f, matrices, provider, light);
		matrices.pop();
    }

}

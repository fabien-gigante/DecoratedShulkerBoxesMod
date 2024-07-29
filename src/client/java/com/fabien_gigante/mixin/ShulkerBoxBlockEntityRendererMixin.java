package com.fabien_gigante.mixin;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.SecondaryColorExt;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class ShulkerBoxBlockEntityRendererMixin {
	@Shadow
	private ShulkerEntityModel<?> model;

	// Redirect the model rendering to only render the lid (and not the entire model)
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
	private void renderLid(ShulkerEntityModel<?> model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		model.getLid().render(matrices, vertices, light, overlay);
	}
	
	// Then render the base separately, using the secondary color
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
	private void renderBase(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
		DyeColor secondaryColor = ((SecondaryColorExt) shulkerBoxBlockEntity).getSecondaryColor();
		SpriteIdentifier texture = secondaryColor != null
				? TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getId())
				: TexturedRenderLayers.SHULKER_TEXTURE_ID;
		VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
		ModelPart base = this.model.getParts().iterator().next();
		base.render(matrixStack, vertexConsumer, i, j);
	}


}

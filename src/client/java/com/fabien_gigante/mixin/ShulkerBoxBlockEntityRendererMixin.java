package com.fabien_gigante.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;

import org.joml.Math;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.DecoratedShulkerBoxEntity;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class ShulkerBoxBlockEntityRendererMixin {
	@Shadow
	private ShulkerEntityModel<?> model;

    @Inject(method="render", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", shift=At.Shift.AFTER))
    private void renderPost(ShulkerBoxBlockEntity shulker, float partialTick, MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, CallbackInfo ci) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
		ItemStack stack = ((DecoratedShulkerBoxEntity)shulker).getDisplayedItem();
		shulker.getCustomName();

        if (stack != null) {
            ItemFrameEntity entity = new ItemFrameEntity((World)minecraft.world, shulker.getPos(), Direction.DOWN);
            entity.setHeldItemStack(stack, false);
            entity.setInvisible(true);
            matrices.push();
			float t = shulker.getAnimationProgress(partialTick);
            float yOffset = 0.4375f - t / 2.0f;
            if (stack.isOf(Items.FILLED_MAP)) yOffset += 0.039f;
			matrices.translate(0,yOffset, 0);
		 	if (!shulker.hasWorld()) matrices.scale(1.5f, 1.5f, 1.5f);
			matrices.multiply(new Quaternionf().rotationY(1.5f * (float)Math.PI * t));
            minecraft.getEntityRenderDispatcher().render((Entity)entity, 0.0, 0.0, 0.0, 0, partialTick, matrices, provider, light);
            matrices.pop();
		}
    }

	// Redirect the model rendering to only render the lid (and not the entire model)
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
	private void renderLid(ShulkerEntityModel<?> model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		model.getLid().render(matrices, vertices, light, overlay);
	}
	
	// Then render the base separately, using the secondary color
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
	private void renderBase(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
		DyeColor secondaryColor = ((DecoratedShulkerBoxEntity) shulkerBoxBlockEntity).getSecondaryColor();
		SpriteIdentifier texture = secondaryColor != null
				? TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getId())
				: TexturedRenderLayers.SHULKER_TEXTURE_ID;
		VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
		ModelPart base = this.model.getParts().iterator().next();
		base.render(matrixStack, vertexConsumer, i, j);
	}

}

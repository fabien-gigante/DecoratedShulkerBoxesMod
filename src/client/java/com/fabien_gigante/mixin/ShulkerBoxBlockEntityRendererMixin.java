package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import com.fabien_gigante.DecoratedShulkerBoxesModClient;
import com.fabien_gigante.IDecoratedBox;
import com.fabien_gigante.IDecoratedShulkerBoxBlockEntityRenderer;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class ShulkerBoxBlockEntityRendererMixin implements IDecoratedShulkerBoxBlockEntityRenderer {
	@Shadow @Final private ShulkerBoxBlockModel model;
	private ModelPart lid, base;

	@Inject(method="<init>(Lnet/minecraft/client/render/entity/model/LoadedEntityModels;)V", at=@At("TAIL"))
	private void onInit(LoadedEntityModels models, CallbackInfo ci) {
		DecoratedShulkerBoxesModClient.LOGGER.info("ShulkerBoxBlockEntityRenderer.onInit()");
		ModelPart root = this.model.getRootPart();
		this.lid = root.getChild("lid");
		this.base = root.getChild("base");
	}

	private void renderModel(MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, float openness, SpriteIdentifier lidId, SpriteIdentifier baseId) {
		this.model.animateLid(openness);
		this.lid.render(matrices, lidId.getVertexConsumer(provider, this.model::getLayer), light, overlay);
		this.base.render(matrices, baseId.getVertexConsumer(provider, this.model::getLayer), light, overlay);
	}

	private void renderDisplayed(MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, float openness, ItemStack displayed, boolean zoomed) {
		if (displayed == null) return;
		matrices.translate(0, 7.75f / 16f - openness / 2f, 0);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180+270 * openness));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
		float scale = zoomed ? 0.75f : 2f/3f; // for comparaison, .5f is the scale used by item frame
		matrices.scale(scale, scale, scale);
		MinecraftClient client = MinecraftClient.getInstance();
		client.getItemRenderer().renderItem(displayed, ItemDisplayContext.FIXED, light, overlay, matrices, provider, client.world, 0);
	}

	private void render(MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, Direction facing, float openness, SpriteIdentifier lidId, SpriteIdentifier baseId, ItemStack displayed, boolean zoomed) {
		matrices.push();
		matrices.translate(0.5f, 0.5f, 0.5F);
		matrices.scale(0.9995f, 0.9995f, 0.9995f);
		matrices.multiply(facing.getRotationQuaternion());
		matrices.scale(1f, -1f, -1f);
		matrices.translate(0f, -1f, 0f);
		this.renderModel(matrices, provider, light, overlay, openness, lidId, baseId);
		this.renderDisplayed(matrices, provider, light, overlay, openness, displayed, zoomed);
		matrices.pop();
	}

	/** @reason using overwrite because behavior change not easy by simple code injection @author fabien **/
	@Overwrite
	public void render(ShulkerBoxBlockEntity shulker, float delta, MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, Vec3d vec3d) {
		Direction facing = (Direction)shulker.getCachedState().get(ShulkerBoxBlock.FACING, Direction.UP);
		DyeColor dyeColor = shulker.getColor();
		SpriteIdentifier lidId = dyeColor == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.getShulkerBoxTextureId(dyeColor);
		DyeColor secondaryColor = shulker instanceof IDecoratedBox decorated ? decorated.getSecondaryColor() : null;
		SpriteIdentifier baseId = secondaryColor == null ? lidId : TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getIndex());
		float openness = shulker.getAnimationProgress(delta);
		ItemStack displayed = shulker instanceof IDecoratedBox decorated ? decorated.getDisplayedItem() : null;
		this.render(matrices, provider, light, overlay, facing, openness, lidId, baseId, displayed, false);
	}

	// IDecoratedShulkerBoxBlockEntityRenderer
	public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, Direction facing, float openness, SpriteIdentifier lidId, SpriteIdentifier baseId, ItemStack displayed) {
		this.render(matrices, provider, light, overlay, facing, openness, lidId, baseId, displayed, true);
	}
}
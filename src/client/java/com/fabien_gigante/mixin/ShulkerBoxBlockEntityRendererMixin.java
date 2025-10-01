package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel;
import net.minecraft.client.render.block.entity.state.ShulkerBoxBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import com.fabien_gigante.DecoratedShulkerBoxBlockModel;
import com.fabien_gigante.IDecoratedBox;
import com.fabien_gigante.IDecoratedShulkerBoxBlockEntityRenderer;
import com.fabien_gigante.DecoratedShulkerBoxBlockEntityRenderState;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class ShulkerBoxBlockEntityRendererMixin implements IDecoratedShulkerBoxBlockEntityRenderer {
	@Shadow @Final private ShulkerBoxBlockModel model;
	@Shadow @Final private SpriteHolder materials;
    @Final private ItemModelManager itemModelManager;

	@Shadow private void setTransforms(MatrixStack matrices, Direction facing, float openness) {}

	@Inject(method="<init>(Lnet/minecraft/client/render/block/entity/BlockEntityRendererFactory$Context;)V", at=@At("TAIL"))
	private void onInit1(BlockEntityRendererFactory.Context context, CallbackInfo ci) {
		this.itemModelManager = context.itemModelManager();
	}

	@Inject(method="<init>(Lnet/minecraft/client/render/item/model/special/SpecialModelRenderer$BakeContext;)V", at=@At("TAIL"))
	private void onInit2(SpecialModelRenderer.BakeContext context, CallbackInfo ci) {
		this.itemModelManager = MinecraftClient.getInstance().getItemModelManager();
	}

	@Redirect(method = "<init>(Lnet/minecraft/client/render/entity/model/LoadedEntityModels;Lnet/minecraft/client/texture/SpriteHolder;)V",
			  at = @At(value = "NEW", target = "net/minecraft/client/render/block/entity/ShulkerBoxBlockEntityRenderer$ShulkerBoxBlockModel") )
	private ShulkerBoxBlockModel createModel(ModelPart part) {
		return new DecoratedShulkerBoxBlockModel(part);
	}

	/** @reason intended @author fabien **/
	@Overwrite
	public ShulkerBoxBlockEntityRenderState createRenderState() {
		return new DecoratedShulkerBoxBlockEntityRenderState();
	}

	@Inject(method="updateRenderState", at=@At("TAIL"))
	public void updateRenderState(ShulkerBoxBlockEntity shulker, ShulkerBoxBlockEntityRenderState shulkerState, float f, Vec3d vec3d, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, CallbackInfo ci) {
		DecoratedShulkerBoxBlockEntityRenderState state = (DecoratedShulkerBoxBlockEntityRenderState)shulkerState;
		state.secondaryColor = shulker instanceof IDecoratedBox decorated ? decorated.getSecondaryColor() : null;
		ItemStack stack = shulker instanceof IDecoratedBox decorated ? decorated.getDisplayedItem() : null;
       	this.itemModelManager.clearAndUpdate(state.itemRenderState, stack == null ? ItemStack.EMPTY : stack, ItemDisplayContext.FIXED, shulker.getWorld(), null, 0);
	}

	private void renderModel(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, float openness, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int tintedColor, SpriteIdentifier lidId, SpriteIdentifier baseId) {
		queue.submitModel(this.model, openness, matrices, lidId.getRenderLayer(model::getLayer), light, overlay, -1, this.materials.getSprite(lidId), tintedColor, crumblingOverlay);
		queue.submitModel(this.model, Float.NaN, matrices, baseId.getRenderLayer(model::getLayer), light, overlay, -1, this.materials.getSprite(baseId), tintedColor, crumblingOverlay);
	}

	private void renderDisplayed(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, float openness, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int tintedColor, ItemRenderState itemRenderState, boolean zoomed) {
		matrices.translate(0, 7.75f / 16f - openness / 2f, 0);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180+270 * openness));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
		float scale = zoomed ? 0.75f : 2f/3f; // for comparaison, .5f is the scale used by item frame
		matrices.scale(scale, scale, scale);
		itemRenderState.render(matrices, queue, light, overlay, tintedColor);
	}

	private void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Direction facing, float openness, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i, SpriteIdentifier lidId, SpriteIdentifier baseId, ItemRenderState itemRenderState, boolean zoomed) {
		matrices.push();
		this.setTransforms(matrices, facing, openness);
		this.renderModel(matrices, queue, light, overlay, openness, crumblingOverlay, i, lidId, baseId);
		this.renderDisplayed(matrices, queue, light, overlay, openness, crumblingOverlay, i, itemRenderState, zoomed);
		matrices.pop();
	}

	/** @reason simplest way @author fabien **/
	@Overwrite
	public void render(ShulkerBoxBlockEntityRenderState shulkerState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
		DecoratedShulkerBoxBlockEntityRenderState state = (DecoratedShulkerBoxBlockEntityRenderState)shulkerState;
		SpriteIdentifier lidId = state.dyeColor == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.getShulkerBoxTextureId(state.dyeColor);
		SpriteIdentifier baseId = state.secondaryColor == null ? lidId : TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(state.secondaryColor.getIndex());
		this.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, state.facing, state.animationProgress, state.crumblingOverlay, 0, lidId, baseId, state.itemRenderState, false);
	}

	// IDecoratedShulkerBoxBlockEntityRenderer
	public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Direction facing, float openness, int tintedColor, SpriteIdentifier lidId, SpriteIdentifier baseId, ItemStack displayed) {
		ItemRenderState itemRenderState = new ItemRenderState();
       	this.itemModelManager.clearAndUpdate(itemRenderState, displayed == null ? ItemStack.EMPTY : displayed, ItemDisplayContext.FIXED, null, null, 0);
		this.render(matrices, queue, light, overlay, facing, openness, null, tintedColor, lidId, baseId, itemRenderState, true);
	}
}
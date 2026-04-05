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
import com.fabien_gigante.DecoratedBoxModel;
import com.fabien_gigante.Decorable;
import com.fabien_gigante.DecoratedBoxRenderable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer.ShulkerBoxModel;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import com.fabien_gigante.DecoratedBoxRenderState;

@Mixin(ShulkerBoxRenderer.class)
public abstract class ShulkerBoxRendererMixin implements DecoratedBoxRenderable {
	@Shadow @Final private ShulkerBoxModel model;
	@Shadow @Final private SpriteGetter sprites;
    @Final private ItemModelResolver itemModelManager;

	@Inject(method="<init>(Lnet/minecraft/client/renderer/blockentity/BlockEntityRendererProvider$Context;)V", at=@At("TAIL"))
	private void onInit1(BlockEntityRendererProvider.Context context, CallbackInfo ci) {
		this.itemModelManager = context.itemModelResolver();
	}

	@Inject(method="<init>(Lnet/minecraft/client/renderer/special/SpecialModelRenderer$BakingContext;)V", at=@At("TAIL"))
	private void onInit2(SpecialModelRenderer.BakingContext context, CallbackInfo ci) {
		this.itemModelManager = Minecraft.getInstance().getItemModelResolver();
	}

	@Redirect(method = "<init>(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;)V",
			  at = @At(value = "NEW", target = "net/minecraft/client/renderer/blockentity/ShulkerBoxRenderer$ShulkerBoxModel") )
	private ShulkerBoxModel createModel(ModelPart part) {
		return new DecoratedBoxModel(part);
	}

	/** @reason intended @author fabien **/
	@Overwrite
	public ShulkerBoxRenderState createRenderState() { return new DecoratedBoxRenderState();}

	@Inject(method="extractRenderState", at=@At("TAIL"))
	public void extractDecoratedState(ShulkerBoxBlockEntity shulker, ShulkerBoxRenderState shulkerState, float f, Vec3 vec3d, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlayCommand, CallbackInfo ci) {
		DecoratedBoxRenderState state = (DecoratedBoxRenderState)shulkerState;
		state.secondaryColor = shulker instanceof Decorable decorated ? decorated.getSecondaryColor() : null;
		ItemStack stack = shulker instanceof Decorable decorated ? decorated.getDisplayedItem() : null;
       	this.itemModelManager.updateForTopItem(state.itemRenderState, stack == null ? ItemStack.EMPTY : stack, ItemDisplayContext.FIXED, shulker.getLevel(), null, 0);
	}

	private void submitModel(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, float openness, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int tintedColor, SpriteId lidId, SpriteId baseId) {
		queue.submitModel(this.model, openness, matrices, lidId.renderType(model::renderType), light, overlay, -1, this.sprites.get(lidId), tintedColor, crumblingOverlay);
		queue.submitModel(this.model, Float.NaN, matrices, baseId.renderType(model::renderType), light, overlay, -1, this.sprites.get(baseId), tintedColor, crumblingOverlay);
	}

	private void submitDisplayed(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, float openness, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int tintedColor, ItemStackRenderState itemRenderState, boolean zoomed) {
		matrices.translate(0, 7.75f / 16f - openness / 2f, 0);
		matrices.mulPose(Axis.YP.rotationDegrees(180+270 * openness));
		matrices.mulPose(Axis.XP.rotationDegrees(-90));
		float scale = zoomed ? 0.75f : 2f/3f; // for comparaison, .5f is the scale used by item frame
		matrices.scale(scale, scale, scale);
		itemRenderState.submit(matrices, queue, light, overlay, tintedColor);
	}

	private void submit(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, Direction facing, float openness, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int i, SpriteId lidId, SpriteId baseId, ItemStackRenderState itemRenderState, boolean zoomed) {
		matrices.pushPose();
     	if (facing != null) matrices.mulPose(ShulkerBoxRenderer.modelTransform(facing));
		this.model.setupAnim(openness);
		this.submitModel(matrices, queue, light, overlay, openness, crumblingOverlay, i, lidId, baseId);
		this.submitDisplayed(matrices, queue, light, overlay, openness, crumblingOverlay, i, itemRenderState, zoomed);
		matrices.popPose();
	}

	/** @reason simplest way @author fabien **/
	@Overwrite
	public void submit(ShulkerBoxRenderState shulkerState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState) {
		DecoratedBoxRenderState state = (DecoratedBoxRenderState)shulkerState;
		SpriteId lidId = state.color == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.getShulkerBoxSprite(state.color);
		SpriteId baseId = state.secondaryColor == null ? lidId : Sheets.SHULKER_TEXTURE_LOCATION.get(state.secondaryColor.getId());
		this.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, state.direction, state.progress, state.breakProgress, 0, lidId, baseId, state.itemRenderState, false);
	}

	@Override // implements DecoratedBoxRenderable
	public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, float openness, int tintedColor, SpriteId lidId, SpriteId baseId, ItemStack displayed) {
		ItemStackRenderState itemRenderState = new ItemStackRenderState();
       	this.itemModelManager.updateForTopItem(itemRenderState, displayed == null ? ItemStack.EMPTY : displayed, ItemDisplayContext.FIXED, null, null, 0);
		this.submit(matrices, queue, light, overlay, null, openness, null, tintedColor, lidId, baseId, itemRenderState, true);
	}
}

/*

Mixin apply for mod decorated-shulker-boxes failed DecoratedShulkerBoxesMod.client.mixins.json:ShulkerBoxRendererMixin from mod decorated-shulker-boxes 
-> net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer: org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException Critical injection failure:
 @Redirect annotation on createModel could not find any targets matching '<init>(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/MaterialSet;)V' in net/minecraft/client/renderer/blockentity/ShulkerBoxRenderer. No refMap loaded. [INJECT_PREPARE Applicator Phase -> DecoratedShulkerBoxesMod.client.mixins.json:ShulkerBoxRendererMixin from mod decorated-shulker-boxes -> Prepare Injections -> redirect$baj000$decorated-shulker-boxes$createModel(Lnet/minecraft/client/model/geom/ModelPart;)Lnet/minecraft/client/renderer/blockentity/ShulkerBoxRenderer$ShulkerBoxModel; -> Parse ->  -> Validate Targets]

*/
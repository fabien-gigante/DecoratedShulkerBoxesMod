package com.fabien_gigante.mixin;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.fabien_gigante.DecoratedBoxItemStack;
import com.fabien_gigante.IDecoratedBox;
import com.fabien_gigante.IDecoratedShulkerBoxBlockEntityRenderer;

@Mixin(ShulkerBoxModelRenderer.class)
public abstract class ShulkerBoxModelRendererMixin implements SimpleSpecialModelRenderer {
    @Shadow
	private ShulkerBoxBlockEntityRenderer blockEntityRenderer;
    @Shadow
	private float openness;
    @Shadow
	private Direction orientation;
    @Shadow
	private SpriteIdentifier textureId;

    private SpriteIdentifier secondaryId;
    private ItemStack displayedItem;

    @Override
	public Void getData(ItemStack stack) {
        IDecoratedBox decorated = new DecoratedBoxItemStack(stack);
		DyeColor secondaryColor = decorated.getSecondaryColor();
		this.secondaryId = secondaryColor == null ? textureId : TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getId());
        this.displayedItem = decorated.getDisplayedItem();
        return null;
	}

    @Override
	public void render(ModelTransformationMode modelTransformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
        IDecoratedShulkerBoxBlockEntityRenderer renderer = (IDecoratedShulkerBoxBlockEntityRenderer)this.blockEntityRenderer;
        renderer.render(matrices, vertexConsumers, light, overlay, this.orientation, this.openness, this.textureId, this.secondaryId, this.displayedItem);
	}
}
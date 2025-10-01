package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import com.fabien_gigante.DecoratedBoxItemStack;
import com.fabien_gigante.IDecoratedBox;
import com.fabien_gigante.IDecoratedShulkerBoxBlockEntityRenderer;

@Mixin(ShulkerBoxModelRenderer.class)
public abstract class ShulkerBoxModelRendererMixin implements SimpleSpecialModelRenderer {
    @Shadow @Final private ShulkerBoxBlockEntityRenderer blockEntityRenderer;
    @Shadow @Final private float openness;
    @Shadow @Final private Direction facing;
    @Shadow @Final private SpriteIdentifier textureId;
    private SpriteIdentifier secondaryId;
    private ItemStack displayedItem;

    @Override
	public Void getData(ItemStack stack) {
        IDecoratedBox decorated = new DecoratedBoxItemStack(stack);
		DyeColor secondaryColor = decorated.getSecondaryColor();
		this.secondaryId = secondaryColor == null ? textureId : TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getIndex());
        this.displayedItem = decorated.getDisplayedItem();
        return null;
	}

    @Override
	public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int tintedColor) {
        IDecoratedShulkerBoxBlockEntityRenderer renderer = (IDecoratedShulkerBoxBlockEntityRenderer)this.blockEntityRenderer;
        renderer.render(matrices, queue, light, overlay, this.facing, this.openness, tintedColor, this.textureId, this.secondaryId, this.displayedItem);
	}

}
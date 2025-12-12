package com.fabien_gigante;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import java.util.function.Consumer;
import org.joml.Vector3fc;

public class DecoratedShulkerBoxModelRenderer implements SpecialModelRenderer<DecoratedBoxComponent> {
   private final ShulkerBoxBlockEntityRenderer blockEntityRenderer;
   private final float openness;
   private final Direction facing;
   private final SpriteIdentifier textureId;

   public DecoratedShulkerBoxModelRenderer(ShulkerBoxBlockEntityRenderer blockEntityRenderer, float openness, Direction facing, SpriteIdentifier textureId) {
      this.blockEntityRenderer = blockEntityRenderer;
      this.openness = openness;
      this.facing = facing;
      this.textureId = textureId;
   }

   public void collectVertices(Consumer<Vector3fc> vertices) {
      this.blockEntityRenderer.collectVertices(this.facing, this.openness, vertices);
   }

   @Override
   public DecoratedBoxComponent getData(ItemStack stack) {
        IDecoratedBox decorated = new DecoratedBoxItemStack(stack);
        return decorated.getDecorations();
   }

   @Override
	public void render(DecoratedBoxComponent decorations, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int tintedColor) {
        DyeColor secondaryColor = decorations.secondaryColor();
        SpriteIdentifier secondaryId = secondaryColor == null ? textureId : TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(secondaryColor.getIndex());
        ItemStack displayedItem = decorations.displayedItem();
        IDecoratedShulkerBoxBlockEntityRenderer renderer = (IDecoratedShulkerBoxBlockEntityRenderer)this.blockEntityRenderer;
        renderer.render(matrices, queue, light, overlay, this.facing, this.openness, tintedColor, this.textureId, secondaryId, displayedItem);
    }
}
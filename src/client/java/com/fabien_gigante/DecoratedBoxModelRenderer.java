package com.fabien_gigante;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

public class DecoratedBoxModelRenderer implements SpecialModelRenderer<DecoratedBoxComponent> {
   private final ShulkerBoxRenderer blockEntityRenderer;
   private final float openness;
   private final Direction facing;
   private final Material textureId;

   public DecoratedBoxModelRenderer(ShulkerBoxRenderer blockEntityRenderer, float openness, Direction facing, Material textureId) {
      this.blockEntityRenderer = blockEntityRenderer;
      this.openness = openness;
      this.facing = facing;
      this.textureId = textureId;
   }

   public void getExtents(Consumer<Vector3fc> vertices) {
      this.blockEntityRenderer.getExtents(this.facing, this.openness, vertices);
   }

   @Override
   public DecoratedBoxComponent extractArgument(ItemStack stack) {
        IDecoratedBox decorated = new DecoratedBoxItemStack(stack);
        return decorated.getDecorations();
   }

   @Override
	public void submit(DecoratedBoxComponent decorations, ItemDisplayContext displayContext, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, boolean glint, int tintedColor) {
        DyeColor secondaryColor = decorations.secondaryColor();
        Material secondaryId = secondaryColor == null ? textureId : Sheets.SHULKER_TEXTURE_LOCATION.get(secondaryColor.getId());
        ItemStack displayedItem = decorations.displayedItem();
        IDecoratedBoxRenderer renderer = (IDecoratedBoxRenderer)this.blockEntityRenderer;
        renderer.submit(matrices, queue, light, overlay, this.facing, this.openness, tintedColor, this.textureId, secondaryId, displayedItem);
    }
}
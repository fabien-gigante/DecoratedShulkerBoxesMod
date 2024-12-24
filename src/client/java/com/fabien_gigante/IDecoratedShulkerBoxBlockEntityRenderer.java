package com.fabien_gigante;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface IDecoratedShulkerBoxBlockEntityRenderer {
	public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, Direction facing, float openness, SpriteIdentifier lidId, SpriteIdentifier baseId, ItemStack display);
}

package com.fabien_gigante;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface IDecoratedShulkerBoxBlockEntityRenderer {
	public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Direction facing, float openness, int tintedColor, SpriteIdentifier lidId, SpriteIdentifier baseId, ItemStack display);
}

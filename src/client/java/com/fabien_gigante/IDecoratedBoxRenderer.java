package com.fabien_gigante;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IDecoratedBoxRenderer {
	public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, Direction facing, float openness, int tintedColor, Material lidId, Material baseId, ItemStack display);
}

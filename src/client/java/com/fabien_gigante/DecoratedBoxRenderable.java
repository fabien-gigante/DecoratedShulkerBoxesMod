package com.fabien_gigante;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.item.ItemStack;

public interface DecoratedBoxRenderable {
	public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, float openness, int tintedColor, SpriteId lidId, SpriteId baseId, ItemStack display);
}

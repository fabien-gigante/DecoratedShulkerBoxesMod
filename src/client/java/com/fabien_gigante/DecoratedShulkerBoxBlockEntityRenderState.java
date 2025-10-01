package com.fabien_gigante;

import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.render.block.entity.state.ShulkerBoxBlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

public class DecoratedShulkerBoxBlockEntityRenderState extends ShulkerBoxBlockEntityRenderState {
   @Nullable
   public DyeColor secondaryColor;
   public ItemRenderState itemRenderState = new ItemRenderState();
   
   public DecoratedShulkerBoxBlockEntityRenderState() {}
}

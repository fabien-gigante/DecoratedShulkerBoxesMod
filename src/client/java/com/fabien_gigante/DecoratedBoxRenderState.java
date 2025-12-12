package com.fabien_gigante;

import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

public class DecoratedBoxRenderState extends ShulkerBoxRenderState {
   @Nullable
   public DyeColor secondaryColor;
   public ItemStackRenderState itemRenderState = new ItemStackRenderState();
   
   public DecoratedBoxRenderState() {}
}

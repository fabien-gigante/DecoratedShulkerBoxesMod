package com.fabien_gigante.mixin;

import com.fabien_gigante.DecoratedShulkerBoxModelRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Mixin(ShulkerBoxModelRenderer.Unbaked.class)
public abstract class ShulkerBoxModelRendererUnbakedMixin  {
    @Shadow public abstract Identifier texture();
    @Shadow public abstract float openness();
    @Shadow public abstract Direction facing(); 

    /** @reason intended @author fabien **/
    @Overwrite
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
      return new DecoratedShulkerBoxModelRenderer(new ShulkerBoxBlockEntityRenderer(context), this.openness(), this.facing(), TexturedRenderLayers.SHULKER_SPRITE_MAPPER.map(this.texture()));
    }
}
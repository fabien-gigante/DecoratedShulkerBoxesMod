package com.fabien_gigante.mixin;

import com.fabien_gigante.DecoratedBoxModelRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBoxSpecialRenderer.Unbaked.class)
public abstract class ShulkerBoxSpecialRendererUnbakedMixin  {
    @Shadow public abstract Identifier texture();
    @Shadow public abstract float openness();
    @Shadow public abstract Direction orientation(); 

    /** @reason intended @author fabien **/
    @Overwrite
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
      return new DecoratedBoxModelRenderer(new ShulkerBoxRenderer(context), this.openness(), this.orientation(), Sheets.SHULKER_MAPPER.apply(this.texture()));
    }
}
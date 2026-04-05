package com.fabien_gigante.mixin;

import com.fabien_gigante.DecoratedBoxModelRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBoxSpecialRenderer.Unbaked.class)
public abstract class ShulkerBoxSpecialRendererUnbakedMixin  {
    @Shadow public abstract Identifier texture();
    @Shadow public abstract float openness();

    /** @reason intended @author fabien **/
    @Overwrite
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
      return new DecoratedBoxModelRenderer(new ShulkerBoxRenderer(context), this.openness(), Sheets.SHULKER_MAPPER.apply(this.texture()));
    }
}

package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.IDyed;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin extends HandledScreen<ShulkerBoxScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
    private static final int DEFAULT_COLOR = 0x976797;
    private static final int ALPHA = 0xff;

    public ShulkerBoxScreenMixin(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) { super(handler, inventory, title); }

    @Inject(method="drawBackground", at=@At("TAIL"))
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.handler instanceof IDyed dyed) {
            DyeColor dye = dyed.getColor();
            int color = ColorHelper.withAlpha(ALPHA, dye == null ? DEFAULT_COLOR : dye.getEntityColor()); 
            int x = 8-2, y = 18-2, w = 9*18+2, h = 3*18+2;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, this.x + x, this.y + y, x, y, w, h, 256, 256, color);
        }
    }

}

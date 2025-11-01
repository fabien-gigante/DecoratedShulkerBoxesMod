package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.DecoratedShulkerBoxesModClient;
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
    private static final Identifier DYED_TEXTURE = Identifier.of(DecoratedShulkerBoxesModClient.MOD_ID, "textures/gui/container/dyed_shulker_box.png");
    private static final int DEFAULT_COLOR = 0x976797;

    public ShulkerBoxScreenMixin(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) { super(handler, inventory, title); }

    private int getColor() {
        if (!(this.handler instanceof IDyed dyed)) return 0;
        DyeColor dye = dyed.getColor();
        return ColorHelper.fullAlpha(dye == null ? DEFAULT_COLOR : dye.getEntityColor()); 
    }
    private static boolean isDarkColor(int color) {
        return ColorHelper.getBlue(ColorHelper.grayscale(color)) < 160;
    }

    @Inject(method="drawBackground", at=@At("TAIL"))
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int color = getColor();
        if (color != 0)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, DYED_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256, color);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int color = getColor();
        if (color == 0)
            super.drawForeground(context, mouseX, mouseY);
        else {
            int titleColor = isDarkColor(color) ? 0xffffffff : 0xff404040;
            context.drawText(this.textRenderer, this.title, this.titleX, this.titleY + 1, titleColor, false);
            context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY + 1, 0xff404040, false);
        }
    }
}
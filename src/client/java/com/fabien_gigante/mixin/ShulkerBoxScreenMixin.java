package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.DecoratedShulkerBoxesModClient;
import com.fabien_gigante.Dyeable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin extends AbstractContainerScreen<ShulkerBoxMenu> {
    private static final Identifier DYED_TEXTURE = Identifier.fromNamespaceAndPath(DecoratedShulkerBoxesModClient.MOD_ID, "textures/gui/container/dyed_shulker_box.png");
    private static final int DEFAULT_COLOR = 0x976797;

    public ShulkerBoxScreenMixin(ShulkerBoxMenu handler, Inventory inventory, Component title) { super(handler, inventory, title); }

    private int getColor() {
        if (!(this.menu instanceof Dyeable dyed)) return 0;
        DyeColor dye = dyed.getColor();
        return ARGB.opaque(dye == null ? DEFAULT_COLOR : dye.getTextureDiffuseColor()); 
    }
    private static boolean isDarkColor(int color) {
        return ARGB.blue(ARGB.greyscale(color)) < 160;
    }

    @Inject(method="renderBg", at=@At("TAIL"))
    protected void drawBackground(GuiGraphics context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int color = getColor();
        if (color != 0)
            context.blit(RenderPipelines.GUI_TEXTURED, DYED_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256, color);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        int color = getColor();
        if (color == 0)
            super.renderLabels(context, mouseX, mouseY);
        else {
            int titleColor = isDarkColor(color) ? 0xffffffff : 0xff404040;
            context.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY + 1, titleColor, false);
            context.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 1, 0xff404040, false);
        }
    }
}
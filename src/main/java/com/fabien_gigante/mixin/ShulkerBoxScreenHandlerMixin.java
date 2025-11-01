package com.fabien_gigante.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.DyeColor;

import com.fabien_gigante.DecoratedShulkerBoxesMod;
import com.fabien_gigante.IDyed;

@Mixin(ShulkerBoxScreenHandler.class)
public abstract class ShulkerBoxScreenHandlerMixin extends ScreenHandler implements IDyed {
    protected @Nullable DyeColor color = null;

    protected ShulkerBoxScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) { super(type, syncId); }

    @Override
    public void setColor(@Nullable DyeColor color) { this.color = color; }

    @Override 
    public @Nullable DyeColor getColor() { return this.color; }

    @Override 
    public ScreenHandlerType<?> getType() {
        ScreenHandlerType<?> type = (color != null) ? DecoratedShulkerBoxesMod.SCREEN_HANDLER_TYPES.get(color) : null;
        return (type != null) ? type : super.getType();
    }
}

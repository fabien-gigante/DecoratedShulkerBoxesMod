package com.fabien_gigante.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import com.fabien_gigante.DecoratedShulkerBoxesMod;
import com.fabien_gigante.IDyed;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;

@Mixin(ShulkerBoxMenu.class)
public abstract class ShulkerBoxMenuMixin extends AbstractContainerMenu implements IDyed {
    protected @Nullable DyeColor color = null;

    protected ShulkerBoxMenuMixin(MenuType<?> type, int syncId) { super(type, syncId); }

    @Override
    public void setColor(@Nullable DyeColor color) { this.color = color; }

    @Override 
    public @Nullable DyeColor getColor() { return this.color; }

    @Override 
    public MenuType<?> getType() {
        MenuType<?> type = (color != null) ? DecoratedShulkerBoxesMod.SCREEN_HANDLER_TYPES.get(color) : null;
        return (type != null) ? type : super.getType();
    }
}

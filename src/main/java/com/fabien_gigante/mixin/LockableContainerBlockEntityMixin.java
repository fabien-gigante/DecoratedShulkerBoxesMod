package com.fabien_gigante.mixin;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin extends BlockEntityMixin {
    @Shadow
    public abstract Text getCustomName();
}
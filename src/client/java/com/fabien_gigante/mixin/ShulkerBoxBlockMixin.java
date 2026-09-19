package com.fabien_gigante.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.DecoratedShulkerBoxesModClient;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin {
    private @Shadow @Final @Nullable DyeColor color;

    @Inject(method="useWithoutItem", at=@At("HEAD"))
    private void captureLastUsedShulkerBoxColor(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult, final CallbackInfoReturnable<InteractionResult> cir) {
        DecoratedShulkerBoxesModClient.lastUsedShulkerBoxColor = this.color; // Fall back when mod is only present on client-side
    }
}

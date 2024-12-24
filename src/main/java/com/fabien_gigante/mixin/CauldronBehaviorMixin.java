package com.fabien_gigante.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.DecoratedShulkerBoxItemStack;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Inject(method="cleanShulkerBox", at = @At("RETURN"), cancellable = true)
	private static void cleanShulkerBox(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> ci) {
        if (ci.getReturnValue() != ActionResult.PASS && !world.isClient) {
            DecoratedShulkerBoxItemStack shulker = DecoratedShulkerBoxItemStack.from(world, player.getStackInHand(hand));
            if (shulker != null) shulker.setSecondaryColor(null);
        }
    }
}

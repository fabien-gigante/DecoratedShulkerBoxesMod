package com.fabien_gigante.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.fabien_gigante.IDecoratedShulkerBox;
import com.fabien_gigante.DecoratedShulkerBoxItemStack;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    @Unique
    private static void copyDecorations(BlockEntity blockEntity, ItemStack stack) {
        if (!(blockEntity instanceof IDecoratedShulkerBox source) || !source.hasDecorations()) return;
        var target = DecoratedShulkerBoxItemStack.from(blockEntity.getWorld(), stack);
        if (target != null); target.setDecorations(source);
    }

    // Properly carry over the secondary color when a shulker box block is broken into a shulker box item (survial mode)
    @Inject(method = "getDroppedStacks", cancellable = true, at = @At("TAIL"))
    private void getDroppedStacksWithDecorations(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> ci) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        for(ItemStack stack : ci.getReturnValue()) copyDecorations(blockEntity, stack);
    }

    // Properly carry over the secondary color when a shulker box block is broken into a shulker box item (creative mode)
    @Inject(method = "onBreak", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;applyComponentsFrom(Lnet/minecraft/component/ComponentMap;)V"))
    private void onBreakWithDecorations(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir, @Local ShulkerBoxBlockEntity shulker, @Local ItemStack stack) {
        copyDecorations(shulker, stack);
    }
}


	
package com.fabien_gigante.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.fabien_gigante.BlockEntityExt;
import com.fabien_gigante.DecoratedShulkerBoxEntity;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    private static void copyDecorationNbt(BlockEntity blockEntity, ItemStack stack) {
        if (!(blockEntity instanceof DecoratedShulkerBoxEntity shulker) || !shulker.hasDecorations()) return;
        if (stack != null && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) {
            NbtCompound nbt = BlockEntityExt.getBlockEntityNbt(stack);
            if (nbt != null) shulker.writeDecorationNbt(nbt);
            else {
                nbt = new NbtCompound();
                shulker.writeDecorationNbt(nbt);
                BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, nbt);
            }
        }
    }

    // Properly carry over the secondary color when a shulker box block is broken into a shulker box item (survial mode)
    @Inject(method = "getDroppedStacks", cancellable = true, at = @At("TAIL"))
    private void getDroppedStacksWithSecondaryColor(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> ci) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        for(ItemStack stack : ci.getReturnValue()) copyDecorationNbt(blockEntity, stack);
    }

    // Properly carry over the secondary color when a shulker box block is broken into a shulker box item (creative mode)
    @Inject(method = "onBreak", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;applyComponentsFrom(Lnet/minecraft/component/ComponentMap;)V"))
    private void onBreakWithSecondaryColor(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir, @Local ShulkerBoxBlockEntity shulker, @Local ItemStack stack) {
        copyDecorationNbt(shulker, stack);
    }
}


	
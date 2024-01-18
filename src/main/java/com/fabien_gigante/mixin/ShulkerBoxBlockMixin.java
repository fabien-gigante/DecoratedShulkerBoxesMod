package com.fabien_gigante.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.ShulkerBoxBlockEntityExt;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    // Properly carry over the secondary color when a shulker box block is broken into a shulker box item
    @Inject(method = "getDroppedStacks", cancellable = true, at = @At("TAIL"))
    private void getDroppedStacksWithSecondaryColor(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> ci) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            List<ItemStack> stacks = ci.getReturnValue();
            ItemStack stack = stacks.size() == 1 ? stacks.get(0) : null;
            if (stack != null && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock)
                ((ShulkerBoxBlockEntityExt) blockEntity).writeNbtSecondaryColor(BlockItem.getBlockEntityNbt(stack));
        }
    }

}

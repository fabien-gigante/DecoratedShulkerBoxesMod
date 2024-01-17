package com.fabien_gigante.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.ShulkerBoxBlockEntityExt;

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

    @Inject(method = "getDroppedStacks", cancellable = true, at = @At("TAIL"))
    private void getDroppedStacksWithBaseColor(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> ci) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        List<ItemStack> stacks = ci.getReturnValue();
        if (blockEntity instanceof ShulkerBoxBlockEntity && stacks.size() == 1) {
            ((ShulkerBoxBlockEntityExt)blockEntity).writeNbtSecondaryColor(BlockItem.getBlockEntityNbt(stacks.get(0)));
        }
    }

}

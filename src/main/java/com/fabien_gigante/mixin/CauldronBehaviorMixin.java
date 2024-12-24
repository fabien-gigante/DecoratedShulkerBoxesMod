package com.fabien_gigante.mixin;

import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method="registerBehavior", at = @At("TAIL"))
    private static void registerBehaviorBundles(CallbackInfo ci) {
        Map<Item, CauldronBehavior> map = CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map();
        for (DyeColor dyeColor : DyeColor.values())
            map.put(BundleItem.getBundle(dyeColor), CauldronBehaviorMixin::cleanBundle);
    }

	private static ActionResult cleanBundle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
		if (!stack.isIn(ItemTags.BUNDLES) || stack.isOf(Items.BUNDLE)) {
			return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
		} else {
			if (!world.isClient) {
				ItemStack itemStack = stack.copyComponentsToNewStack(Items.BUNDLE, 1);
				player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, itemStack, false));
				LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
			}
			return ActionResult.SUCCESS;
		}
	}    
}

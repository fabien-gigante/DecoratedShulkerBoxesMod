package com.fabien_gigante.mixin;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.fabien_gigante.DecoratedBoxItemStack;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin {
    @Inject(method="shulkerBoxInteraction", at = @At("RETURN"), cancellable = true)
	private static void cleanShulkerBox(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, CallbackInfoReturnable<InteractionResult> ci) {
        if (ci.getReturnValue() != InteractionResult.PASS && !world.isClientSide())
            new DecoratedBoxItemStack(player.getItemInHand(hand)).setSecondaryColor(null);
    }

    @Inject(method="bootStrap", at = @At("TAIL"))
    private static void registerBundles(CallbackInfo ci) {
        Map<Item, CauldronInteraction> map = CauldronInteraction.WATER.map();
        for (DyeColor dyeColor : DyeColor.values())
            map.put(BundleItem.getByColor(dyeColor), CauldronInteractionMixin::cleanBundle);
    }

	private static InteractionResult cleanBundle(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		if (!stack.is(ItemTags.BUNDLES) || stack.is(Items.BUNDLE))
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		if (!world.isClientSide()) {
			ItemStack itemStack = stack.transmuteCopy(Items.BUNDLE, 1);
			player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, itemStack, false));
			LayeredCauldronBlock.lowerFillLevel(state, world, pos);
		}
		return InteractionResult.SUCCESS;
	}    
}

package com.fabien_gigante.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.fabien_gigante.DecoratedBoxItemStack;

@Mixin(CauldronInteractions.class)
public abstract class CauldronInteractionsMixin {
	@Redirect(method = "shulkerBoxInteraction",	at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;transmuteCopy(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/item/ItemStack;"))
	private static ItemStack cleanShulkerBox(ItemStack stack, ItemLike newItem, int newCount) {
		ItemStack cleanedShulkerBox = stack.transmuteCopy(newItem, newCount);
		new DecoratedBoxItemStack(cleanedShulkerBox).setSecondaryColor(null);
		return cleanedShulkerBox;
	}

    @Inject(method="bootStrap", at = @At("TAIL"))
    private static void registerBundles(CallbackInfo ci) {
        CauldronInteractions.WATER.put(ItemTags.BUNDLES, CauldronInteractionsMixin::cleanBundle);
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

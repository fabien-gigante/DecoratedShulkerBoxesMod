package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.ShulkerBoxBlockEntityExt;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    @Shadow
    private static ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED;
    @Shadow
    private static ShulkerBoxBlockEntity RENDER_SHULKER_BOX;
    @Shadow
    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    // Vanilla item renderer delegates shulker box item rending to the shulker box block entity renderer
    // Slightly override this logic to set the secondary color on the block entity first
    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void renderShulkerBox(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (block instanceof ShulkerBoxBlock) {
            DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
            BlockEntity blockEntity = dyeColor == null ? RENDER_SHULKER_BOX : RENDER_SHULKER_BOX_DYED[dyeColor.getId()];
            ((ShulkerBoxBlockEntityExt) blockEntity).readNbtSecondaryColor(BlockItem.getBlockEntityNbt(stack));
            this.blockEntityRenderDispatcher.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
            ci.cancel();
        }
    }
}

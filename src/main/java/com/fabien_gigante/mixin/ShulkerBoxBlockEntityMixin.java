package com.fabien_gigante.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent.ItemStackContent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.IDecoratedShulkerBox;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends LockableContainerBlockEntityMixin implements IDecoratedShulkerBox {
	@Unique
	private DyeColor secondaryColor = null;
	private ItemStack displayedItem = null;

	private ShulkerBoxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {}

	@Shadow
	public abstract DyeColor getColor();

	@Override
	public boolean hasSecondaryColor() { return this.secondaryColor != null; }

	@Override
	public DyeColor getSecondaryColor() {
		return this.secondaryColor == null ? this.getColor() : this.secondaryColor;
	}
	@Override
	public void setSecondaryColor(DyeColor color) {
		this.secondaryColor = color == this.getColor() ? null : color;
	}

	@Override
	public boolean hasDisplayedItem() { return displayedItem != null; }

	@Override
	public ItemStack getDisplayedItem() { 
		ItemStack displayedItem = this.displayedItem; 
		if (displayedItem == null) {
		    Text text = getCustomName();
			HoverEvent hover = text != null ? text.getStyle().getHoverEvent() : null;
			ItemStackContent content = hover != null ? hover.getValue(HoverEvent.Action.SHOW_ITEM) : null;
			if (content != null) displayedItem = content.asStack();
		}
		return displayedItem;
	}

	@Override
	public void setDisplayedItem(ItemStack stack) {
		this.displayedItem = stack;
	 }

	protected void readDecorationNbt(NbtCompound nbt) {
		this.secondaryColor = nbt == null ? null : IDecoratedShulkerBox.getNbtSecondaryColor(nbt);
		this.displayedItem = nbt == null ? null : IDecoratedShulkerBox.getNbtDisplayedItem(nbt);
	}
	protected void writeDecorationNbt(NbtCompound nbt) {
		if (nbt != null) {
			IDecoratedShulkerBox.putNbtSecondaryColor(nbt, this.secondaryColor);
			IDecoratedShulkerBox.putNbtDisplayedItem(nbt, this.displayedItem);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) { readDecorationNbt(nbt); }

	@Override
	protected void writeNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) { writeDecorationNbt(nbt); }

	@Override
	protected void toInitialChunkDataNbt(WrapperLookup lookup, CallbackInfoReturnable<NbtCompound> cir) {
		writeDecorationNbt(cir.getReturnValue());
	}

	@Override
    protected void toUpdatePacket(CallbackInfoReturnable<@Nullable Packet<ClientPlayPacketListener>> cir) {
		cir.setReturnValue(BlockEntityUpdateS2CPacket.create((BlockEntity)(Object)this));
	}
}

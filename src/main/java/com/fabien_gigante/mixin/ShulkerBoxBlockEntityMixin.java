package com.fabien_gigante.mixin;

import net.minecraft.block.entity.BlockEntity;
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

	// Shulker boxed can be decorated with : a secondary color

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

	// Shulker boxed can be decorated with : a displayed item

	@Override
	public boolean hasDisplayedItem() { return displayedItem != null; }

	@Override
	public ItemStack getDisplayedItem() { 
		ItemStack displayedItem = this.displayedItem; 
		if (displayedItem == null) {
			// Fallback implementation (mod used as client only, with server plugin setting the hover show item)
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

	// Persistency in Nbt...

	@Unique
	protected void readDecorationNbt(WrapperLookup lookup, NbtCompound nbt) {
		this.secondaryColor = nbt == null ? null : IDecoratedShulkerBox.getNbtSecondaryColor(nbt);
		this.displayedItem = nbt == null ? null : IDecoratedShulkerBox.getNbtDisplayedItem(lookup, nbt);
	}
	@Unique
	protected void writeDecorationNbt(WrapperLookup lookup, NbtCompound nbt) {
		if (nbt != null) {
			IDecoratedShulkerBox.putNbtSecondaryColor(nbt, this.secondaryColor);
			IDecoratedShulkerBox.putNbtDisplayedItem(lookup, nbt, this.displayedItem);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) { readDecorationNbt(lookup, nbt); }
	@Override
	protected void writeNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) { writeDecorationNbt(lookup, nbt); }
	@Override
	protected void toInitialChunkDataNbt(WrapperLookup lookup, CallbackInfoReturnable<NbtCompound> cir) {
		writeDecorationNbt(lookup, cir.getReturnValue());
	}
	@Override
    protected void toUpdatePacket(CallbackInfoReturnable<@Nullable Packet<ClientPlayPacketListener>> cir) {
		cir.setReturnValue(BlockEntityUpdateS2CPacket.create((BlockEntity)(Object)this));
	}
}

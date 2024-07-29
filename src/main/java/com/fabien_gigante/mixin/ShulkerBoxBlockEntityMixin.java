package com.fabien_gigante.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.SecondaryColorExt;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends BlockEntityMixin implements SecondaryColorExt {
	@Unique
	private DyeColor secondaryColor = null;

	private ShulkerBoxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {}

	@Shadow
	public abstract DyeColor getColor(); // { return null; }

	@Override
	public DyeColor getSecondaryColor() {
		return this.secondaryColor == null ? this.getColor() : this.secondaryColor;
	}
	@Override
	public void setSecondaryColor(DyeColor color) {
		this.secondaryColor = color == this.getColor() ? null : color;
	}

	@Override
	public boolean hasDistinctSecondaryColor() { return this.secondaryColor != null; }

	@Override
	public void readNbtSecondaryColor(NbtCompound nbt) {
		this.secondaryColor = nbt == null ? null : SecondaryColorExt.getNbtSecondaryColor(nbt);
	}
	@Override
	public void writeNbtSecondaryColor(NbtCompound nbt) {
		if (nbt != null)
			SecondaryColorExt.putNbtSecondaryColor(nbt, this.secondaryColor);
	}

	@Override
	protected void readNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) { readNbtSecondaryColor(nbt); }

	@Override
	protected void writeNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) { writeNbtSecondaryColor(nbt); }

	protected void toInitialChunkDataNbt(WrapperLookup lookup, CallbackInfoReturnable<NbtCompound> cir) {
		NbtCompound nbt = cir.getReturnValue();
		writeNbtSecondaryColor(nbt);
		cir.setReturnValue(nbt);
	}

    protected void toUpdatePacket(CallbackInfoReturnable<@Nullable Packet<ClientPlayPacketListener>> cir) {
		cir.setReturnValue(BlockEntityUpdateS2CPacket.create((BlockEntity)(Object)this));
	}
}

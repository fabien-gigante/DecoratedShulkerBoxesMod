package com.fabien_gigante.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.ShulkerBoxBlockEntityExt;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends BlockEntity implements ShulkerBoxBlockEntityExt {
	@Unique
	private DyeColor secondaryColor = null;

	public ShulkerBoxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

	@Shadow 
	public DyeColor getColor() { return null; }

	@Override
	public DyeColor getSecondaryColor() {
		return this.secondaryColor == null ? this.getColor() : this.secondaryColor;
	}
	@Override
	public void setSecondaryColor(DyeColor color) {
		this.secondaryColor = color == this.getColor() ? null : color;
	}

	@Override
	public void readNbtSecondaryColor(NbtCompound nbt) {
		this.secondaryColor = nbt == null ? null : ShulkerBoxBlockEntityExt.getNbtSecondaryColor(nbt);
	}
	@Override
	public void writeNbtSecondaryColor(NbtCompound nbt) {
		if (nbt != null) ShulkerBoxBlockEntityExt.putNbtSecondaryColor(nbt, this.secondaryColor);
	}

	@Inject(method = "readNbt", at = @At("TAIL"))
	private void readNbtSecondaryColor(NbtCompound nbt, CallbackInfo ci) { readNbtSecondaryColor(nbt); }
	@Inject(method = "writeNbt", at = @At("TAIL"))
	private void writeNbtSecondaryColor(NbtCompound nbt, CallbackInfo ci) { writeNbtSecondaryColor(nbt); }

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbt = new NbtCompound();
		writeNbtSecondaryColor(nbt);
		return nbt;
	}

}

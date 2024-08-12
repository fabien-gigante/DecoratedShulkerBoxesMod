package com.fabien_gigante.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Avoid overrides using mixin inheritance
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin  {
	@Inject(method = "readNbt", at = @At("TAIL"))
	protected void readNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) {}

	@Inject(method = "writeNbt", at = @At("TAIL"))
	protected void writeNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) {}

    @Inject(method = "toInitialChunkDataNbt", at = @At("TAIL"), cancellable = true)
    protected void toInitialChunkDataNbt(WrapperLookup lookup, CallbackInfoReturnable<NbtCompound> cir) {}

    @Inject(method = "toUpdatePacket", at = @At("TAIL"), cancellable = true)
    protected void toUpdatePacket(CallbackInfoReturnable<@Nullable Packet<ClientPlayPacketListener>> cir) {}
}

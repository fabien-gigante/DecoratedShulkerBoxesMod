package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

import com.fabien_gigante.DecoratedBoxComponent;
import com.fabien_gigante.IDecoratedBox;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends LockableContainerBlockEntity implements IDecoratedBox {
	@Unique
	private DecoratedBoxComponent decorations = DecoratedBoxComponent.DEFAULT;

	private ShulkerBoxBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) { super(blockEntityType, blockPos, blockState); }

	// Shulker boxed can be decorated
	
	@Shadow
	public abstract DyeColor getColor();
	public void setDecorations(DecoratedBoxComponent decorations) { this.decorations = decorations; }
	public DecoratedBoxComponent getDecorations() { return this.decorations; }

	// Persistency
	
	@Inject(method = "readNbt", at = @At("TAIL"))
	protected void readNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) {
		this.decorations =	DecoratedBoxComponent.readNbt(nbt, lookup);
	}

	@Inject(method = "writeNbt", at = @At("TAIL"))
	protected void writeNbt(NbtCompound nbt, WrapperLookup lookup, CallbackInfo ci) {
		this.decorations.writeNbt(nbt, lookup);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt(WrapperLookup lookup) {
		return this.decorations.writeNbt(new NbtCompound(), lookup);
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create((BlockEntity)(Object)this);
	}

	@Override
	public void readComponents(ComponentsAccess components) {
		super.readComponents(components);
		this.decorations = components.getOrDefault(DecoratedBoxComponent.TYPE, DecoratedBoxComponent.DEFAULT);
	}
 
	@Override
	public void addComponents(ComponentMap.Builder builder) {
		super.addComponents(builder);
		builder.add(DecoratedBoxComponent.TYPE, this.decorations.orNull());
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		DecoratedBoxComponent.removeNbt(nbt);
	}
}

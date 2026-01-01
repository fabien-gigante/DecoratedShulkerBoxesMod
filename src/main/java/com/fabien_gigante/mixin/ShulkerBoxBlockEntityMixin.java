package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.fabien_gigante.DecoratedBoxComponent;
import com.fabien_gigante.Decorable;
import com.fabien_gigante.Dyeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends BaseContainerBlockEntity implements Decorable {
	@Unique
	private DecoratedBoxComponent decorations = DecoratedBoxComponent.DEFAULT;

	private ShulkerBoxBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) { super(blockEntityType, blockPos, blockState); }

	// Shulker boxed can be decorated
	
	@Shadow
	public abstract DyeColor getColor();
	public void setDecorations(DecoratedBoxComponent decorations) { this.decorations = decorations; }
	public DecoratedBoxComponent getDecorations() { return this.decorations; }

	// Persistency
	
	@Inject(method = "loadAdditional", at = @At("TAIL"))
	protected void loadDecorations(ValueInput view, CallbackInfo ci) {
		this.decorations =	DecoratedBoxComponent.readData(view);
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	protected void saveDecorations(ValueOutput view, CallbackInfo ci) {
		this.decorations.writeData(view);
	}
	
	@Override
	public CompoundTag getUpdateTag(Provider lookup) {
		return saveWithoutMetadata(lookup);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create((BlockEntity)(Object)this);
	}

	@Override
	public void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		this.decorations = components.getOrDefault(DecoratedBoxComponent.TYPE, DecoratedBoxComponent.DEFAULT);
	}
 
	@Override
	public void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);
		builder.set(DecoratedBoxComponent.TYPE, this.decorations.orNull());
	}

	@Override
	public void removeComponentsFromTag(ValueOutput view) {
		super.removeComponentsFromTag(view);
		DecoratedBoxComponent.removeData(view);
	}

	@Inject(method = "createMenu", at = @At("RETURN"))
	protected void setMenuColor(int syncId, Inventory playerInventory, CallbackInfoReturnable<AbstractContainerMenu> cir) {
		if (cir.getReturnValue() instanceof Dyeable dyed) dyed.setColor(getColor());
	}
}

package com.fabien_gigante.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends Entity {
	public IronGolemEntityMixin(EntityType<?> type, World world) { super(type, world); }

	// Iron golems drop a tear when given a poppy
	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void interactMobWithFlower(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.POPPY)) {
			World world = this.getWorld();
			if (!player.getAbilities().creativeMode)
				itemStack.decrement(1);
			if (!world.isClient && this.random.nextFloat() < 0.125f) {
				this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, .5F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F);
				this.dropItem(Items.IRON_NUGGET);
				this.emitGameEvent(GameEvent.ENTITY_PLACE);
			}
			cir.setReturnValue(ActionResult.success(world.isClient));
		}
	}
}
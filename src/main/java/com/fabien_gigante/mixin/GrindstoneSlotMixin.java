package com.fabien_gigante.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabien_gigante.SlotExtendable;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = {"net/minecraft/world/inventory/GrindstoneMenu$2", "net/minecraft/world/inventory/GrindstoneMenu$4"})
public abstract class GrindstoneSlotMixin implements SlotExtendable {
    @Unique private List<Predicate<ItemStack>> itemConditions = new ArrayList<>();
    @Unique private List<BiConsumer<Player, ItemStack>> takeActions = new ArrayList<>();

    @Override
    public void allowItemCondition(Predicate<ItemStack> condition) { itemConditions.add(condition); }

    @Override
    public void registerItemTakenAction(BiConsumer<Player, ItemStack> consumer) { takeActions.add(consumer); }

    @ModifyReturnValue(method = "mayPlace", at = @At("RETURN"))
    private boolean modifyMayPlace(boolean original, ItemStack stack) {
        return original || itemConditions.stream().anyMatch(condition -> condition.test(stack));
    }

    @Inject(method = "onTake", at = @At("HEAD"), require = 0)
    private void onTake(Player player, ItemStack resultStack, CallbackInfo ci) {
        takeActions.forEach(consumer -> consumer.accept(player, resultStack));
    }
}




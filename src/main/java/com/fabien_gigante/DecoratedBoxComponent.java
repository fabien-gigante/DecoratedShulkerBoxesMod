package com.fabien_gigante;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.storage.WriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record DecoratedBoxComponent(DyeColor secondaryColor, ItemStack displayedItem) {
    public static final Codec<DecoratedBoxComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
            DyeColor.CODEC.optionalFieldOf("secondary_color").forGetter(deco -> Optional.ofNullable(deco.secondaryColor)),
            ItemStack.CODEC.optionalFieldOf("displayed_item").forGetter(deco -> Optional.ofNullable(deco.displayedItem))
        ).apply(builder, (color, item) -> new DecoratedBoxComponent(color.orElse(null), item.orElse(null)));
    });

    private static Identifier ID = Identifier.of(DecoratedShulkerBoxesMod.MOD_ID, "decorations");
    public static final ComponentType<DecoratedBoxComponent> TYPE = Registry.register(
        Registries.DATA_COMPONENT_TYPE, ID,
        ComponentType.<DecoratedBoxComponent>builder().codec(DecoratedBoxComponent.CODEC).build()
    );

    static public final DecoratedBoxComponent DEFAULT = new DecoratedBoxComponent(null, null);
    public boolean isEmpty() { return secondaryColor==null && displayedItem==null; }
    public @Nullable DecoratedBoxComponent orNull() { return isEmpty() ? null : this; }

    private static ItemStack nonNull(ItemStack stack) { return stack == null ? ItemStack.EMPTY : stack; }
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DecoratedBoxComponent deco)) return false;
        return secondaryColor==deco.secondaryColor && ItemStack.areEqual(nonNull(this.displayedItem), nonNull(deco.displayedItem));
   }

    public DecoratedBoxComponent setSecondaryColor(DyeColor secondaryColor) { return new DecoratedBoxComponent(secondaryColor, displayedItem); }
    public DecoratedBoxComponent setDisplayedItem(ItemStack displayedItem) { return new DecoratedBoxComponent(secondaryColor, displayedItem); }

	public static DecoratedBoxComponent readData(ReadView view) {
        return view.read(ID.toString(), CODEC).orElse(DEFAULT);
	}

	public void writeData(WriteView view) {
		if (isEmpty()) removeData(view);
        else view.put(ID.toString(), CODEC, this);
	}

    static public void removeData(WriteView view) { view.remove(ID.toString()); }
}

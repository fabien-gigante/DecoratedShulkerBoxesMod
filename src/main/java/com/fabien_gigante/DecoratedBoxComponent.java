package com.fabien_gigante;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DecoratedBoxComponent(DyeColor secondaryColor, ItemStack displayedItem) {
    public static final Codec<DecoratedBoxComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
            DyeColor.CODEC.optionalFieldOf("secondary_color").forGetter(deco -> Optional.ofNullable(deco.secondaryColor)),
            ItemStack.CODEC.optionalFieldOf("displayed_item").forGetter(deco -> Optional.ofNullable(deco.displayedItem))
        ).apply(builder, (color, item) -> new DecoratedBoxComponent(color.orElse(null), item.orElse(null)));
    });

    private static Identifier ID = Identifier.fromNamespaceAndPath(DecoratedShulkerBoxesMod.MOD_ID, "decorations");
    public static final DataComponentType<DecoratedBoxComponent> TYPE = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE, ID,
        DataComponentType.<DecoratedBoxComponent>builder().persistent(DecoratedBoxComponent.CODEC).build()
    );

    static public final DecoratedBoxComponent DEFAULT = new DecoratedBoxComponent(null, null);
    public boolean isEmpty() { return secondaryColor==null && displayedItem==null; }
    public @Nullable DecoratedBoxComponent orNull() { return isEmpty() ? null : this; }

    private static ItemStack nonNull(ItemStack stack) { return stack == null ? ItemStack.EMPTY : stack; }
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DecoratedBoxComponent deco)) return false;
        return secondaryColor==deco.secondaryColor && ItemStack.matches(nonNull(this.displayedItem), nonNull(deco.displayedItem));
   }

    public DecoratedBoxComponent setSecondaryColor(DyeColor secondaryColor) { return new DecoratedBoxComponent(secondaryColor, displayedItem); }
    public DecoratedBoxComponent setDisplayedItem(ItemStack displayedItem) { return new DecoratedBoxComponent(secondaryColor, displayedItem); }

	public static DecoratedBoxComponent readData(ValueInput view) {
        return view.read(ID.toString(), CODEC).orElse(DEFAULT);
	}

	public void writeData(ValueOutput view) {
		if (isEmpty()) removeData(view);
        else view.store(ID.toString(), CODEC, this);
	}

    static public void removeData(ValueOutput view) { view.discard(ID.toString()); }
}

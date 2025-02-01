package com.fabien_gigante;

import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FakeItemFrameEntity extends ItemFrameEntity {
    public FakeItemFrameEntity() {
        super(null, BlockPos.ORIGIN, Direction.DOWN);
        setSilent(true); setInvisible(true);
    }
    @Override
    public MapIdComponent getMapId(ItemStack stack) { return null; }
}

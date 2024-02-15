package com.fabien_gigante.mixin;

import com.fabien_gigante.PipeBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
 
 /**
  * Hopper output can be extended using pipes
  */
 @Mixin(HopperBlockEntity.class)
 public class HopperBlockEntityMixin {

    private static boolean isPipe(World world, BlockPos pos, Direction direction) {
        BlockState state = world.getBlockState(pos);
        return state != null && state.isOf(PipeBlock.PIPE) && state.get(PipeBlock.AXIS) == direction.getAxis();
    }

    @Overwrite
    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(HopperBlock.FACING);
        BlockPos targetPos = pos;
        for(int d = 0; d < 16; d++) {
            targetPos = targetPos.offset(direction);
            if (!isPipe(world, targetPos, direction)) break;
        }
        return HopperBlockEntity.getInventoryAt(world, targetPos);
    }
    
 }
 
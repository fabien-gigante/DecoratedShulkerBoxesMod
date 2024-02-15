package com.fabien_gigante;

import com.mojang.serialization.MapCodec;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/*
 * A pipe block can be used to extend hopper's outputs (@see {@link HopperBLockEntityMixin})
 */
public class PipeBlock extends PoleBlock {
    public static final MapCodec<PipeBlock> CODEC = PipeBlock.createCodec(PipeBlock::new);

    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6, 4, 0, 10, 8, 16);
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0, 4, 6, 16, 8, 10);

    // Pipe block singleton
    public static final PipeBlock PIPE = new PipeBlock(
        FabricBlockSettings.create().mapColor(MapColor.ORANGE).solid().requiresTool()
        .strength(4.0f, 5.0f).sounds(BlockSoundGroup.COPPER).nonOpaque());

    public MapCodec<PipeBlock> getCodec() { return CODEC; }

    // Constructor
    public PipeBlock(AbstractBlock.Settings settings) { super(settings); }

    // Get the pipe's specific outline shape box accroding to its axis
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {case X -> X_SHAPE; case Z-> Z_SHAPE; default -> Y_SHAPE; };
    }
}


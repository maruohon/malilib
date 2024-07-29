package malilib.util.game;

import malilib.util.position.BlockPos;
import net.minecraft.block.state.IBlockState;

public interface BlockView
{
    IBlockState getBlockState(int x, int y, int z);

    IBlockState getBlockState(BlockPos pos);
}

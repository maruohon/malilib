package malilib.util.game;

import net.minecraft.block.state.IBlockState;

import malilib.util.position.BlockPos;

public interface BlockView<T extends IBlockState>
{
    T getBlockState(int x, int y, int z);

    T getBlockState(BlockPos pos);
}

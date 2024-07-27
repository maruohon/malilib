package malilib.util.game;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import malilib.util.position.BlockPos;

public interface BlockAndDataView<T extends IBlockState, BE extends TileEntity, TAG extends NBTTagCompound> extends BlockView<T>
{
    @Nullable
    BE getBlockEntity(int x, int y, int z);

    @Nullable
    BE getBlockEntity(BlockPos pos);

    @Nullable
    TAG getBlockDataTag(int x, int y, int z);

    @Nullable
    TAG getBlockDataTag(BlockPos pos);
}

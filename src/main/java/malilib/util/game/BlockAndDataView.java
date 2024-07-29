package malilib.util.game;

import javax.annotation.Nullable;
import malilib.util.position.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface BlockAndDataView extends BlockView
{
    @Nullable
    TileEntity getBlockEntity(int x, int y, int z);

    @Nullable
    TileEntity getBlockEntity(BlockPos pos);

    @Nullable
    NBTTagCompound getBlockDataTag(int x, int y, int z);

    @Nullable
    NBTTagCompound getBlockDataTag(BlockPos pos);
}

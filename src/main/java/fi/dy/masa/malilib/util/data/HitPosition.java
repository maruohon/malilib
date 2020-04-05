package fi.dy.masa.malilib.util.data;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HitPosition
{
    protected final BlockPos blockPos;
    protected final Vec3d exactPos;
    protected final EnumFacing direction;

    public HitPosition(BlockPos blockPos, Vec3d exactPos, EnumFacing direction)
    {
        this.blockPos = blockPos;
        this.exactPos = exactPos;
        this.direction = direction;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public Vec3d getExactPos()
    {
        return this.exactPos;
    }

    public EnumFacing getSide()
    {
        return this.direction;
    }

    public static HitPosition of(BlockPos blockPos, Vec3d exactPos, EnumFacing direction)
    {
        return new HitPosition(blockPos, exactPos, direction);
    }
}

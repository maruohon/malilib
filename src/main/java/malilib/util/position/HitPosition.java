package malilib.util.position;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class HitPosition
{
    protected final BlockPos blockPos;
    protected final Vec3 exactPos;
    protected final Direction direction;

    public HitPosition(BlockPos blockPos, Vec3 exactPos, Direction direction)
    {
        this.blockPos = blockPos;
        this.exactPos = exactPos;
        this.direction = direction;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public Vec3 getExactPos()
    {
        return this.exactPos;
    }

    public Direction getSide()
    {
        return this.direction;
    }

    public static HitPosition of(BlockPos blockPos, Vec3 exactPos, Direction direction)
    {
        return new HitPosition(blockPos, exactPos, direction);
    }
}

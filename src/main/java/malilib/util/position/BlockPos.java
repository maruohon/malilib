package malilib.util.position;

import malilib.util.MathUtils;

public class BlockPos extends Vec3i
{
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public BlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPos offset(Direction direction, int amount)
    {
        return new BlockPos(this.x + direction.getXOffset() * amount,
                            this.y + direction.getYOffset() * amount,
                            this.z + direction.getZOffset() * amount);
    }

    public BlockPos offset(Direction direction)
    {
        return this.offset(direction, 1);
    }

    public BlockPos down()
    {
        return new BlockPos(this.x, this.y - 1, this.z);
    }

    public BlockPos up()
    {
        return new BlockPos(this.x, this.y + 1, this.z);
    }

    public BlockPos north()
    {
        return new BlockPos(this.x, this.y, this.z - 1);
    }

    public BlockPos south()
    {
        return new BlockPos(this.x, this.y, this.z + 1);
    }

    public BlockPos west()
    {
        return new BlockPos(this.x - 1, this.y, this.z);
    }

    public BlockPos east()
    {
        return new BlockPos(this.x + 1, this.y, this.z);
    }

    public static BlockPos ofFloored(double x, double y, double z)
    {
        return new BlockPos(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(z));
    }
}

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
        return new BlockPos(this.getX() + direction.getXOffset() * amount,
                            this.getY() + direction.getYOffset() * amount,
                            this.getZ() + direction.getZOffset() * amount);
    }

    public BlockPos offset(Direction direction)
    {
        return this.offset(direction, 1);
    }

    public BlockPos down()
    {
        return new BlockPos(this.getX(), this.getY() - 1, this.getZ());
    }

    public BlockPos up()
    {
        return new BlockPos(this.getX(), this.getY() + 1, this.getZ());
    }

    public BlockPos north()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    public BlockPos south()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    public BlockPos west()
    {
        return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    public BlockPos east()
    {
        return new BlockPos(this.getX() + 1, this.getY(), this.getZ());
    }

    public static BlockPos ofFloored(double x, double y, double z)
    {
        return new BlockPos(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(z));
    }

    public static class MutBlockPos extends BlockPos
    {
        private int x;
        private int y;
        private int z;

        public MutBlockPos()
        {
            this(0, 0, 0);
        }

        public MutBlockPos(int x, int y, int z)
        {
            super(x, y, z);
        }

        @Override
        public int getX()
        {
            return this.x;
        }

        @Override
        public int getY()
        {
            return this.y;
        }

        @Override
        public int getZ()
        {
            return this.z;
        }

        public void setX(int x)
        {
            this.x = x;
        }

        public void setY(int y)
        {
            this.y = y;
        }

        public void setZ(int z)
        {
            this.z = z;
        }

        public void set(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public BlockPos offset(Direction direction, int amount)
        {
            this.set(this.getX() + direction.getXOffset() * amount,
                     this.getY() + direction.getYOffset() * amount,
                     this.getZ() + direction.getZOffset() * amount);

            return this;
        }

        @Override
        public BlockPos offset(Direction direction)
        {
            return this.offset(direction, 1);
        }

        public BlockPos toImmutable()
        {
            return new BlockPos(this.x, this.y, this.z);
        }
    }
}

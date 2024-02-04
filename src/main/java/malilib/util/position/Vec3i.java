package malilib.util.position;

public class Vec3i extends net.minecraft.util.math.BlockPos
{
    public static final Vec3i ZERO = new Vec3i(0, 0, 0);

    /*
    public final int x;
    public final int y;
    public final int z;
    */

    public Vec3i(int x, int y, int z)
    {
        super(x, y, z);
        /*
        this.x = x;
        this.y = y;
        this.z = z;
        */
    }

    /*
    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }
    */

    public long squareDistanceTo(Vec3i other)
    {
        return this.squareDistanceTo(other.getX(), other.getY(), other.getZ());
    }

    public long squareDistanceTo(int x, int y, int z)
    {
        return (long) this.getX() * x + (long) this.getY() * y + (long) this.getZ() * z;
    }

    public double squareDistanceOfCenterTo(Vec3d pos)
    {
        return (this.getX() + 0.5) * pos.x + (this.getY() + 0.5) * pos.y + (this.getZ() + 0.5) * pos.z;
    }
}

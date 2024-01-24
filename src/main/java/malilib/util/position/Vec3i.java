package malilib.util.position;

import net.minecraft.util.math.Vec3d;

public class Vec3i
{
    public static final Vec3i ZERO = new Vec3i(0, 0, 0);

    public final int x;
    public final int y;
    public final int z;

    public Vec3i(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

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

    public long squareDistanceTo(Vec3i other)
    {
        return this.squareDistanceTo(other.x, other.y, other.z);
    }

    public long squareDistanceTo(int x, int y, int z)
    {
        return (long) this.x * x + (long) this.y * y + (long) this.z * z;
    }

    public double squareDistanceOfCenterTo(Vec3d pos)
    {
        return (this.x + 0.5) * pos.x + (this.y + 0.5) * pos.y + (this.z + 0.5) * pos.z;
    }
}

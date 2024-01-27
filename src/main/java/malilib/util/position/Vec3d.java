package malilib.util.position;

public class Vec3d
{
    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);

    public final double x;
    public final double y;
    public final double z;

    public Vec3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public Vec3d add(Vec3d other)
    {
        return new Vec3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3d subtract(Vec3d other)
    {
        return new Vec3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vec3d scale(double factor)
    {
        return new Vec3d(this.x * factor, this.y * factor, this.z * factor);
    }

    public double squareDistanceTo(Vec3d other)
    {
        return this.squareDistanceTo(other.x, other.y, other.z);
    }

    public double squareDistanceTo(double x, double y, double z)
    {
        return this.x * x + this.y * y + this.z * z;
    }

    public double distanceTo(Vec3d other)
    {
        return this.distanceTo(other.x, other.y, other.z);
    }

    public double distanceTo(double x, double y, double z)
    {
        return Math.sqrt(this.squareDistanceTo(x, y, z));
    }

    public Vec3d normalize()
    {
        return normalized(this.x, this.y, this.z);
    }

    public net.minecraft.util.math.Vec3d toVanilla()
    {
        return net.minecraft.util.math.Vec3d.of(this.x, this.y, this.z);
    }

    public static Vec3d normalized(double x, double y, double z)
    {
        double d = Math.sqrt(x * x + y * y + z * z);
        return d < 1.0E-4 ? ZERO : new Vec3d(x / d, y / d, z / d);
    }
}

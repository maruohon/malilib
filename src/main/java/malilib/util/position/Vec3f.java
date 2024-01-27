package malilib.util.position;

public class Vec3f
{
    public static final Vec3f ZERO = new Vec3f(0.0F, 0.0F, 0.0F);

    public final float x;
    public final float y;
    public final float z;

    public Vec3f(double x, double y, double z)
    {
        this((float) x, (float) y, (float) z);
    }

    public Vec3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public float getZ()
    {
        return this.z;
    }

    public Vec3f normalize()
    {
        return normalized(this.x, this.y, this.z);
    }

    public static Vec3f normalized(float x, float y, float z)
    {
        double d = Math.sqrt(x * x + y * y + z * z);
        return d < 1.0E-4 ? ZERO : new Vec3f(x / d, y / d, z / d);
    }
}

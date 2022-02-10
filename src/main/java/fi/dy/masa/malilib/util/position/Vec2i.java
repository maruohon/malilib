package fi.dy.masa.malilib.util.position;

public class Vec2i
{
    public static final Vec2i ZERO = new Vec2i(0, 0);

    public final int x;
    public final int y;

    public Vec2i(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public double getSquaredDistance(int x, int y)
    {
        double diffX = (double) x - (double) this.x;
        double diffY = (double) y - (double) this.y;

        return diffX * diffX + diffY * diffY;
    }

    public double getDistance(int x, int y)
    {
        return Math.sqrt(this.getSquaredDistance(x, y));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        Vec2i vec2i = (Vec2i) o;

        if (this.x != vec2i.x) { return false; }
        return this.y == vec2i.y;
    }

    @Override
    public int hashCode()
    {
        int result = this.x;
        result = 31 * result + this.y;
        return result;
    }
}

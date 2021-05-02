package fi.dy.masa.malilib.util.data;

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

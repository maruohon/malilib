package fi.dy.masa.malilib.util;

public class Color4f
{
    public static final Color4f ZERO = new Color4f(0F, 0F, 0F, 0F);
    public final float r;
    public final float g;
    public final float b;
    public final float a;
    public final int intValue;

    public Color4f(float r, float g, float b)
    {
        this(r, g, b, 1f);
    }

    public Color4f(float r, float g, float b, float a)
    {
        if (r == -0.0F)
        {
            r = 0.0F;
        }

        if (g == -0.0F)
        {
            g = 0.0F;
        }

        if (b == -0.0F)
        {
            b = 0.0F;
        }

        if (a == -0.0F)
        {
            a = 0.0F;
        }

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        /*
        this.r = MathHelper.clamp(r, 0f, 1f);
        this.g = MathHelper.clamp(g, 0f, 1f);
        this.b = MathHelper.clamp(b, 0f, 1f);
        this.a = MathHelper.clamp(a, 0f, 1f);
        */

        this.intValue = (((int) (a * 0xFF)) << 24) | (((int) (r * 0xFF)) << 16) | (((int) (g * 0xFF)) << 8) | (((int) (b * 0xFF)));
    }

    public static Color4f fromColor(int color)
    {
        float alpha = ((color & 0xFF000000) >>> 24) / 255f;
        return fromColor(color, alpha);
    }

    public static Color4f fromColor(int color, float alpha)
    {
        float r = ((color & 0x00FF0000) >>> 16) / 255f;
        float g = ((color & 0x000FF000) >>>  8) / 255f;
        float b = ((color & 0x000000FF)       ) / 255f;

        return new Color4f(r, g, b, alpha);
    }

    public static Color4f fromColor(Color4f color, float alpha)
    {
        return new Color4f(color.r, color.g, color.b, alpha);
    }
}

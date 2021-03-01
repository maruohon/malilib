package fi.dy.masa.malilib.util.data;

import java.awt.*;

public class Color4f
{
    public static final Color4f WHITE = new Color4f(1F, 1F, 1F, 1F);
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

    public Color4f withAlpha(float alpha)
    {
        return fromColor(this.intValue, alpha);
    }

    public static Color4f fromColor(int color)
    {
        float alpha = ((color & 0xFF000000) >>> 24) / 255f;
        return fromColor(color, alpha);
    }

    public static Color4f fromColor(int color, float alpha)
    {
        float r = ((color & 0x00FF0000) >>> 16) / 255f;
        float g = ((color & 0x0000FF00) >>>  8) / 255f;
        float b = ((color & 0x000000FF)       ) / 255f;

        return new Color4f(r, g, b, alpha);
    }

    public static Color4f fromColor(Color4f color, float alpha)
    {
        return new Color4f(color.r, color.g, color.b, alpha);
    }

    public static int getColorFromHue(int hue)
    {
        return 0xFF000000 | (Color.HSBtoRGB((float) (hue % 360) / 360f, 1f, 1f) & 0x00FFFFFF);
    }

    /**
     * Returns the hex color string with a hashtag in front (in the format "#30505050")
     * @param color
     * @return
     */
    public static String getHexColorString(int color)
    {
        return String.format("#%08X", color);
    }

    public static float[] convertRgb2Hsv(int color)
    {
        float[] hsv = new float[3];
        int r = ((color >>> 16) & 0xFF);
        int g = ((color >>>  8) & 0xFF);
        int b = ( color         & 0xFF);

        Color.RGBtoHSB(r, g, b, hsv);

        return hsv;
    }
}

package fi.dy.masa.malilib.util.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color4f
{
    public static final Pattern HEX_8 = Pattern.compile("(?:0x|#)([a-fA-F0-9]{8})");
    public static final Pattern HEX_6 = Pattern.compile("(?:0x|#)([a-fA-F0-9]{6})");
    public static final Pattern HEX_4 = Pattern.compile("(?:0x|#)([a-fA-F0-9]{4})");
    public static final Pattern HEX_3 = Pattern.compile("(?:0x|#)([a-fA-F0-9]{3})");

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

    @Override
    public String toString()
    {
        return String.format("Color4f{hex=%s, a = %f, r = %f, g = %f, b = %f, intValue = %d}",
                             getHexColorString(this.intValue), this.a, this.r, this.g, this.b, this.intValue);
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
        return 0xFF000000 | (java.awt.Color.HSBtoRGB((float) (hue % 360) / 360f, 1f, 1f) & 0x00FFFFFF);
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

    /**
     * Tries to parse the given string as a hexadecimal value,
     * if it begins with '#' or '0x'.<br>
     * Accepts 8, 6, 4 or 3 digits long values.
     * The 4 and 3 digits long values will repeat each digit for each color channel,
     * so for example F159 will become FF115599.<br>
     * The 6 and 3 long versions will use 0xFF for the alpha channel.<br>
     * If the hex parsing fails, then the input it attempted to be parsed as a regular base 10 integer.
     * @param colorStr
     * @param defaultColor the fallback color if the parsing fails
     * @return the parsed color as an AARRGGBB int, or the fallback color if the parsing fails
     */
    public static int getColorFromString(String colorStr, int defaultColor)
    {
        Matcher matcher = HEX_8.matcher(colorStr);

        if (matcher.matches())
        {
            try
            {
                return (int) Long.parseLong(matcher.group(1), 16);
            }
            catch (NumberFormatException ignore) {}
        }

        matcher = HEX_6.matcher(colorStr);

        if (matcher.matches())
        {
            try
            {
                return 0xFF000000 | (int) Long.parseLong(matcher.group(1), 16);
            }
            catch (NumberFormatException ignore) { return defaultColor; }
        }

        matcher = HEX_4.matcher(colorStr);

        if (matcher.matches())
        {
            try
            {
                String str = matcher.group(1);
                int orig = Integer.parseInt(str, 16);
                int a = ((orig & 0xF000) >>> 12) * 17;
                int r = ((orig & 0x0F00) >>>  8) * 17;
                int g = ((orig & 0x00F0) >>>  4) * 17;
                int b = ((orig & 0x000F)       ) * 17;
                return a << 24 | r << 16 | g << 8 | b;
            }
            catch (NumberFormatException ignore) {}
        }

        matcher = HEX_3.matcher(colorStr);

        if (matcher.matches())
        {
            try
            {
                String str = matcher.group(1);
                int orig = Integer.parseInt(str, 16);
                int r = ((orig & 0x0F00) >>>  8) * 17;
                int g = ((orig & 0x00F0) >>>  4) * 17;
                int b = ((orig & 0x000F)       ) * 17;
                return 0xFF000000 | r << 16 | g << 8 | b;
            }
            catch (NumberFormatException ignore) {}
        }

        try { return Integer.parseInt(colorStr, 10); }
        catch (NumberFormatException e) { return defaultColor; }
    }

    public static float[] convertRgb2Hsv(int color)
    {
        float[] hsv = new float[3];
        int r = ((color >>> 16) & 0xFF);
        int g = ((color >>>  8) & 0xFF);
        int b = ( color         & 0xFF);

        java.awt.Color.RGBtoHSB(r, g, b, hsv);

        return hsv;
    }
}

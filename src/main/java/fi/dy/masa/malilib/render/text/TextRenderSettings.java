package fi.dy.masa.malilib.render.text;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class TextRenderSettings
{
    protected boolean useBackground;
    protected boolean useOddEvenBackground;
    protected boolean useEvenWidthBackground;
    protected boolean useTextShadow = true;
    protected int backgroundColor = 0xA0505050;
    protected int backgroundColorOdd = 0x70A0A0A0;
    protected int textColor = 0xFFFFFFFF;

    public boolean getUseBackground()
    {
        return this.useBackground;
    }

    public boolean getUseOddEvenBackground()
    {
        return this.useOddEvenBackground;
    }

    public boolean getUseEvenWidthBackground()
    {
        return this.useEvenWidthBackground;
    }

    public boolean getUseTextShadow()
    {
        return this.useTextShadow;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public int getOddRowBackgroundColor()
    {
        return this.backgroundColorOdd;
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public void setUseBackground(boolean useBackground)
    {
        this.useBackground = useBackground;
    }

    public void setUseOddEvenBackground(boolean useOddEvenBackground)
    {
        this.useOddEvenBackground = useOddEvenBackground;
    }

    public void setUseEvenWidthBackground(boolean useEvenWidthBackground)
    {
        this.useEvenWidthBackground = useEvenWidthBackground;
    }

    public void setUseTextShadow(boolean useTextShadow)
    {
        this.useTextShadow = useTextShadow;
    }

    public void setBackgroundColor(int color)
    {
        this.backgroundColor = color;
    }

    public void setOddRowBackgroundColor(int color)
    {
        this.backgroundColorOdd = color;
    }

    public void setTextColor(int color)
    {
        this.textColor = color;
    }

    public void toggleUseBackground()
    {
        this.useBackground = ! this.useBackground;
    }

    public void toggleUseOddEvenBackground()
    {
        this.useOddEvenBackground = ! this.useOddEvenBackground;
    }

    public void toggleUseEvenWidthBackground()
    {
        this.useEvenWidthBackground = ! this.useEvenWidthBackground;
    }

    public void toggleUseTextShadow()
    {
        this.useTextShadow = ! this.useTextShadow;
    }

    public void setFrom(TextRenderSettings other)
    {
        this.useBackground = other.useBackground;
        this.useOddEvenBackground = other.useOddEvenBackground;
        this.useEvenWidthBackground = other.useEvenWidthBackground;
        this.useTextShadow = other.useTextShadow;
        this.backgroundColor = other.backgroundColor;
        this.backgroundColorOdd = other.backgroundColorOdd;
        this.textColor = other.textColor;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("bg_enabled", this.useBackground);
        obj.addProperty("bg_odd_even", this.useOddEvenBackground);
        obj.addProperty("bg_even_width", this.useOddEvenBackground);
        obj.addProperty("bg_color", this.backgroundColor);
        obj.addProperty("bg_color_odd", this.backgroundColorOdd);
        obj.addProperty("text_shadow", this.useTextShadow);
        obj.addProperty("text_color", this.textColor);


        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.useBackground = JsonUtils.getBooleanOrDefault(obj, "bg_enabled", false);
        this.useOddEvenBackground = JsonUtils.getBooleanOrDefault(obj, "bg_odd_even", false);
        this.useEvenWidthBackground = JsonUtils.getBooleanOrDefault(obj, "bg_even_width", false);
        this.backgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", 0xA0505050);
        this.backgroundColorOdd = JsonUtils.getIntegerOrDefault(obj, "bg_color_odd", 0x70A0A0A0);
        this.useTextShadow = JsonUtils.getBooleanOrDefault(obj, "text_shadow", true);
        this.textColor = JsonUtils.getIntegerOrDefault(obj, "text_color", 0xFFFFFFFF);
    }
}

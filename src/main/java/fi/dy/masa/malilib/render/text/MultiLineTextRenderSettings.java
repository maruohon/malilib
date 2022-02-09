package fi.dy.masa.malilib.render.text;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class MultiLineTextRenderSettings extends TextRenderSettings
{
    protected boolean oddEvenBackgroundEnabled;
    protected boolean evenWidthBackgroundEnabled;
    protected int oddRowBackgroundColor = 0x70A0A0A0;

    public MultiLineTextRenderSettings()
    {
        this.lineHeight = TextRenderer.INSTANCE.getLineHeight();
    }

    public boolean getOddEvenBackgroundEnabled()
    {
        return this.oddEvenBackgroundEnabled;
    }

    public boolean getEvenWidthBackgroundEnabled()
    {
        return this.evenWidthBackgroundEnabled;
    }

    public int getOddRowBackgroundColor()
    {
        return this.oddRowBackgroundColor;
    }

    public void setOddEvenBackgroundEnabled(boolean oddEvenBackgroundEnabled)
    {
        this.oddEvenBackgroundEnabled = oddEvenBackgroundEnabled;
    }

    public void setEvenWidthBackgroundEnabled(boolean evenWidthBackgroundEnabled)
    {
        this.evenWidthBackgroundEnabled = evenWidthBackgroundEnabled;
    }

    public void setOddRowBackgroundColor(int color)
    {
        this.oddRowBackgroundColor = color;
    }

    public void toggleUseOddEvenBackground()
    {
        this.oddEvenBackgroundEnabled = ! this.oddEvenBackgroundEnabled;
    }

    public void toggleUseEvenWidthBackground()
    {
        this.evenWidthBackgroundEnabled = ! this.evenWidthBackgroundEnabled;
    }

    public void setFrom(MultiLineTextRenderSettings other)
    {
        super.setFrom(other);

        this.oddEvenBackgroundEnabled = other.oddEvenBackgroundEnabled;
        this.evenWidthBackgroundEnabled = other.evenWidthBackgroundEnabled;
        this.oddRowBackgroundColor = other.oddRowBackgroundColor;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("bg_odd_even", this.oddEvenBackgroundEnabled);
        obj.addProperty("bg_even_width", this.oddEvenBackgroundEnabled);
        obj.addProperty("bg_color_odd", this.oddRowBackgroundColor);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.oddEvenBackgroundEnabled = JsonUtils.getBooleanOrDefault(obj, "bg_odd_even", false);
        this.evenWidthBackgroundEnabled = JsonUtils.getBooleanOrDefault(obj, "bg_even_width", false);
        this.oddRowBackgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color_odd", 0x70A0A0A0);
    }
}

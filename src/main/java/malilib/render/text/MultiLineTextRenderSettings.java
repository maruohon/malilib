package malilib.render.text;

import com.google.gson.JsonObject;
import malilib.util.data.json.JsonUtils;

public class MultiLineTextRenderSettings extends TextRenderSettings
{
    protected static final int DEFAULT_ODD_ROW_BG_COLOR = 0x70A0A0A0;

    protected boolean oddEvenBackgroundEnabled;
    protected boolean evenWidthBackgroundEnabled;
    protected int oddRowBackgroundColor = DEFAULT_ODD_ROW_BG_COLOR;

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

    /**
     * @return true if any of the values are not at the default values
     */
    @Override
    public boolean isModified()
    {
        return super.isModified() ||
               this.oddEvenBackgroundEnabled ||
               this.evenWidthBackgroundEnabled ||
               this.oddRowBackgroundColor != DEFAULT_ODD_ROW_BG_COLOR;
    }

    public void writeToJsonIfModified(JsonObject obj, String keyName)
    {
        if (this.isModified())
        {
            obj.add(keyName, this.toJsonModifiedOnly());
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("bg_odd_even", this.oddEvenBackgroundEnabled);
        obj.addProperty("bg_even_width", this.evenWidthBackgroundEnabled);
        obj.addProperty("bg_color_odd", this.oddRowBackgroundColor);

        return obj;
    }

    @Override
    public JsonObject toJsonModifiedOnly()
    {
        JsonObject obj = super.toJsonModifiedOnly();

        if (this.oddEvenBackgroundEnabled)   { obj.addProperty("bg_odd_even", this.oddEvenBackgroundEnabled); }
        if (this.evenWidthBackgroundEnabled) { obj.addProperty("bg_even_width", this.evenWidthBackgroundEnabled); }
        if (this.oddRowBackgroundColor != DEFAULT_ODD_ROW_BG_COLOR) { obj.addProperty("bg_color_odd", this.oddRowBackgroundColor); }

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.oddEvenBackgroundEnabled = JsonUtils.getBooleanOrDefault(obj, "bg_odd_even", false);
        this.evenWidthBackgroundEnabled = JsonUtils.getBooleanOrDefault(obj, "bg_even_width", false);
        this.oddRowBackgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color_odd", DEFAULT_ODD_ROW_BG_COLOR);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }
        if (! super.equals(o)) { return false; }

        MultiLineTextRenderSettings that = (MultiLineTextRenderSettings) o;

        if (this.oddEvenBackgroundEnabled != that.oddEvenBackgroundEnabled) { return false; }
        if (this.evenWidthBackgroundEnabled != that.evenWidthBackgroundEnabled) { return false; }
        return this.oddRowBackgroundColor == that.oddRowBackgroundColor;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (this.oddEvenBackgroundEnabled ? 1 : 0);
        result = 31 * result + (this.evenWidthBackgroundEnabled ? 1 : 0);
        result = 31 * result + this.oddRowBackgroundColor;
        return result;
    }
}

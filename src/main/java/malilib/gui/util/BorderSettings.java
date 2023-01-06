package malilib.gui.util;

import java.util.Objects;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;

import malilib.listener.EventListener;
import malilib.util.data.EdgeInt;
import malilib.util.data.json.JsonUtils;

public class BorderSettings
{
    protected final EdgeInt borderColor;
    @Nullable protected EventListener sizeChangeListener;
    protected boolean defaultEnabled;
    protected boolean enabled;
    protected int defaultBorderWidth;
    protected int borderWidth;

    public BorderSettings()
    {
        this(0xFFFFFFFF);
    }

    public BorderSettings(int defaultColor)
    {
        this(defaultColor, 1);
    }

    public BorderSettings(int defaultColor, int borderWidth)
    {
        this.borderColor = new EdgeInt(defaultColor);
        this.borderWidth = borderWidth;
        this.defaultBorderWidth = this.borderWidth;
    }

    public BorderSettings setSizeChangeListener(@Nullable EventListener sizeChangeListener)
    {
        this.sizeChangeListener = sizeChangeListener;
        return this;
    }

    /**
     * Sets the default enabled and borderWidth value, which are used for checking
     * if the values have changed later on, and if the values should get serialized
     * in writeToJsonIfModified(). This also sets the current values.
     */
    public BorderSettings setDefaults(boolean enabled, int borderWidth, int borderColor)
    {
        this.defaultEnabled = enabled;
        this.enabled = enabled;
        this.defaultBorderWidth = borderWidth;
        this.borderWidth = borderWidth;
        this.borderColor.setAllDefaults(borderColor);
        return this;
    }

    public BorderSettings setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public BorderSettings setBorderWidth(int borderWidth)
    {
        this.borderWidth = borderWidth;
        this.setEnabled(borderWidth > 0);
        this.updateSize();
        return this;
    }

    public BorderSettings setColor(EdgeInt borderColor)
    {
        this.borderColor.setFrom(borderColor);
        return this;
    }

    public BorderSettings setColor(int borderColor)
    {
        this.borderColor.setAll(borderColor);
        return this;
    }

    public BorderSettings setBorderWidthAndColor(int borderWidth, int color)
    {
        this.setBorderWidth(borderWidth);
        this.setColor(color);
        return this;
    }

    public void toggleEnabled()
    {
        this.enabled = ! this.enabled;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getBorderWidth()
    {
        return this.borderWidth;
    }

    public int getActiveBorderWidth()
    {
        return this.enabled ? this.borderWidth : 0;
    }

    public EdgeInt getColor()
    {
        return this.borderColor;
    }

    protected void updateSize()
    {
        if (this.sizeChangeListener != null)
        {
            this.sizeChangeListener.onEvent();
        }
    }

    public boolean isModified()
    {
        return this.defaultEnabled != this.enabled ||
               this.defaultBorderWidth != this.borderWidth ||
               this.borderColor.isModified();
    }

    public void writeToJsonIfModified(JsonObject obj, String keyName)
    {
        if (this.isModified())
        {
            obj.add(keyName, this.toJsonModifiedOnly());
        }
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("enabled", this.enabled);
        obj.addProperty("width", this.borderWidth);
        obj.add("color", this.borderColor.toJson());

        return obj;
    }

    public JsonObject toJsonModifiedOnly()
    {
        JsonObject obj = new JsonObject();

        if (this.enabled != this.defaultEnabled)
        {
            obj.addProperty("enabled", this.enabled);
        }

        if (this.borderWidth != this.defaultBorderWidth)
        {
            obj.addProperty("width", this.borderWidth);
        }

        this.borderColor.writeToJsonIfModified(obj, "color");

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", this.enabled);
        this.borderWidth = JsonUtils.getIntegerOrDefault(obj, "width", this.borderWidth);
        JsonUtils.getArrayIfExists(obj, "color", this.borderColor::fromJson);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        BorderSettings that = (BorderSettings) o;

        if (this.enabled != that.enabled) { return false; }
        if (this.borderWidth != that.borderWidth) { return false; }
        return Objects.equals(this.borderColor, that.borderColor);
    }

    @Override
    public int hashCode()
    {
        int result = this.borderColor.hashCode();
        result = 31 * result + (this.enabled ? 1 : 0);
        result = 31 * result + this.borderWidth;
        return result;
    }
}

package malilib.gui.util;

import com.google.gson.JsonObject;
import malilib.util.data.json.JsonUtils;

public class BackgroundSettings
{
    protected boolean defaultEnabled;
    protected boolean enabled;
    protected int defaultColor;
    protected int color;

    public BackgroundSettings(int color)
    {
        this.color = color;
    }

    public BackgroundSettings setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public BackgroundSettings setColor(int color)
    {
        this.color = color;
        return this;
    }

    /**
     * Sets both the "default" values and the current values.
     * The default values are used for checking if the values have been modified
     * from the defaults, and if the values need to be serialized/saved.
     */
    public BackgroundSettings setDefaultEnabledAndColor(boolean enabled, int color)
    {
        this.defaultEnabled = enabled;
        this.enabled = enabled;
        this.defaultColor = color;
        this.color = color;
        return this;
    }

    public BackgroundSettings setEnabledAndColor(boolean enabled, int color)
    {
        this.enabled = enabled;
        this.color = color;
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

    public int getColor()
    {
        return this.color;
    }

    public boolean isModified()
    {
        return this.enabled != this.defaultEnabled ||
               this.color != this.defaultColor;
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
        obj.addProperty("color", this.color);
        return obj;
    }

    public JsonObject toJsonModifiedOnly()
    {
        JsonObject obj = new JsonObject();

        if (this.enabled != this.defaultEnabled)
        {
            obj.addProperty("enabled", this.enabled);
        }
        if (this.color != this.defaultColor)
        {
            obj.addProperty("color", this.color);
        }

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", this.enabled);
        this.color = JsonUtils.getIntegerOrDefault(obj, "color", this.color);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        BackgroundSettings that = (BackgroundSettings) o;

        if (this.enabled != that.enabled) { return false; }
        return this.color == that.color;
    }

    @Override
    public int hashCode()
    {
        int result = (this.enabled ? 1 : 0);
        result = 31 * result + this.color;
        return result;
    }
}

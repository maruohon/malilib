package fi.dy.masa.malilib.gui.util;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class BackgroundSettings
{
    protected boolean enabled;
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

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("enabled", this.isEnabled());
        obj.addProperty("color", this.getColor());
        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", this.enabled);
        this.color = JsonUtils.getIntegerOrDefault(obj, "color", this.color);
    }
}

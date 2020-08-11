package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;

public class ColorConfig extends IntegerConfig
{
    protected Color4f color;

    public ColorConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public ColorConfig(String name, String defaultValue, String comment)
    {
        super(name, StringUtils.getColor(defaultValue, 0), comment);

        this.color = Color4f.fromColor(this.getIntegerValue());
    }

    public Color4f getColor()
    {
        return this.color;
    }

    @Override
    public String getStringValue()
    {
        return Color4f.getHexColorString(this.getIntegerValue());
    }

    @Override
    public String getDefaultStringValue()
    {
        return Color4f.getHexColorString(this.getDefaultIntegerValue());
    }

    @Override
    public void setValueFromString(String value)
    {
        this.setIntegerValue(StringUtils.getColor(value, 0));
    }

    @Override
    public void setIntegerValue(int value)
    {
        this.color = Color4f.fromColor(value);

        super.setIntegerValue(value); // This also calls the callback, if set
    }

    @Override
    public boolean isModified(String newValue)
    {
        try
        {
            return StringUtils.getColor(newValue, 0) != this.getDefaultIntegerValue();
        }
        catch (Exception ignore)
        {
        }

        return true;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = this.getClampedValue(StringUtils.getColor(element.getAsString(), 0));
                this.color = Color4f.fromColor(this.value);
                this.onValueLoaded(this.value);
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.getStringValue());
    }
}

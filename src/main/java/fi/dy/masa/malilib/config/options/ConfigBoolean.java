package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigBoolean extends ConfigBase<ConfigBoolean> implements IConfigBoolean
{
    private final boolean defaultValue;
    private boolean value;
    private boolean lastSavedValue;

    public ConfigBoolean(String name, boolean defaultValue, String comment)
    {
        this(name, defaultValue, comment, name);
    }

    public ConfigBoolean(String name, boolean defaultValue, String comment, String prettyName)
    {
        super(ConfigType.BOOLEAN, name, comment, prettyName);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.value;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        boolean oldValue = this.value;
        this.value = value;

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
    }

    @Override
    public boolean isModified()
    {
        return this.value != this.defaultValue;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return Boolean.parseBoolean(newValue) != this.defaultValue;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedValue != this.value;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValue = this.value;
    }

    @Override
    public void resetToDefault()
    {
        this.value = this.defaultValue;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.value);
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValue);
    }

    @Override
    public void setValueFromString(String value)
    {
        this.value = Boolean.parseBoolean(value);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = element.getAsBoolean();
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}

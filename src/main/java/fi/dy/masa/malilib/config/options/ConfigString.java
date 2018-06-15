package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigString extends ConfigBase
{
    private final String defaultValue;
    private String value;

    public ConfigString(String name, String defaultValue, String comment)
    {
        super(ConfigType.STRING, name, comment);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @Override
    public String getStringValue()
    {
        return this.value;
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setValueFromString(String value)
    {
        this.value = value;
    }

    @Override
    public void resetToDefault()
    {
        this.value = this.defaultValue;
    }

    @Override
    public boolean isModified()
    {
        return this.value.equals(this.defaultValue) == false;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.defaultValue.equals(newValue) == false;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = element.getAsString();
            }
            else
            {
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}

package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;

public class ConfigBoolean extends ConfigBase implements IConfigBoolean
{
    private final String prettyName;
    private final boolean defaultValue;
    private boolean value;

    public ConfigBoolean(String name, boolean defaultValue, String comment)
    {
        this(name, defaultValue, comment, name);
    }

    public ConfigBoolean(String name, boolean defaultValue, String comment, String prettyName)
    {
        super(ConfigType.BOOLEAN, name, comment);

        this.prettyName = prettyName;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @Override
    public String getPrettyName()
    {
        return this.prettyName;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.value;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        this.value = value;
    }

    public boolean getDefaultValue()
    {
        return this.defaultValue;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.value);
    }

    @Override
    public void setValueFromString(String value)
    {
        this.value = Boolean.getBoolean(value);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                this.value = primitive.getAsBoolean();
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

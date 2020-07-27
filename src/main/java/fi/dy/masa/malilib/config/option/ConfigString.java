package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigString extends ConfigStringBase<String>
{
    public ConfigString(String name, String defaultValue, String comment)
    {
        super(ConfigType.STRING, name, defaultValue, comment);
    }

    @Override
    public void setValueFromString(String value)
    {
        if (this.value.equals(value) == false)
        {
            String oldValue = this.value;
            this.value = value;
            this.onValueChanged(value, oldValue);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = element.getAsString();
            }
            else
            {
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }
}

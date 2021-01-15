package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLib;

public class StringConfig extends BaseStringConfig<String>
{
    public StringConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringConfig(String name, String defaultValue, String comment)
    {
        super(name, defaultValue, comment);

        this.stringValue = defaultValue;
    }

    @Override
    public void setValue(String newValue)
    {
        this.stringValue = newValue;
        super.setValue(newValue);
    }

    @Override
    public void setValueFromString(String newValue)
    {
        this.setValue(newValue);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.stringValue = element.getAsString();
                this.value = this.stringValue;
                this.onValueLoaded(this.stringValue);
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
}

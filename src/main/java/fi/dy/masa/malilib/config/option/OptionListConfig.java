package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;

public class OptionListConfig<T extends ConfigOptionListEntry<T>> extends BaseGenericConfig<T>
{
    public OptionListConfig(String name, T defaultValue)
    {
        this(name, defaultValue, name);
    }

    public OptionListConfig(String name, T defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public OptionListConfig(String name, T defaultValue, String prettyName, String comment)
    {
        super(name, defaultValue, name, prettyName, comment);
    }

    public void cycleValue(boolean forward)
    {
        this.setValue(this.value.cycle(forward));
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = this.value.fromString(element.getAsString());
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
        return new JsonPrimitive(this.value.getStringValue());
    }
}

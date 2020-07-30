package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;

public class OptionListConfig<T extends IConfigOptionListEntry<T>> extends BaseConfig<T>
{
    protected final T defaultValue;
    protected T value;
    protected T lastSavedValue;

    public OptionListConfig(String name, T defaultValue, String comment)
    {
        this(name, defaultValue, comment, name);
    }

    public OptionListConfig(String name, T defaultValue, String comment, String prettyName)
    {
        super(ConfigType.OPTION_LIST, name, comment, prettyName);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
    }

    public T getOptionListValue()
    {
        return this.value;
    }

    public T getDefaultOptionListValue()
    {
        return this.defaultValue;
    }

    public void setOptionListValue(T value)
    {
        T oldValue = this.value;
        this.value = value;

        if (oldValue != this.value)
        {
            this.onValueChanged(value, oldValue);
        }
    }

    public void cycleValue(boolean forward)
    {
        this.setOptionListValue(this.value.cycle(forward));
    }

    @Override
    public boolean isModified()
    {
        return this.value != this.defaultValue;
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
        this.setOptionListValue(this.defaultValue);
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
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value.getStringValue());
    }
}

package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public class ConfigOptionList<T extends IConfigOptionListEntry<T>> extends ConfigBase<T> implements IConfigOptionList<T>, IStringRepresentable
{
    private final T defaultValue;
    private T value;
    private T lastSavedValue;

    public ConfigOptionList(String name, T defaultValue, String comment)
    {
        this(name, defaultValue, comment, name);
    }

    public ConfigOptionList(String name, T defaultValue, String comment, String prettyName)
    {
        super(ConfigType.OPTION_LIST, name, comment, prettyName);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
    }

    @Override
    public T getOptionListValue()
    {
        return this.value;
    }

    @Override
    public T getDefaultOptionListValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setOptionListValue(T value)
    {
        T oldValue = this.value;
        this.value = value;

        if (oldValue != this.value)
        {
            this.onValueChanged(value, oldValue);
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
        try
        {
            return this.value.fromString(newValue) != this.defaultValue;
        }
        catch (Exception e)
        {
        }

        return true;
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
    public String getStringValue()
    {
        return this.value.getStringValue();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.defaultValue.getStringValue();
    }

    @Override
    public void setValueFromString(String value)
    {
        this.value = this.value.fromString(value);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.setValueFromString(element.getAsString());
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
        return new JsonPrimitive(this.getStringValue());
    }
}

package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;

public abstract class ConfigStringBase<T> extends ConfigBase<T> implements IConfigValue
{
    protected final String defaultValue;
    protected String value;
    protected String lastSavedValue;

    protected ConfigStringBase(ConfigType type, String name, String defaultValue, String comment)
    {
        super(type, name, comment);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
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
    public void resetToDefault()
    {
        this.setValueFromString(this.defaultValue);
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
    public boolean isDirty()
    {
        return this.lastSavedValue.equals(this.value) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValue = this.value;
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.getStringValue());
    }
}

package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;

public abstract class BaseStringConfig<T> extends BaseConfig<T>
{
    protected final String defaultValue;
    protected String value;
    protected String lastSavedValue;

    protected BaseStringConfig(ConfigType type, String name, String defaultValue, String comment)
    {
        super(type, name, comment);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.cacheSavedValue();
    }

    public String getStringValue()
    {
        return this.value;
    }

    public String getDefaultStringValue()
    {
        return this.defaultValue;
    }

    public abstract void setValueFromString(String newValue);

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

package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class BaseStringConfig<T> extends BaseConfig<T>
{
    protected final String defaultValue;
    protected String value;
    protected String lastSavedValue;

    protected BaseStringConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    protected BaseStringConfig(String name, String defaultValue, String comment)
    {
        super(name, comment);

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

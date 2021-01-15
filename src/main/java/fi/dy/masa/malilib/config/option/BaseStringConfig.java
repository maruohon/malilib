package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class BaseStringConfig<T> extends BaseGenericConfig<T>
{
    protected String stringValue;

    protected BaseStringConfig(String name, T defaultValue)
    {
        this(name, defaultValue, name);
    }

    protected BaseStringConfig(String name, T defaultValue, String comment)
    {
        super(name, defaultValue, comment);
    }

    public String getStringValue()
    {
        return this.stringValue;
    }

    public abstract void setValueFromString(String newValue);

    public boolean isModified(String newValue)
    {
        return this.defaultValue.equals(newValue) == false;
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.getStringValue());
    }
}

package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;

public class BooleanConfig extends BaseGenericConfig<Boolean>
{
    protected boolean booleanValue;

    public BooleanConfig(String name, boolean defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BooleanConfig(String name, boolean defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public BooleanConfig(String name, boolean defaultValue, String prettyName, String comment)
    {
        super(name, defaultValue, name, prettyName, comment);

        this.booleanValue = defaultValue;
    }

    public boolean getBooleanValue()
    {
        return this.booleanValue;
    }

    public void toggleBooleanValue()
    {
        this.setValue(! this.booleanValue);
    }

    @Override
    public void setValue(Boolean newValue)
    {
        this.booleanValue = newValue;

        super.setValue(newValue);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.booleanValue = element.getAsBoolean();
                this.value = this.booleanValue;
                this.onValueLoaded(this.booleanValue);
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
        return new JsonPrimitive(this.booleanValue);
    }
}

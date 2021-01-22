package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class BooleanConfig extends BaseGenericConfig<Boolean>
{
    protected boolean booleanValue;
    protected boolean hasOverride;
    protected boolean overrideValue;

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
        return this.hasOverride ? this.overrideValue : this.booleanValue;
    }

    @Override
    public Boolean getValue()
    {
        return this.hasOverride ? this.overrideValue : this.value;
    }

    public void toggleBooleanValue()
    {
        this.setValue(! this.booleanValue);
    }

    @Override
    public boolean setValue(Boolean newValue)
    {
        if (this.locked == false)
        {
            this.booleanValue = newValue;
            return super.setValue(newValue);
        }

        return false;
    }

    @Override
    public boolean isLocked()
    {
        return super.isLocked() || this.hasOverride;
    }

    public boolean isOverridden()
    {
        return this.hasOverride;
    }

    public void setOverride(boolean overrideEnabled, boolean overrideValue)
    {
        this.hasOverride = overrideEnabled;
        this.overrideValue = overrideValue;
    }

    @Override
    protected void rebuildLockOverrideMessages()
    {
        super.rebuildLockOverrideMessages();

        if (this.hasOverride && this.overrideMessage != null)
        {
            this.lockOverrideMessages.add(StringUtils.translate(this.overrideMessage));
        }
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

package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class BooleanConfig extends BaseGenericConfig<Boolean> implements BooleanStorage
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

    @Override
    public boolean getBooleanValue()
    {
        return this.hasOverride ? this.overrideValue : this.booleanValue;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        if (this.locked == false)
        {
            this.booleanValue = value;
            super.setValue(value);
        }
    }

    @Override
    public Boolean getValue()
    {
        return this.hasOverride ? this.overrideValue : this.value;
    }

    public void toggleBooleanValue()
    {
        this.setBooleanValue(! this.booleanValue);
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
    public void loadValueFromConfig(Boolean value)
    {
        this.booleanValue = value;
        super.loadValueFromConfig(value);
    }
}

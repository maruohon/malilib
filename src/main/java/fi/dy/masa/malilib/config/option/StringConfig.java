package fi.dy.masa.malilib.config.option;

public class StringConfig extends BaseStringConfig<String>
{
    public StringConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringConfig(String name, String defaultValue, String comment)
    {
        super(name, defaultValue, comment);

        this.stringValue = defaultValue;
    }

    @Override
    public boolean setValue(String newValue)
    {
        if (this.locked == false)
        {
            this.stringValue = newValue;
            return super.setValue(newValue);
        }

        return false;
    }

    @Override
    public void setValueFromString(String newValue)
    {
        this.setValue(newValue);
    }

    public void loadStringValueFromConfig(String value)
    {
        this.stringValue = value;
        this.value = value;
        this.cacheSavedValue();
        this.updateEffectiveValue();
        this.onValueLoaded(this.effectiveValue);
    }
}

package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.util.data.Color4f;

public class ColorConfig extends IntegerConfig
{
    protected Color4f color;

    public ColorConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public ColorConfig(String name, String defaultValue, String comment)
    {
        super(name, Color4f.getColorFromString(defaultValue, 0), comment);

        this.color = Color4f.fromColor(this.getIntegerValue());
    }

    public Color4f getColor()
    {
        return this.color;
    }

    @Override
    public String getStringValue()
    {
        return Color4f.getHexColorString(this.getIntegerValue());
    }

    @Override
    public String getDefaultStringValue()
    {
        return Color4f.getHexColorString(this.getDefaultIntegerValue());
    }

    @Override
    public void setValueFromString(String value)
    {
        this.setValue(Color4f.getColorFromString(value, 0));
    }

    @Override
    public boolean setValue(Integer newValue)
    {
        if (this.locked == false)
        {
            this.color = Color4f.fromColor(newValue);
            return super.setValue(newValue); // This also calls the callback, if set
        }

        return false;
    }

    @Override
    public boolean isModified(String newValue)
    {
        try
        {
            return Color4f.getColorFromString(newValue, 0) != this.getDefaultIntegerValue();
        }
        catch (Exception ignore)
        {
        }

        return true;
    }

    public void loadColorValueFromString(String value)
    {
        this.integerValue = this.getClampedValue(Color4f.getColorFromString(value, 0));
        this.value = this.integerValue;
        this.color = Color4f.fromColor(this.integerValue);
        this.cacheSavedValue();
        this.onValueLoaded(this.integerValue);
    }
}

package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.util.data.Color4f;

public class ColorConfig extends BaseGenericConfig<Color4f>
{
    public ColorConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public ColorConfig(String name, String defaultValue,
                       String commentTranslationKey, Object... commentArgs)
    {
        super(name, Color4f.fromString(defaultValue), commentTranslationKey, commentArgs);
    }

    public Color4f getColor()
    {
        return this.value;
    }

    public int getIntegerValue()
    {
        return this.value.intValue;
    }

    public void setValueFromInt(int newValue)
    {
        this.setValue(Color4f.fromColor(newValue));
    }

    public String getStringValue()
    {
        return Color4f.getHexColorString(this.effectiveValue.intValue);
    }

    public void setValueFromString(String value)
    {
        this.setValue(Color4f.fromString(value));
    }

    public void loadColorValueFromString(String value)
    {
        this.value = Color4f.fromString(value);
        this.cacheSavedValue();
        this.updateEffectiveValue();
        this.onValueLoaded(this.effectiveValue);
    }
}

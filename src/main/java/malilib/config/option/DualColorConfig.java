package malilib.config.option;

import org.apache.commons.lang3.tuple.Pair;

import malilib.util.data.Color4f;

public class DualColorConfig extends BaseGenericConfig<Pair<Color4f, Color4f>>
{
    protected String firstColorHoverInfoKey = "?";
    protected String secondColorHoverInfoKey = "?";

    public DualColorConfig(String name, String defaultValue1, String defaultValue2)
    {
        this(name, defaultValue1, defaultValue2, name);
    }

    public DualColorConfig(String name, String defaultValue1, String defaultValue2,
                           String commentTranslationKey, Object... commentArgs)
    {
        super(name, Pair.of(Color4f.fromString(defaultValue1), Color4f.fromString(defaultValue2)),
              commentTranslationKey, commentArgs);
    }

    public Color4f getFirstColor()
    {
        return this.value.getLeft();
    }

    public Color4f getSecondColor()
    {
        return this.value.getRight();
    }

    public int getFirstColorInt()
    {
        return this.getFirstColor().intValue;
    }

    public int getSecondColorInt()
    {
        return this.getSecondColor().intValue;
    }

    public void setFirstColorFromInt(int newValue)
    {
        this.setValue(Pair.of(Color4f.fromColor(newValue), this.getSecondColor()));
    }

    public void setSecondColorFromInt(int newValue)
    {
        this.setValue(Pair.of(this.getFirstColor(), Color4f.fromColor(newValue)));
    }

    public void setValueFromStrings(String value1, String value2)
    {
        this.setValue(Pair.of(Color4f.fromString(value1), Color4f.fromString(value2)));
    }

    public void loadColorValueFromInts(int value1, int value2)
    {
        this.value = Pair.of(Color4f.fromColor(value1), Color4f.fromColor(value2));
        this.cacheSavedValue();
        this.updateEffectiveValue();
        this.onValueLoaded(this.effectiveValue);
    }

    public String getFirstColorHoverInfoKey()
    {
        return this.firstColorHoverInfoKey;
    }

    public String getSecondColorHoverInfoKey()
    {
        return this.secondColorHoverInfoKey;
    }

    public DualColorConfig setFirstColorHoverInfoKey(String firstColorHoverInfoKey)
    {
        this.firstColorHoverInfoKey = firstColorHoverInfoKey;
        return this;
    }

    public DualColorConfig setSecondColorHoverInfoKey(String secondColorHoverInfoKey)
    {
        this.secondColorHoverInfoKey = secondColorHoverInfoKey;
        return this;
    }
}

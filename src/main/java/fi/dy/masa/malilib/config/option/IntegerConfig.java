package fi.dy.masa.malilib.config.option;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;
import fi.dy.masa.malilib.util.data.RangedIntegerStorage;

public class IntegerConfig extends BaseSliderConfig<Integer> implements RangedIntegerStorage
{
    protected final int minValue;
    protected final int maxValue;
    protected int effectiveIntegerValue;

    public IntegerConfig(String name, int defaultValue)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, name);
    }

    public IntegerConfig(String name, int defaultValue, String comment)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, comment);
    }

    public IntegerConfig(String name, int defaultValue, int minValue, int maxValue)
    {
        this(name, defaultValue, minValue, maxValue, name);
    }

    public IntegerConfig(String name, int defaultValue, int minValue, int maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment);
    }

    public IntegerConfig(String name, int defaultValue, int minValue, int maxValue, boolean sliderActive, String comment)
    {
        super(name, defaultValue, comment, sliderActive);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.updateEffectiveValue();
        this.sliderCallbackFactory = (listener) -> new IntegerSliderCallback(this, listener);
    }

    @Override
    public int getIntegerValue()
    {
        return this.effectiveIntegerValue;
    }

    public int getDefaultIntegerValue()
    {
        return this.defaultValue;
    }

    @Override
    public boolean setValue(Integer newValue)
    {
        newValue = this.getClampedValue(newValue);
        return super.setValue(newValue);
    }

    @Override
    public boolean setIntegerValue(int newValue)
    {
        return this.setValue(newValue);
    }

    @Override
    protected void updateEffectiveValue()
    {
        super.updateEffectiveValue();
        this.effectiveIntegerValue = this.effectiveValue;
    }

    @Override
    public int getMinIntegerValue()
    {
        return this.minValue;
    }

    @Override
    public int getMaxIntegerValue()
    {
        return this.maxValue;
    }

    protected int getClampedValue(int value)
    {
        return MathHelper.clamp(value, this.minValue, this.maxValue);
    }

    public boolean isModified(String newValue)
    {
        try
        {
            return Integer.parseInt(newValue) != this.defaultValue;
        }
        catch (Exception ignore) {}

        return true;
    }

    public String getStringValue()
    {
        return String.valueOf(this.effectiveIntegerValue);
    }

    public void setValueFromString(String value)
    {
        try
        {
            this.setValue(Integer.parseInt(value));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'", this.getName(), value);
        }
    }
}

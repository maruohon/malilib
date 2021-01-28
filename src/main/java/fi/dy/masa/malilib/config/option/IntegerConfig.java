package fi.dy.masa.malilib.config.option;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;

public class IntegerConfig extends BaseSliderConfig<Integer>
{
    protected int integerValue;
    protected int minValue;
    protected int maxValue;

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

        this.integerValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sliderCallbackFactory = (listener) -> new IntegerSliderCallback(this, listener);
    }

    public int getIntegerValue()
    {
        return this.integerValue;
    }

    public int getDefaultIntegerValue()
    {
        return this.defaultValue;
    }

    @Override
    public boolean setValue(Integer newValue)
    {
        if (this.locked == false)
        {
            newValue = this.getClampedValue(newValue);
            this.integerValue = newValue;
            return super.setValue(newValue);
        }

        return false;
    }

    public int getMinIntegerValue()
    {
        return this.minValue;
    }

    public int getMaxIntegerValue()
    {
        return this.maxValue;
    }

    public void setMinIntegerValue(int minValue)
    {
        this.minValue = minValue;
        this.setValue(this.integerValue);
    }

    public void setMaxIntegerValue(int maxValue)
    {
        this.maxValue = maxValue;
        this.setValue(this.integerValue);
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
        catch (Exception ignore)
        {
        }

        return true;
    }

    public String getStringValue()
    {
        return String.valueOf(this.integerValue);
    }

    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValue);
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

    @Override
    public void loadValueFromConfig(Integer value)
    {
        this.integerValue = value;
        super.loadValueFromConfig(value);
    }
}

package fi.dy.masa.malilib.config.option;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.callback.DoubleSliderCallback;
import fi.dy.masa.malilib.util.data.RangedDoubleStorage;

public class DoubleConfig extends BaseSliderConfig<Double> implements RangedDoubleStorage
{
    protected final double minValue;
    protected final double maxValue;
    protected double effectiveDoubleValue;

    public DoubleConfig(String name, double defaultValue)
    {
        this(name, defaultValue, name);
    }

    public DoubleConfig(String name, double defaultValue, String comment)
    {
        this(name, defaultValue, -10000, 10000, comment);
    }

    public DoubleConfig(String name, double defaultValue, double minValue, double maxValue)
    {
        this(name, defaultValue, minValue, maxValue, name);
    }

    public DoubleConfig(String name, double defaultValue, double minValue, double maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment);
    }

    public DoubleConfig(String name, double defaultValue, double minValue, double maxValue,
                        boolean sliderActive, String comment)
    {
        super(name, defaultValue, comment, sliderActive);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.updateEffectiveValue();
        this.sliderCallbackFactory = (listener) -> new DoubleSliderCallback(this, listener);
    }

    @Override
    public double getDoubleValue()
    {
        return this.effectiveDoubleValue;
    }

    public float getFloatValue()
    {
        return (float) this.effectiveDoubleValue;
    }

    public double getDefaultDoubleValue()
    {
        return this.defaultValue;
    }

    @Override
    public boolean setValue(Double newValue)
    {
        if (Double.isNaN(newValue) == false)
        {
            newValue = this.getClampedValue(newValue);
            return super.setValue(newValue);
        }

        return false;
    }

    @Override
    public boolean setDoubleValue(double newValue)
    {
        return this.setValue(newValue);
    }

    @Override
    protected void updateEffectiveValue()
    {
        super.updateEffectiveValue();
        this.effectiveDoubleValue = this.effectiveValue;
    }

    @Override
    public double getMinDoubleValue()
    {
        return this.minValue;
    }

    @Override
    public double getMaxDoubleValue()
    {
        return this.maxValue;
    }

    protected double getClampedValue(double value)
    {
        return MathHelper.clamp(value, this.minValue, this.maxValue);
    }

    public boolean isModified(String newValue)
    {
        try
        {
            return Double.parseDouble(newValue) != this.defaultValue;
        }
        catch (Exception ignore) {}

        return true;
    }

    public String getStringValue()
    {
        return String.valueOf(this.effectiveDoubleValue);
    }

    public void setValueFromString(String value)
    {
        try
        {
            this.setValue(Double.parseDouble(value));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'", this.getName(), value);
        }
    }
}

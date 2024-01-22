package malilib.config.option;

import java.util.StringJoiner;
import javax.annotation.Nullable;

import malilib.MaLiLib;
import malilib.config.option.BooleanAndDoubleConfig.BooleanAndDouble;
import malilib.gui.callback.DoubleSliderCallback;
import malilib.util.MathUtils;
import malilib.util.data.BooleanStorageWithDefault;
import malilib.util.data.RangedDoubleStorage;

public class BooleanAndDoubleConfig extends BaseBooleanAndNumberConfig<BooleanAndDouble>
        implements RangedDoubleStorage, BooleanStorageWithDefault
{
    protected final double minValue;
    protected final double maxValue;
    protected double effectiveDoubleValue;

    public BooleanAndDoubleConfig(String name, boolean defaultBoolean, double defaultDouble)
    {
        this(name, defaultBoolean, defaultDouble, -10000.0, 10000.0, name);
    }

    public BooleanAndDoubleConfig(String name, boolean defaultBoolean, double defaultDouble,
                                  double minValue, double maxValue)
    {
        this(name, defaultBoolean, defaultDouble, minValue, maxValue, name);
    }

    public BooleanAndDoubleConfig(String name, boolean defaultBoolean, double defaultDouble,
                                  double minValue, double maxValue,
                                  @Nullable String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultBoolean, defaultDouble, minValue, maxValue, false, commentTranslationKey, commentArgs);
    }

    public BooleanAndDoubleConfig(String name, boolean defaultBoolean, double defaultDouble,
                                  double minValue, double maxValue, boolean sliderActive,
                                  @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, new BooleanAndDouble(defaultBoolean, defaultDouble), sliderActive,
              commentTranslationKey, commentArgs);

        this.minValue = minValue;
        this.maxValue = maxValue;

        this.updateEffectiveValue();
        this.cacheSavedValue();
        this.setSliderCallbackFactory(listener -> new DoubleSliderCallback(this, listener));
    }

    public float getFloatValue()
    {
        return (float) this.effectiveDoubleValue;
    }

    @Override
    public double getDoubleValue()
    {
        return this.effectiveDoubleValue;
    }

    @Override
    public boolean setDoubleValue(double newValue)
    {
        BooleanAndDouble oldValue = this.getValue();
        double clampedValue = this.getClampedValue(newValue);
        return this.setValue(new BooleanAndDouble(oldValue.booleanValue, clampedValue));
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

    @Override
    public boolean setValue(BooleanAndDouble newValue)
    {
        double clampedValue = this.getClampedValue(newValue.doubleValue);

        if (clampedValue != newValue.doubleValue)
        {
            newValue = new BooleanAndDouble(newValue.booleanValue, clampedValue);
        }

        return super.setValue(newValue);
    }

    @Override
    protected void updateEffectiveValue()
    {
        super.updateEffectiveValue();
        this.effectiveDoubleValue = this.effectiveValue.doubleValue;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.getValue().booleanValue;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue.booleanValue;
    }

    @Override
    public boolean setBooleanValue(boolean newValue)
    {
        BooleanAndDouble oldValue = this.getValue();
        return this.setValue(new BooleanAndDouble(newValue, oldValue.doubleValue));
    }

    @Override
    public boolean toggleBooleanValue()
    {
        BooleanAndDouble oldValue = this.getValue();
        return this.setValue(new BooleanAndDouble(! oldValue.booleanValue, oldValue.doubleValue));
    }

    protected double getClampedValue(double value)
    {
        return MathUtils.clamp(value, this.minValue, this.maxValue);
    }

    public boolean isModified(String newValue)
    {
        try
        {
            return Double.parseDouble(newValue) != this.defaultValue.doubleValue;
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
            this.setDoubleValue(Double.parseDouble(value));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'", this.getName(), value);
        }
    }

    public static class BooleanAndDouble
    {
        public final boolean booleanValue;
        public final double doubleValue;

        public BooleanAndDouble(boolean booleanValue, double doubleValue)
        {
            this.booleanValue = booleanValue;
            this.doubleValue = doubleValue;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {return true;}
            if (o == null || this.getClass() != o.getClass()) {return false;}

            BooleanAndDouble that = (BooleanAndDouble) o;
            return this.booleanValue == that.booleanValue &&
                   Double.compare(that.doubleValue, this.doubleValue) == 0;
        }

        @Override
        public int hashCode()
        {
            int result;
            long temp;
            result = (this.booleanValue ? 1 : 0);
            temp = Double.doubleToLongBits(this.doubleValue);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString()
        {
            return new StringJoiner(", ", BooleanAndDouble.class.getSimpleName() + "[", "]")
                    .add("booleanValue=" + this.booleanValue)
                    .add("doubleValue=" + this.doubleValue)
                    .toString();
        }
    }
}

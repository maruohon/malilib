package malilib.config.option;

import java.util.StringJoiner;
import malilib.MaLiLib;
import malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import malilib.gui.callback.IntegerSliderCallback;
import malilib.util.data.BooleanStorageWithDefault;
import malilib.util.data.RangedIntegerStorage;
import net.minecraft.util.math.MathHelper;

public class BooleanAndIntConfig extends BaseBooleanAndNumberConfig<BooleanAndInt>
        implements RangedIntegerStorage, BooleanStorageWithDefault
{
    protected final int minValue;
    protected final int maxValue;
    protected int effectiveIntegerValue;

    public BooleanAndIntConfig(String name, boolean defaultBoolean, int defaultInt)
    {
        this(name, defaultBoolean, defaultInt, Integer.MIN_VALUE, Integer.MAX_VALUE, name);
    }

    public BooleanAndIntConfig(String name, boolean defaultBoolean, int defaultInt,
                               int minValue, int maxValue)
    {
        this(name, defaultBoolean, defaultInt, minValue, maxValue, name);
    }

    public BooleanAndIntConfig(String name, boolean defaultBoolean, int defaultInt,
                               int minValue, int maxValue,
                               String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultBoolean, defaultInt, minValue, maxValue, false, commentTranslationKey, commentArgs);
    }

    public BooleanAndIntConfig(String name, boolean defaultBoolean, int defaultInt,
                               int minValue, int maxValue, boolean sliderActive,
                               String commentTranslationKey, Object... commentArgs)
    {
        super(name, new BooleanAndInt(defaultBoolean, defaultInt), sliderActive, commentTranslationKey, commentArgs);

        this.minValue = minValue;
        this.maxValue = maxValue;

        this.updateEffectiveValue();
        this.cacheSavedValue();
        this.sliderCallbackFactory = (listener) -> new IntegerSliderCallback(this, listener);
    }

    @Override
    public int getIntegerValue()
    {
        return this.effectiveIntegerValue;
    }

    @Override
    public boolean setIntegerValue(int newValue)
    {
        BooleanAndInt oldValue = this.getValue();
        int clampedValue = this.getClampedValue(newValue);
        return this.setValue(new BooleanAndInt(oldValue.booleanValue, clampedValue));
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

    @Override
    public boolean setValue(BooleanAndInt newValue)
    {
        int clampedValue = this.getClampedValue(newValue.intValue);

        if (clampedValue != newValue.intValue)
        {
            newValue = new BooleanAndInt(newValue.booleanValue, clampedValue);
        }

        return super.setValue(newValue);
    }

    @Override
    protected void updateEffectiveValue()
    {
        super.updateEffectiveValue();
        this.effectiveIntegerValue = this.effectiveValue.intValue;
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
        BooleanAndInt oldValue = this.getValue();
        return this.setValue(new BooleanAndInt(newValue, oldValue.intValue));
    }

    @Override
    public void toggleBooleanValue()
    {
        BooleanAndInt oldValue = this.getValue();
        this.setValue(new BooleanAndInt(! oldValue.booleanValue, oldValue.intValue));
    }

    protected int getClampedValue(int value)
    {
        return MathHelper.clamp(value, this.minValue, this.maxValue);
    }

    public boolean isModified(String newValue)
    {
        try
        {
            return Integer.parseInt(newValue) != this.defaultValue.intValue;
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
            this.setIntegerValue(Integer.parseInt(value));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for {} from the string '{}'", this.getName(), value);
        }
    }

    public static class BooleanAndInt
    {
        public final boolean booleanValue;
        public final int intValue;

        public BooleanAndInt(boolean booleanValue, int intValue)
        {
            this.booleanValue = booleanValue;
            this.intValue = intValue;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || this.getClass() != o.getClass()) { return false; }

            BooleanAndInt that = (BooleanAndInt) o;
            return this.booleanValue == that.booleanValue &&
                   this.intValue == that.intValue;
        }

        @Override
        public int hashCode()
        {
            int result = (this.booleanValue ? 1 : 0);
            result = 31 * result + this.intValue;
            return result;
        }

        @Override
        public String toString()
        {
            return new StringJoiner(", ", BooleanAndInt.class.getSimpleName() + "[", "]")
                    .add("booleanValue=" + this.booleanValue)
                    .add("intValue=" + this.intValue)
                    .toString();
        }
    }
}

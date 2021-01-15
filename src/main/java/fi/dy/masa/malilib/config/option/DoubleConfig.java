package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.SliderConfig;
import fi.dy.masa.malilib.gui.callback.DoubleSliderCallback;

public class DoubleConfig extends BaseSliderConfig<Double> implements SliderConfig
{
    protected double doubleValue;
    protected double minValue;
    protected double maxValue;

    public DoubleConfig(String name, double defaultValue)
    {
        this(name, defaultValue, name);
    }

    public DoubleConfig(String name, double defaultValue, String comment)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment);
    }

    public DoubleConfig(String name, double defaultValue, double minValue, double maxValue)
    {
        this(name, defaultValue, minValue, maxValue, name);
    }

    public DoubleConfig(String name, double defaultValue, double minValue, double maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment);
    }

    public DoubleConfig(String name, double defaultValue, double minValue, double maxValue, boolean sliderActive, String comment)
    {
        super(name, defaultValue, comment, sliderActive);

        this.doubleValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sliderCallbackFactory = (listener) -> new DoubleSliderCallback(this, listener);
    }

    public double getDoubleValue()
    {
        return this.doubleValue;
    }

    public float getFloatValue()
    {
        return (float) this.doubleValue;
    }

    @Override
    public void setValue(Double newValue)
    {
        if (Double.isNaN(newValue) == false)
        {
            newValue = this.getClampedValue(newValue);
            this.doubleValue = newValue;
            super.setValue(newValue);
        }
    }

    public double getMinDoubleValue()
    {
        return this.minValue;
    }

    public double getMaxDoubleValue()
    {
        return this.maxValue;
    }

    public void setMinDoubleValue(double minValue)
    {
        this.minValue = minValue;
        this.setValue(this.doubleValue);
    }

    public void setMaxDoubleValue(double maxValue)
    {
        this.maxValue = maxValue;
        this.setValue(this.doubleValue);
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
        catch (Exception ignore)
        {
        }

        return true;
    }

    public String getStringValue()
    {
        return String.valueOf(this.doubleValue);
    }

    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValue);
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

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.doubleValue = this.getClampedValue(element.getAsDouble());
                this.value = this.doubleValue;
                this.onValueLoaded(this.doubleValue);
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.doubleValue);
    }
}

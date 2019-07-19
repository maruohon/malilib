package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import net.minecraft.util.math.MathHelper;

public class ConfigDouble extends ConfigBase<ConfigDouble> implements IConfigDouble
{
    private final double minValue;
    private final double maxValue;
    private final double defaultValue;
    private double value;
    private double lastSavedValue;
    private boolean useSlider;

    public ConfigDouble(String name, double defaultValue, String comment)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, String comment)
    {
        this(name, defaultValue, minValue, maxValue, false, comment);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, boolean useSlider, String comment)
    {
        super(ConfigType.DOUBLE, name, comment);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.useSlider = useSlider;

        this.cacheSavedValue();
    }

    @Override
    public boolean shouldUseSlider()
    {
        return this.useSlider;
    }

    @Override
    public void toggleUseSlider()
    {
        this.useSlider = ! this.useSlider;
    }

    @Override
    public double getDoubleValue()
    {
        return this.value;
    }

    @Override
    public double getDefaultDoubleValue()
    {
        return this.defaultValue;
    }

    @Override
    public void setDoubleValue(double value)
    {
        double oldValue = this.value;
        this.value = this.getClampedValue(value);

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
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

    @Override
    public boolean isModified()
    {
        return this.value != this.defaultValue;
    }

    @Override
    public boolean isModified(String newValue)
    {
        try
        {
            return Double.parseDouble(newValue) != this.defaultValue;
        }
        catch (Exception e)
        {
        }

        return true;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedValue != this.value;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValue = this.value;
    }

    @Override
    public void resetToDefault()
    {
        this.value = this.defaultValue;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.value);
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValue);
    }

    @Override
    public void setValueFromString(String value)
    {
        try
        {
            this.setDoubleValue(Double.parseDouble(value));
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for {} from the string '{}'", this.getName(), value, e);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = this.getClampedValue(element.getAsDouble());
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}

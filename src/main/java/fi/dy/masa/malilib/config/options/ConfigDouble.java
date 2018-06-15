package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigDouble;
import net.minecraft.util.math.MathHelper;

public class ConfigDouble extends ConfigBase implements IConfigDouble
{
    private final double minValue;
    private final double maxValue;
    private final double defaultValue;
    private double value;

    public ConfigDouble(String name, double defaultValue, String comment)
    {
        this(name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, comment);
    }

    public ConfigDouble(String name, double defaultValue, double minValue, double maxValue, String comment)
    {
        super(ConfigType.DOUBLE, name, comment);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
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
        this.value = MathHelper.clamp(value, this.minValue, this.maxValue);
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
            LiteModMaLiLib.logger.warn("Failed to set config value for {} from the string '{}'", this.getName(), value, e);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.setDoubleValue(element.getAsDouble());
            }
            else
            {
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}

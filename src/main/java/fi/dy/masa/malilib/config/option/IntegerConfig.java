package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;

public class IntegerConfig extends BaseSliderConfig<Integer>
{
    protected final int defaultValue;
    protected int value;
    protected int lastSavedValue;
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
        super(name, comment, sliderActive);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sliderCallbackFactory = (listener) -> new IntegerSliderCallback(this, listener);

        this.cacheSavedValue();
    }

    public int getIntegerValue()
    {
        return this.value;
    }

    public int getDefaultIntegerValue()
    {
        return this.defaultValue;
    }

    @Override
    public Integer getValue()
    {
        return this.value;
    }

    public void setIntegerValue(int value)
    {
        int oldValue = this.value;
        this.value = this.getClampedValue(value);

        if (oldValue != this.value)
        {
            this.onValueChanged(value, oldValue);
        }
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
        this.setIntegerValue(this.value);
    }

    public void setMaxIntegerValue(int maxValue)
    {
        this.maxValue = maxValue;
        this.setIntegerValue(this.value);
    }

    protected int getClampedValue(int value)
    {
        return MathHelper.clamp(value, this.minValue, this.maxValue);
    }

    @Override
    public boolean isModified()
    {
        return this.value != this.defaultValue;
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
        this.setIntegerValue(this.defaultValue);
    }

    public String getStringValue()
    {
        return String.valueOf(this.value);
    }

    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValue);
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

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = this.getClampedValue(element.getAsInt());
                this.onValueLoaded(this.value);
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
        return new JsonPrimitive(this.value);
    }
}

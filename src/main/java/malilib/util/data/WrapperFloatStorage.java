package malilib.util.data;

import malilib.util.MathUtils;

public class WrapperFloatStorage implements RangedFloatStorage
{
    protected final FloatSupplier valueSupplier;
    protected final FloatConsumer valueConsumer;
    protected float minValue;
    protected float maxValue;

    public WrapperFloatStorage(float minValue, float maxValue, FloatSupplier valueSupplier, FloatConsumer valueConsumer)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueSupplier = valueSupplier;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public float getMinFloatValue()
    {
        return this.minValue;
    }

    @Override
    public float getMaxFloatValue()
    {
        return this.maxValue;
    }

    @Override
    public float getFloatValue()
    {
        return this.valueSupplier.getAsFloat();
    }

    @Override
    public boolean setFloatValue(float newValue)
    {
        this.valueConsumer.accept(MathUtils.clamp(newValue, this.minValue, this.maxValue));
        return true;
    }
}

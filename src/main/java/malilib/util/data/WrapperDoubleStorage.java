package malilib.util.data;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import malilib.util.MathUtils;

public class WrapperDoubleStorage implements RangedDoubleStorage
{
    protected final DoubleSupplier valueSupplier;
    protected final DoubleConsumer valueConsumer;
    protected double minValue;
    protected double maxValue;

    public WrapperDoubleStorage(double minValue, double maxValue, DoubleSupplier valueSupplier, DoubleConsumer valueConsumer)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueSupplier = valueSupplier;
        this.valueConsumer = valueConsumer;
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
    public double getDoubleValue()
    {
        return this.valueSupplier.getAsDouble();
    }

    @Override
    public boolean setDoubleValue(double newValue)
    {
        this.valueConsumer.accept(MathUtils.clamp(newValue, this.minValue, this.maxValue));
        return true;
    }
}

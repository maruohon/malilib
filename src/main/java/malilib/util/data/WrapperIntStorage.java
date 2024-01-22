package malilib.util.data;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import malilib.util.MathUtils;

public class WrapperIntStorage implements RangedIntegerStorage
{
    protected final IntSupplier valueSupplier;
    protected final IntConsumer valueConsumer;
    protected int minValue;
    protected int maxValue;

    public WrapperIntStorage(int minValue, int maxValue, IntSupplier valueSupplier, IntConsumer valueConsumer)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueSupplier = valueSupplier;
        this.valueConsumer = valueConsumer;
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
    public int getIntegerValue()
    {
        return this.valueSupplier.getAsInt();
    }

    @Override
    public boolean setIntegerValue(int newValue)
    {
        this.valueConsumer.accept(MathUtils.clamp(newValue, this.minValue, this.maxValue));
        return true;
    }
}

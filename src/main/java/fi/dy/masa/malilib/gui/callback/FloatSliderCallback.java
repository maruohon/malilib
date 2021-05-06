package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.RangedFloatStorage;

public class FloatSliderCallback implements SteppedSliderCallback
{
    protected final RangedFloatStorage storage;
    @Nullable protected final EventListener changeListener;
    protected double stepSize = 0.0009765625F; // 1 / 1024
    protected int maxSteps = Integer.MAX_VALUE;

    public FloatSliderCallback(RangedFloatStorage storage, @Nullable EventListener changeListener)
    {
        this.storage = storage;
        this.changeListener = changeListener;
    }

    @Override
    public double getRelativeValue()
    {
        float minValue = this.storage.getMinFloatValue();
        float maxValue = this.storage.getMaxFloatValue();
        return (this.storage.getFloatValue() - minValue) / (maxValue - minValue);
    }

    @Override
    public void setRelativeValue(double relativeValue)
    {
        float minValue = this.storage.getMinFloatValue();
        float maxValue = this.storage.getMaxFloatValue();
        double relValue = relativeValue * (maxValue - minValue);
        double value = relValue + minValue;
        double step = this.stepSize;

        if (step > 0)
        {
            value = value - ((value + step) % step);
        }

        this.storage.setFloatValue((float) value);

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    @Override
    public double getStepSize()
    {
        return this.stepSize;
    }

    @Override
    public void setStepSize(double step)
    {
        this.stepSize = step;
    }

    @Override
    public int getMaxSteps()
    {
        return this.maxSteps;
    }

    public void setMaxSteps(int maxSteps)
    {
        this.maxSteps = maxSteps;
    }

    @Override
    public String getFormattedDisplayValue()
    {
        return String.format("%.4f", this.storage.getFloatValue());
    }
}

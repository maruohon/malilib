package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.listener.EventListener;

public class DoubleSliderCallback implements SteppedSliderCallback
{
    protected final DoubleConfig config;
    @Nullable protected final EventListener changeListener;
    protected double stepSize = 0.0009765625; // 1 / 1024
    protected int maxSteps = Integer.MAX_VALUE;

    public DoubleSliderCallback(DoubleConfig config, @Nullable EventListener changeListener)
    {
        this.config = config;
        this.changeListener = changeListener;
    }

    @Override
    public double getValueRelative()
    {
        return (this.config.getDoubleValue() - this.config.getMinDoubleValue()) / (this.config.getMaxDoubleValue() - this.config.getMinDoubleValue());
    }

    @Override
    public void setValueRelative(double relativeValue)
    {
        double relValue = relativeValue * (this.config.getMaxDoubleValue() - this.config.getMinDoubleValue());
        double value = relValue + this.config.getMinDoubleValue();
        double step = this.stepSize;

        if (step > 0)
        {
            value = value - ((value + step) % step);
        }

        this.config.setDoubleValue(value);

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
        return String.format("%.4f", this.config.getDoubleValue());
    }
}

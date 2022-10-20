package malilib.gui.callback;

import javax.annotation.Nullable;
import malilib.listener.EventListener;
import malilib.render.text.StyledTextLine;
import malilib.util.data.RangedDoubleStorage;

public class DoubleSliderCallback implements SteppedSliderCallback
{
    protected final RangedDoubleStorage storage;
    @Nullable protected final EventListener changeListener;
    protected double stepSize = 0.0009765625; // 1 / 1024
    protected int maxSteps = Integer.MAX_VALUE;
    protected StyledTextLine displayText;

    public DoubleSliderCallback(RangedDoubleStorage storage, @Nullable EventListener changeListener)
    {
        this.storage = storage;
        this.changeListener = changeListener;
        this.updateDisplayText();
    }

    @Override
    public double getRelativeValue()
    {
        return (this.storage.getDoubleValue() - this.storage.getMinDoubleValue()) / (this.storage.getMaxDoubleValue() - this.storage.getMinDoubleValue());
    }

    @Override
    public void setRelativeValue(double relativeValue)
    {
        double relValue = relativeValue * (this.storage.getMaxDoubleValue() - this.storage.getMinDoubleValue());
        double value = relValue + this.storage.getMinDoubleValue();
        double step = this.stepSize;

        if (step > 0)
        {
            value = value - ((value + step) % step);
        }

        this.storage.setDoubleValue(value);
        this.updateDisplayText();

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
    public void updateDisplayText()
    {
        this.displayText = StyledTextLine.translate("malilibdev.label.config.slider_value.double",
                                                    String.format("%.4f", this.storage.getDoubleValue()));
    }

    @Override
    public StyledTextLine getDisplayText()
    {
        return this.displayText;
    }
}

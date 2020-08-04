package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.config.SliderCallback;

public class SliderCallbackInteger implements SliderCallback
{
    protected final IntegerConfig config;
    @Nullable protected final ButtonBase buttonReset;

    public SliderCallbackInteger(IntegerConfig config, @Nullable ButtonBase buttonReset)
    {
        this.config = config;
        this.buttonReset = buttonReset;
    }

    @Override
    public int getMaxSteps()
    {
        int steps = this.config.getMaxIntegerValue() - this.config.getMinIntegerValue() + 1;
        return steps > 0 ? steps : Integer.MAX_VALUE;
    }

    @Override
    public double getValueRelative()
    {
        return (double) (this.config.getIntegerValue() - this.config.getMinIntegerValue()) / (double) (this.config.getMaxIntegerValue() - this.config.getMinIntegerValue());
    }

    @Override
    public void setValueRelative(double relativeValue)
    {
        int relValue = (int) (relativeValue * (this.config.getMaxIntegerValue() - this.config.getMinIntegerValue()));
        this.config.setIntegerValue(relValue + this.config.getMinIntegerValue());

        if (this.buttonReset != null)
        {
            this.buttonReset.setEnabled(this.config.isModified());
        }
    }

    @Override
    public String getFormattedDisplayValue()
    {
        return String.valueOf(this.config.getIntegerValue());
    }
}

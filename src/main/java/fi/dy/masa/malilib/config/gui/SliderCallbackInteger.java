package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.options.IConfigInteger;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;

public class SliderCallbackInteger implements ISliderCallback
{
    protected final IConfigInteger config;
    protected final ButtonBase buttonReset;

    public SliderCallbackInteger(IConfigInteger config, @Nullable ButtonBase buttonReset)
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

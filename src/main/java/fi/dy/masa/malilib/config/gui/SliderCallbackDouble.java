package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.options.IConfigDouble;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;

public class SliderCallbackDouble implements ISliderCallback
{
    protected final IConfigDouble config;
    protected final ButtonBase resetButton;

    public SliderCallbackDouble(IConfigDouble config, @Nullable ButtonBase resetButton)
    {
        this.config = config;
        this.resetButton = resetButton;
    }

    @Override
    public int getMaxSteps()
    {
        return Integer.MAX_VALUE;
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
        this.config.setDoubleValue(relValue + this.config.getMinDoubleValue());

        if (this.resetButton != null)
        {
            this.resetButton.setEnabled(this.config.isModified());
        }
    }

    @Override
    public String getFormattedDisplayValue()
    {
        return String.format("%.4f", this.config.getDoubleValue());
    }
}

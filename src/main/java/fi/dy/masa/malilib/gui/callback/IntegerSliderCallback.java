package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;

public class IntegerSliderCallback implements SliderCallback
{
    protected final IntegerConfig config;
    @Nullable protected final BaseButton buttonReset;

    public IntegerSliderCallback(IntegerConfig config, @Nullable BaseButton buttonReset)
    {
        this.config = config;
        this.buttonReset = buttonReset;
    }

    @Override
    public int getMaxSteps()
    {
        long steps = (long) this.config.getMaxIntegerValue() - (long) this.config.getMinIntegerValue() + 1L;
        return steps > 0 ? (int) MathHelper.clamp(steps, 1, 8192) : 8192;
    }

    @Override
    public double getValueRelative()
    {
        return ((double) this.config.getIntegerValue() - (double) this.config.getMinIntegerValue()) / ((double) this.config.getMaxIntegerValue() - (double) this.config.getMinIntegerValue());
    }

    @Override
    public void setValueRelative(double relativeValue)
    {
        long relValue = (long) (relativeValue * ((long) this.config.getMaxIntegerValue() - (long) this.config.getMinIntegerValue()));
        this.config.setIntegerValue((int) (relValue + this.config.getMinIntegerValue()));

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

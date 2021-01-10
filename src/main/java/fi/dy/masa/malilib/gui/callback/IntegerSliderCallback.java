package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.listener.EventListener;

public class IntegerSliderCallback implements SliderCallback
{
    protected final IntegerConfig config;
    @Nullable protected final EventListener changeListener;

    public IntegerSliderCallback(IntegerConfig config, @Nullable EventListener changeListener)
    {
        this.config = config;
        this.changeListener = changeListener;
    }

    @Override
    public int getMaxSteps()
    {
        long steps = (long) this.config.getMaxIntegerValue() - (long) this.config.getMinIntegerValue() + 1L;
        return steps > 0 ? (int) MathHelper.clamp(steps, 1, 8192) : 8192;
    }

    @Override
    public double getRelativeValue()
    {
        return ((double) this.config.getIntegerValue() - (double) this.config.getMinIntegerValue()) / ((double) this.config.getMaxIntegerValue() - (double) this.config.getMinIntegerValue());
    }

    @Override
    public void setRelativeValue(double relativeValue)
    {
        long relValue = (long) (relativeValue * ((long) this.config.getMaxIntegerValue() - (long) this.config.getMinIntegerValue()));
        this.config.setIntegerValue((int) (relValue + this.config.getMinIntegerValue()));

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    @Override
    public String getFormattedDisplayValue()
    {
        return String.valueOf(this.config.getIntegerValue());
    }
}

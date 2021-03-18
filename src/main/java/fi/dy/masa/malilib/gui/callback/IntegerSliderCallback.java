package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.RangedIntegerStorage;

public class IntegerSliderCallback implements SliderCallback
{
    protected final RangedIntegerStorage storage;
    @Nullable protected final EventListener changeListener;

    public IntegerSliderCallback(RangedIntegerStorage storage, @Nullable EventListener changeListener)
    {
        this.storage = storage;
        this.changeListener = changeListener;
    }

    @Override
    public int getMaxSteps()
    {
        long steps = (long) this.storage.getMaxIntegerValue() - (long) this.storage.getMinIntegerValue() + 1L;
        return steps > 0 ? (int) MathHelper.clamp(steps, 1, 8192) : 8192;
    }

    @Override
    public double getRelativeValue()
    {
        return ((double) this.storage.getIntegerValue() - (double) this.storage.getMinIntegerValue()) / ((double) this.storage.getMaxIntegerValue() - (double) this.storage.getMinIntegerValue());
    }

    @Override
    public void setRelativeValue(double relativeValue)
    {
        long relValue = (long) (relativeValue * ((long) this.storage.getMaxIntegerValue() - (long) this.storage.getMinIntegerValue()));
        this.storage.setIntegerValue((int) (relValue + this.storage.getMinIntegerValue()));

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    @Override
    public String getFormattedDisplayValue()
    {
        return String.valueOf(this.storage.getIntegerValue());
    }
}

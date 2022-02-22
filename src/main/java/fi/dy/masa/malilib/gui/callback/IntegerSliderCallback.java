package fi.dy.masa.malilib.gui.callback;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.RangedIntegerStorage;

public class IntegerSliderCallback implements SliderCallback
{
    protected final RangedIntegerStorage storage;
    @Nullable protected final EventListener changeListener;
    protected StyledTextLine displayText;

    public IntegerSliderCallback(RangedIntegerStorage storage, @Nullable EventListener changeListener)
    {
        this.storage = storage;
        this.changeListener = changeListener;
        this.updateDisplayText();
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
        long maxValue = this.storage.getMaxIntegerValue();
        long minValue = this.storage.getMinIntegerValue();
        long relValue = (long) (relativeValue * (maxValue - minValue));

        this.storage.setIntegerValue((int) (relValue + minValue));
        this.updateDisplayText();

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    @Override
    public void updateDisplayText()
    {
        this.displayText = StyledTextLine.translate("malilib.label.config.slider_value.integer",
                                                    this.storage.getIntegerValue());
    }

    @Override
    public StyledTextLine getDisplayText()
    {
        return this.displayText;
    }
}

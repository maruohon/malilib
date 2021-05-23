package fi.dy.masa.malilib.gui.widget;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.util.data.RangedIntegerStorage;

public class IntegerEditWidget extends BaseNumberEditWidget implements RangedIntegerStorage
{
    protected final IntConsumer consumer;
    protected final int minValue;
    protected final int maxValue;
    protected int value;

    public IntegerEditWidget(int width, int height, int originalValue,
                             int minValue, int maxValue, IntConsumer consumer)
    {
        super(width, height);

        this.consumer = consumer;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setIntegerValue(originalValue);

        this.textFieldWidget = new IntegerTextFieldWidget(width, 16, originalValue, minValue, maxValue);
        this.textFieldWidget.setListener(this::setValueFromString);
    }

    @Override
    protected SliderWidget createSliderWidget()
    {
        return new SliderWidget(-1, 16, new IntegerSliderCallback(this, this::updateValue));
    }

    @Override
    protected void onValueAdjustButtonClick(BaseButton button, int mouseButton)
    {
        int amount = mouseButton == 1 ? -1 : 1;
        if (BaseScreen.isShiftDown()) { amount *= 8; }
        if (BaseScreen.isAltDown()) { amount *= 4; }

        this.setIntegerValue(this.value + amount);
        this.updateValue();
    }

    protected void updateValue()
    {
        this.textFieldWidget.setText(String.valueOf(this.value));
        this.consumer.accept(this.value);
    }

    protected void setValueFromString(String str)
    {
        try
        {
            this.setIntegerValue(Integer.parseInt(str));
            this.consumer.accept(this.value);
        }
        catch (NumberFormatException ignore) {}
    }

    @Override
    public int getIntegerValue()
    {
        return this.value;
    }

    @Override
    public boolean setIntegerValue(int newValue)
    {
        this.value = MathHelper.clamp(newValue, this.minValue, this.maxValue);
        return true;
    }

    @Override
    public int getMinIntegerValue()
    {
        return this.minValue;
    }

    @Override
    public int getMaxIntegerValue()
    {
        return this.maxValue;
    }
}

package fi.dy.masa.malilib.gui.widget;

import java.util.function.DoubleConsumer;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.callback.DoubleSliderCallback;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.util.data.RangedDoubleStorage;

public class DoubleEditWidget extends BaseNumberEditWidget implements RangedDoubleStorage
{
    protected final DoubleConsumer consumer;
    protected final double minValue;
    protected final double maxValue;
    protected double value;

    public DoubleEditWidget(int width, int height, double originalValue,
                            double minValue, double maxValue, DoubleConsumer consumer)
    {
        super(width, height);

        this.consumer = consumer;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setDoubleValue(originalValue);

        this.textFieldWidget = new DoubleTextFieldWidget(width, 16, originalValue, minValue, maxValue);
        this.textFieldWidget.setListener(this::setValueFromString);
    }

    @Override
    protected SliderWidget createSliderWidget()
    {
        return new SliderWidget(-1, 16, new DoubleSliderCallback(this, this::updateValue));
    }

    @Override
    protected void onValueAdjustButtonClick(BaseButton button, int mouseButton)
    {
        double amount = mouseButton == 1 ? -0.25 : 0.25;
        if (BaseScreen.isShiftDown()) { amount *= 4.0; }
        if (BaseScreen.isAltDown()) { amount *= 8.0; }

        this.setDoubleValue(this.value + amount);
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
            this.setDoubleValue(Double.parseDouble(str));
            this.consumer.accept(this.value);
        }
        catch (NumberFormatException ignore) {}
    }

    @Override
    public double getDoubleValue()
    {
        return this.value;
    }

    @Override
    public boolean setDoubleValue(double newValue)
    {
        this.value = MathHelper.clamp(newValue, this.minValue, this.maxValue);
        return true;
    }

    @Override
    public double getMinDoubleValue()
    {
        return this.minValue;
    }

    @Override
    public double getMaxDoubleValue()
    {
        return this.maxValue;
    }
}

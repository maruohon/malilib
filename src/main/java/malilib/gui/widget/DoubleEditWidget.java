package malilib.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleConsumer;

import malilib.gui.BaseScreen;
import malilib.gui.callback.DoubleSliderCallback;
import malilib.util.MathUtils;
import malilib.util.StringUtils;
import malilib.util.data.RangedDoubleStorage;

public class DoubleEditWidget extends BaseNumberEditWidget implements RangedDoubleStorage
{
    protected final DoubleConsumer consumer;
    protected double minValue;
    protected double maxValue;
    protected double value;
    protected double baseScrollAdjustAmount = 1.0;

    public DoubleEditWidget(int width, int height, RangedDoubleStorage storage)
    {
        this(width, height, storage.getDoubleValue(), storage.getMinDoubleValue(),
             storage.getMaxDoubleValue(), storage::setDoubleValue);
    }

    public DoubleEditWidget(int width, int height, double originalValue,
                            double minValue, double maxValue, DoubleConsumer consumer)
    {
        super(width, height);

        this.consumer = consumer;

        this.setValidRange(minValue, maxValue);
        this.setDoubleValue(originalValue);
    }

    @Override
    protected HorizontalSliderWidget createSliderWidget()
    {
        return new HorizontalSliderWidget(-1, this.getHeight(), new DoubleSliderCallback(this, this::updateTextField));
    }

    @Override
    protected boolean onValueAdjustButtonClick(int mouseButton)
    {
        double amount = this.baseScrollAdjustAmount;
        if (mouseButton == 1) amount = -amount;
        if (BaseScreen.isShiftDown()) { amount *= this.shiftModifier; }
        if (BaseScreen.isAltDown()) { amount *= this.altModifier; }

        double v = (double) ((int) ((this.value + amount) * 100000)) / 100000.0;
        this.setDoubleValue(v);

        return true;
    }

    protected void updateTextField()
    {
        this.textFieldWidget.setText(String.valueOf(this.value));
    }

    public void setBaseScrollAdjustAmount(double baseScrollAdjustAmount)
    {
        this.baseScrollAdjustAmount = baseScrollAdjustAmount;
    }

    public void setValidRange(double minValue, double maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.textFieldWidget.setTextValidator(new DoubleTextFieldWidget.DoubleValidator(minValue, maxValue));

        double scrollAmount = maxValue / 128.0 - minValue / 128.0;

        if (scrollAmount > 0 && scrollAmount <= 1.0)
        {
            this.setBaseScrollAdjustAmount(scrollAmount);
        }

        this.textFieldWidget.getHoverInfoFactory().updateList();
        this.sliderWidget.getHoverInfoFactory().updateList();
    }

    @Override
    protected void setValueFromTextField(String str)
    {
        try
        {
            this.clampAndSetValue(Double.parseDouble(str));
        }
        catch (NumberFormatException ignore) {}
    }

    protected void clampAndSetValue(double newValue)
    {
        this.value = MathUtils.clamp(newValue, this.minValue, this.maxValue);
        this.consumer.accept(this.value);
        this.sliderWidget.updateWidgetState();
    }

    @Override
    public boolean setDoubleValue(double newValue)
    {
        this.clampAndSetValue(newValue);
        this.updateTextField();
        return true;
    }

    @Override
    public double getDoubleValue()
    {
        return this.value;
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

    @Override
    protected List<String> getRangeHoverTooltip()
    {
        return Collections.singletonList(StringUtils.translate("malilib.hover.config.numeric.range",
                                                               this.minValue, this.maxValue));
    }
}

package fi.dy.masa.malilib.gui.widget;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.callback.FloatSliderCallback;
import fi.dy.masa.malilib.util.data.FloatConsumer;
import fi.dy.masa.malilib.util.data.RangedFloatStorage;

public class FloatEditWidget extends BaseNumberEditWidget implements RangedFloatStorage
{
    protected final FloatConsumer consumer;
    protected float minValue;
    protected float maxValue;
    protected float value;

    public FloatEditWidget(int width, int height, float originalValue,
                           float minValue, float maxValue, FloatConsumer consumer)
    {
        super(width, height);

        this.consumer = consumer;

        this.setValidRange(minValue, maxValue);
        this.setFloatValue(originalValue);

        this.textFieldWidget.setText(String.valueOf(originalValue));
        this.textFieldWidget.setTextValidator(new DoubleTextFieldWidget.DoubleValidator(minValue, maxValue));
    }

    @Override
    protected SliderWidget createSliderWidget()
    {
        return new SliderWidget(-1, this.getHeight(), new FloatSliderCallback(this, this::updateTextField));
    }

    @Override
    protected boolean onValueAdjustButtonClick(int mouseButton)
    {
        float amount = mouseButton == 1 ? -0.25F : 0.25F;
        if (BaseScreen.isShiftDown()) { amount *= 4.0F; }
        if (BaseScreen.isAltDown()) { amount *= 8.0F; }

        this.setFloatValue(this.value + amount);
        this.consumer.accept(this.value);
        this.sliderWidget.updateWidgetState();

        return true;
    }

    protected void updateTextField()
    {
        this.textFieldWidget.setText(String.valueOf(this.value));
    }

    public void setValidRange(float minValue, float maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.textFieldWidget.setTextValidator(new DoubleTextFieldWidget.DoubleValidator(minValue, maxValue));
    }

    @Override
    protected void setValueFromTextField(String str)
    {
        try
        {
            this.clampAndSetValue(Float.parseFloat(str));
            this.consumer.accept(this.value);
        }
        catch (NumberFormatException ignore) {}
    }

    protected void clampAndSetValue(float newValue)
    {
        this.value = MathHelper.clamp(newValue, this.minValue, this.maxValue);
    }

    @Override
    public boolean setFloatValue(float newValue)
    {
        this.clampAndSetValue(newValue);
        this.updateTextField();
        return true;
    }

    @Override
    public float getFloatValue()
    {
        return this.value;
    }

    @Override
    public float getMinFloatValue()
    {
        return this.minValue;
    }

    @Override
    public float getMaxFloatValue()
    {
        return this.maxValue;
    }
}

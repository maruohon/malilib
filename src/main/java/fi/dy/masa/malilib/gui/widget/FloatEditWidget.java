package fi.dy.masa.malilib.gui.widget;

import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.callback.FloatSliderCallback;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.util.FloatConsumer;
import fi.dy.masa.malilib.util.data.RangedFloatStorage;

public class FloatEditWidget extends BaseNumberEditWidget implements RangedFloatStorage
{
    protected final FloatConsumer consumer;
    protected final float minValue;
    protected final float maxValue;
    protected float value;

    public FloatEditWidget(int x, int y, int width, int height, float originalValue,
                           float minValue, float maxValue, FloatConsumer consumer)
    {
        super(x, y, width, height);

        this.consumer = consumer;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setFloatValue(originalValue);

        this.textFieldWidget = new DoubleTextFieldWidget(0, 0, width, 16, originalValue, minValue, maxValue);
        this.textFieldWidget.setListener(this::setValueFromString);
    }

    @Override
    protected SliderWidget createSliderWidget()
    {
        return new SliderWidget(0, 0, -1, 16, new FloatSliderCallback(this, this::updateValue));
    }

    @Override
    protected void onValueAdjustButtonClick(BaseButton button, int mouseButton)
    {
        float amount = mouseButton == 1 ? -0.25F : 0.25F;
        if (BaseScreen.isShiftDown()) { amount *= 4.0F; }
        if (BaseScreen.isAltDown()) { amount *= 8.0F; }

        this.setFloatValue(this.value + amount);
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
            this.setFloatValue(Float.parseFloat(str));
            this.consumer.accept(this.value);
        }
        catch (NumberFormatException ignore) {}
    }

    @Override
    public float getFloatValue()
    {
        return this.value;
    }

    @Override
    public boolean setFloatValue(float newValue)
    {
        this.value = MathHelper.clamp(newValue, this.minValue, this.maxValue);
        return true;
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

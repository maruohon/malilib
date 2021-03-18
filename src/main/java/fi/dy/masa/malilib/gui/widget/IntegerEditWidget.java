package fi.dy.masa.malilib.gui.widget;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.RangedIntegerStorage;

public class IntegerEditWidget extends ContainerWidget implements RangedIntegerStorage
{
    protected final IntConsumer consumer;
    protected final int minValue;
    protected final int maxValue;
    protected final IntegerTextFieldWidget textFieldWidget;
    protected final SliderWidget sliderWidget;
    protected final GenericButton sliderToggleButton;
    protected final GenericButton valueAdjustButton;
    protected boolean forceSlider;
    protected boolean useSlider;
    protected boolean sliderActive;
    protected boolean useValueAdjustButton = true;
    protected int value;

    public IntegerEditWidget(int x, int y, int width, int height, int originalValue,
                             int minValue, int maxValue, IntConsumer consumer)
    {
        super(x, y, width, height);

        this.consumer = consumer;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setIntegerValue(originalValue);

        this.sliderWidget = new SliderWidget(0, 0, -1, 16, new IntegerSliderCallback(this, this::updateValue));
        this.sliderToggleButton = GenericButton.createIconOnly(0, 0, () -> this.sliderActive ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER);

        this.valueAdjustButton = GenericButton.createIconOnly(0, 0, DefaultIcons.BTN_PLUSMINUS_14);
        this.valueAdjustButton.setCanScrollToClick(true);
        this.valueAdjustButton.setOutlineColorNormal(0xFF000000);
        this.valueAdjustButton.setActionListener(this::onValueAdjustButtonClick);
        this.valueAdjustButton.addHoverStrings(StringUtils.translate("malilib.button.hover.plus_minus_tip"));

        this.textFieldWidget = new IntegerTextFieldWidget(0, 0, width, 16, originalValue, minValue, maxValue);
        this.textFieldWidget.setListener(this::setValueFromString);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.sliderActive)
        {
            this.addWidget(this.sliderWidget);
        }
        else
        {
            this.addWidget(this.textFieldWidget);
        }

        if (this.useSlider && this.forceSlider == false)
        {
            this.addWidget(this.sliderToggleButton);
        }

        if (this.useValueAdjustButton)
        {
            this.addWidget(this.valueAdjustButton);
        }
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int tw = width;

        if (this.useValueAdjustButton)
        {
            tw -= this.valueAdjustButton.getWidth() + 1;
        }

        if (this.useSlider && this.forceSlider == false)
        {
            tw -= this.sliderToggleButton.getWidth() + 1;
        }

        if (this.sliderActive)
        {
            this.sliderWidget.setWidth(tw);
        }
        else
        {
            this.textFieldWidget.setWidth(tw);
        }

        if (this.sliderActive == false)
        {
            this.textFieldWidget.setPosition(x, y);
            x = this.textFieldWidget.getRight() + 1;
        }
        else
        {
            this.sliderWidget.setPosition(x, y);
            x = this.sliderWidget.getRight() + 1;
        }

        if (this.useValueAdjustButton)
        {
            this.valueAdjustButton.setPosition(x, y);
            x = this.valueAdjustButton.getRight() + 1;
        }

        if (this.useSlider && this.forceSlider == false)
        {
            this.sliderToggleButton.setPosition(x, y);
        }
    }

    public void setUseSlider(boolean useSlider)
    {
        this.useSlider = useSlider;
    }

    public void setUseValueAdjustButton(boolean useButton)
    {
        this.useValueAdjustButton = useButton;
    }

    public void setForceSlider(boolean forceSlider)
    {
        this.forceSlider = forceSlider;

        if (forceSlider)
        {
            this.sliderActive = true;
        }
    }

    protected void toggleSliderActive()
    {
        this.sliderActive = ! this.sliderActive;
        this.updateSubWidgetsToGeometryChanges();
        this.reAddSubWidgets();
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

    protected void onValueAdjustButtonClick(BaseButton button, int mouseButton)
    {
        int amount = mouseButton == 1 ? -1 : 1;
        if (BaseScreen.isShiftDown()) { amount *= 8; }
        if (BaseScreen.isAltDown()) { amount *= 4; }

        this.setIntegerValue(this.value + amount);
        this.updateValue();
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

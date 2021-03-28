package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseNumberEditWidget extends ContainerWidget
{
    protected final SliderWidget sliderWidget;
    protected final GenericButton sliderToggleButton;
    protected final GenericButton valueAdjustButton;
    protected BaseTextFieldWidget textFieldWidget;
    protected boolean forceSlider;
    protected boolean useSlider;
    protected boolean sliderActive;
    protected boolean useValueAdjustButton = true;

    public BaseNumberEditWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        this.sliderToggleButton = GenericButton.createIconOnly(0, 0, () -> this.sliderActive ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER);
        this.sliderToggleButton.setActionListener(this::toggleSliderActive);

        this.valueAdjustButton = GenericButton.createIconOnly(0, 0, DefaultIcons.BTN_PLUSMINUS_16);
        this.valueAdjustButton.setCanScrollToClick(true);
        this.valueAdjustButton.setActionListener(this::onValueAdjustButtonClick);
        this.valueAdjustButton.addHoverStrings(StringUtils.translate("malilib.button.hover.plus_minus_tip"));

        this.sliderWidget = this.createSliderWidget();
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

    protected abstract SliderWidget createSliderWidget();

    protected abstract void onValueAdjustButtonClick(BaseButton button, int mouseButton);
}

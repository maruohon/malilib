package fi.dy.masa.malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.render.text.StyledText;

public abstract class BaseNumberEditWidget extends ContainerWidget
{
    protected final BaseTextFieldWidget textFieldWidget;
    protected final SliderWidget sliderWidget;
    protected final GenericButton sliderToggleButton;
    protected final GenericButton valueAdjustButton;
    protected final LabelWidget labelWidget;
    protected boolean addLabel;
    protected boolean addSlider;
    protected boolean addValueAdjustButton = true;
    protected boolean forceSlider;
    protected boolean sliderActive;

    public BaseNumberEditWidget(int width, int height)
    {
        super(width, height);

        this.labelWidget = new LabelWidget();
        Supplier<MultiIcon> supplier = () -> this.sliderActive ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER;
        this.sliderToggleButton = GenericButton.create(supplier, this::toggleSliderActive);

        this.valueAdjustButton = GenericButton.create(DefaultIcons.BTN_PLUSMINUS_16);
        this.valueAdjustButton.setActionListener(this::onValueAdjustButtonClick);
        this.valueAdjustButton.translateAndAddHoverString("malilib.hover.button.plus_minus_tip");
        this.valueAdjustButton.setCanScrollToClick(true);

        this.textFieldWidget = new BaseTextFieldWidget(width, height);
        this.textFieldWidget.setListener(this::setValueFromTextField);

        this.sliderWidget = this.createSliderWidget();

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.sliderWidget.setEnabledStatusSupplier(enabledSupplier);
        this.textFieldWidget.setEnabledStatusSupplier(enabledSupplier);
        this.valueAdjustButton.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidgetIf(this.labelWidget, this.addLabel);
        this.addWidgetIf(this.textFieldWidget, this.sliderActive == false);
        this.addWidgetIf(this.sliderWidget, this.sliderActive);
        this.addWidgetIf(this.valueAdjustButton, this.addValueAdjustButton);

        if (this.addSlider && this.forceSlider == false)
        {
            this.addWidget(this.sliderToggleButton);
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int tw = width;

        if (this.addLabel)
        {
            int ly = y + this.getHeight() / 2 - 3;
            this.labelWidget.setPosition(x, ly);
            int w = this.labelWidget.getWidth() + 2;
            tw -= w;
            x += w;
        }

        if (this.addValueAdjustButton)
        {
            tw -= this.valueAdjustButton.getWidth() + 1;
        }

        if (this.addSlider && this.forceSlider == false)
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

        if (this.addValueAdjustButton)
        {
            this.valueAdjustButton.setX(x);
            this.valueAdjustButton.centerVerticallyInside(this.textFieldWidget);
            x = this.valueAdjustButton.getRight() + 1;
        }

        if (this.addSlider && this.forceSlider == false)
        {
            this.sliderToggleButton.setPosition(x, y);
        }
    }

    public BaseNumberEditWidget setAddLabel(boolean addLabel)
    {
        this.addLabel = addLabel;
        return this;
    }

    public BaseNumberEditWidget setAddSlider(boolean addSlider)
    {
        this.addSlider = addSlider;
        return this;
    }

    public BaseNumberEditWidget setAddValueAdjustButton(boolean useButton)
    {
        this.addValueAdjustButton = useButton;
        return this;
    }

    public BaseNumberEditWidget setForceSlider(boolean forceSlider)
    {
        this.forceSlider = forceSlider;

        if (forceSlider)
        {
            this.sliderActive = true;
        }

        return this;
    }

    public BaseTextFieldWidget getTextField()
    {
        return this.textFieldWidget;
    }

    public int getTextFieldWidgetX()
    {
        return this.sliderActive ? this.sliderWidget.getX() : this.textFieldWidget.getX();
    }

    protected void toggleSliderActive()
    {
        this.sliderActive = ! this.sliderActive;
        this.updateSubWidgetPositions();
        this.reAddSubWidgets();
    }

    public BaseNumberEditWidget setLabelText(String translationKey, Object... args)
    {
        this.labelWidget.setLabelStyledText(StyledText.translate(translationKey, args));
        this.addLabel = true;
        return this;
    }

    protected abstract void setValueFromTextField(String str);

    protected abstract SliderWidget createSliderWidget();

    protected abstract boolean onValueAdjustButtonClick(int mouseButton);
}

package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.SliderConfig;
import fi.dy.masa.malilib.config.option.BaseConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public abstract class NumericConfigWidget<TYPE, CFG extends BaseConfig<TYPE> & SliderConfig> extends BaseConfigOptionWidget<TYPE, CFG>
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton sliderToggleButton;
    protected SliderWidget sliderWidget;

    public NumericConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.textField = new BaseTextFieldWidget(x, y, 60, 16);

        this.sliderToggleButton = new GenericButton(x, y, () -> this.config.isSliderActive() ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER);
        this.sliderToggleButton.addHoverStrings("malilib.gui.button.hover.text_field_slider_toggle");
        this.sliderToggleButton.setActionListener((btn, mbtn) -> {
            this.config.toggleSliderActive();
            this.reAddSubWidgets();
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.resetButton.setEnabled(this.config.isModified());
            this.reAddSubWidgets();
        });

        this.sliderWidget = new SliderWidget(x, y, 60, 20, config.getSliderCallback(this::updateResetButtonState));
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        if (this.config.isSliderActive())
        {
            this.sliderWidget.setPosition(x, y + 1);
            this.sliderWidget.setWidth(elementWidth - 18);
            x += this.sliderWidget.getWidth() + 2;

            this.addWidget(this.sliderWidget);
        }
        else
        {
            this.textField.setPosition(x, y + 3);
            this.textField.setWidth(elementWidth - 18);
            this.textField.setText(this.getCurrentValueAsString());
            x += this.textField.getWidth() + 2;

            this.addWidget(this.textField);
        }

        this.sliderToggleButton.setPosition(x, y + 3);
        this.sliderToggleButton.setEnabled(this.config.allowSlider());
        this.updateWidgetStates();

        if (this.config.allowSlider() == false)
        {
            this.sliderToggleButton.setHoverStrings("malilib.gui.button.hover.text_field_slider_toggle",
                                                    "malilib.gui.button.hover.text_field_slider_toggle.not_allowed");
        }

        this.updateResetButton(x + 20, y + 1);

        this.addWidget(this.sliderToggleButton);
        this.addWidget(this.resetButton);
    }

    protected void updateWidgetStates()
    {
        if (this.config.isSliderActive())
        {
            this.sliderWidget.setLocked(this.config.isLocked());
            this.sliderWidget.setHoverStrings(this.config.getLockAndOverrideMessages());
        }
        else
        {
            this.textField.setHoverStrings(this.config.getLockAndOverrideMessages());
            this.textField.setEnabled(this.config.isLocked() == false);
        }
    }

    protected abstract String getCurrentValueAsString();
}

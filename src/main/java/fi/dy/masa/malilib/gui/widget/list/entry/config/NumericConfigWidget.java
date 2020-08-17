package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.SliderConfig;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.listener.EventListener;

public abstract class NumericConfigWidget<T extends ConfigOption<?> & SliderConfig> extends BaseConfigOptionWidget<T>
{
    protected final T config;
    protected final BaseTextFieldWidget textField;
    protected final GenericButton sliderToggleButton;
    protected SliderWidget sliderWidget;

    public NumericConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, T config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);

        this.config = config;

        this.textField = new BaseTextFieldWidget(x, y, 60, 16);

        this.sliderToggleButton = new GenericButton(x, y, () -> this.config.shouldUseSlider() ? BaseIcon.BTN_TXTFIELD : BaseIcon.BTN_SLIDER);
        this.sliderToggleButton.addHoverStrings("malilib.gui.button.hover.text_field_slider_toggle");
        this.sliderToggleButton.setActionListener((btn, mbtn) -> {
            this.config.toggleUseSlider();
            this.reAddSubWidgets();
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.resetButton.setEnabled(this.config.isModified());
            this.reAddSubWidgets();
        });

        this.sliderWidget = new SliderWidget(x, y, 60, 20, this.createSliderCallback(config, this::updateResetButtonState));
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        if (this.config.shouldUseSlider())
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
        this.updateResetButton(x + 20, y + 1, this.config);

        this.addWidget(this.sliderToggleButton);
        this.addWidget(this.resetButton);
    }

    protected void updateResetButtonState()
    {
        this.resetButton.setEnabled(this.config.isModified());
    }

    protected abstract String getCurrentValueAsString();

    protected abstract SliderCallback createSliderCallback(T config, EventListener changeListener);
}

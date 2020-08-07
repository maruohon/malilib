package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.SliderConfig;
import fi.dy.masa.malilib.gui.button.GenericButton;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.gui.widget.WidgetSlider;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;

public abstract class NumericConfigWidget<T extends ConfigOption<?> & SliderConfig> extends BaseConfigOptionWidget<T>
{
    protected final T config;
    protected final WidgetTextFieldBase textField;
    protected final GenericButton sliderToggleButton;
    protected WidgetSlider sliderWidget;

    public NumericConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, T config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, gui);

        this.config = config;

        this.textField = new WidgetTextFieldBase(x, y, 60, 16);

        this.sliderToggleButton = new GenericButton(x, y, () -> this.config.shouldUseSlider() ? BaseGuiIcon.BTN_TXTFIELD : BaseGuiIcon.BTN_SLIDER);
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
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY();
        int elementWidth = this.gui.getConfigElementsWidth();

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

    protected abstract String getCurrentValueAsString();
}

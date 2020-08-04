package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.gui.callback.SliderCallbackDouble;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetSlider;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldDouble;

public class DoubleConfigWidget extends BaseConfigOptionWidget<DoubleConfig>
{
    protected final DoubleConfig config;
    protected final double initialValue;

    public DoubleConfigWidget(int x, int y, int width, int height, int listIndex, DoubleConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getDoubleValue();

        this.reCreateWidgets(x, y);
    }

    @Override
    protected void reCreateWidgets(int x, int y)
    {
        super.reCreateWidgets(x, y);

        x += this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth() - 18;

        this.addGenericResetButton(x + elementWidth + 22, y + 1, this.config);

        if (this.config.shouldUseSlider())
        {
            WidgetSlider slider = new WidgetSlider(x, y + 1, elementWidth, 20, new SliderCallbackDouble(this.config, this.resetButton));
            x += slider.getWidth() + 2;
            this.addWidget(slider);
        }
        else
        {
            WidgetTextFieldDouble textField = new WidgetTextFieldDouble(x, y + 3, elementWidth, 16, this.config.getDoubleValue(),
                                                                        this.config.getMinDoubleValue(), this.config.getMaxDoubleValue());
            textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_DOUBLE);

            textField.setListener((str) -> {
                this.config.setValueFromString(str);
                this.resetButton.setEnabled(this.config.isModified());
            });

            x += textField.getWidth() + 2;
            this.addWidget(textField);
        }

        this.addSliderToggleButton(x, y + 3, this.config);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getDoubleValue() != this.initialValue;
    }
}


package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.callback.SliderCallbackInteger;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetSlider;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldInteger;

public class IntegerConfigWidget extends BaseConfigOptionWidget<IntegerConfig>
{
    protected final IntegerConfig config;
    protected final int initialValue;

    public IntegerConfigWidget(int x, int y, int width, int height, int listIndex, IntegerConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getIntegerValue();

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
            WidgetSlider slider = new WidgetSlider(x, y, elementWidth, 20, new SliderCallbackInteger(this.config, this.resetButton));
            x += slider.getWidth() + 2;
            this.addWidget(slider);
        }
        else
        {
            WidgetTextFieldInteger textField = new WidgetTextFieldInteger(x, y + 3, elementWidth, 16, this.config.getIntegerValue(),
                                                                          this.config.getMinIntegerValue(), this.config.getMaxIntegerValue());
            textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_INTEGER);

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
        return this.config.getIntegerValue() != this.initialValue;
    }
}

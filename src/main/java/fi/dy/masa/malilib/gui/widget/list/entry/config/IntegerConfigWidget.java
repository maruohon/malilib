package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.callback.SliderCallbackInteger;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetSlider;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldInteger;

public class IntegerConfigWidget extends NumericConfigWidget<IntegerConfig>
{
    protected final IntegerConfig integerConfig;
    protected final int initialValue;

    public IntegerConfigWidget(int x, int y, int width, int height, int listIndex, IntegerConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.integerConfig = config;
        this.initialValue = this.config.getIntegerValue();

        this.sliderWidget = new WidgetSlider(x, y, 60, 20, new SliderCallbackInteger(this.integerConfig, this.resetButton));

        this.textField.setTextValidator(new WidgetTextFieldInteger.IntValidator(this.config.getMinIntegerValue(), this.config.getMaxIntegerValue()));
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    protected String getCurrentValueAsString()
    {
        return String.valueOf(this.integerConfig.getIntegerValue());
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getIntegerValue() != this.initialValue;
    }
}

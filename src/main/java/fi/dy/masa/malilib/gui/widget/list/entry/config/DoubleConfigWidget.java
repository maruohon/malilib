package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.gui.callback.SliderCallbackDouble;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetSlider;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldDouble;

public class DoubleConfigWidget extends NumericConfigWidget<DoubleConfig>
{
    protected final DoubleConfig doubleConfig;
    protected final double initialValue;

    public DoubleConfigWidget(int x, int y, int width, int height, int listIndex, DoubleConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.doubleConfig = config;
        this.initialValue = this.config.getDoubleValue();

        this.sliderWidget = new WidgetSlider(x, y, 60, 20, new SliderCallbackDouble(this.doubleConfig, this.resetButton));

        this.textField.setTextValidator(new WidgetTextFieldDouble.DoubleValidator(this.config.getMinDoubleValue(), this.config.getMaxDoubleValue()));
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    protected String getCurrentValueAsString()
    {
        return String.valueOf(this.doubleConfig.getDoubleValue());
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getDoubleValue() != this.initialValue;
    }
}


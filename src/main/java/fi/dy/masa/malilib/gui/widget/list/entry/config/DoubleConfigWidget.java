package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
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

        // TODO config refactor
        WidgetTextFieldDouble textField = new WidgetTextFieldDouble(x + 120, y + 3, 120, 16, this.initialValue,
                                                                    config.getMinDoubleValue(), config.getMaxDoubleValue());
        textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_DOUBLE);
        this.addTextField(textField, (newText) -> {
            //resetButton.setEnabled(config.isModified(newText));
            this.config.setValueFromString(newText);
        });
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getDoubleValue() != this.initialValue;
    }
}


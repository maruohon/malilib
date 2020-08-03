package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
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

        // TODO config refactor
        WidgetTextFieldInteger textField = new WidgetTextFieldInteger(x + 120, y + 3, 120, 16, this.initialValue,
                                                                      config.getMinIntegerValue(), config.getMaxIntegerValue());
        textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_INTEGER);
        this.addTextField(textField, (newText) -> {
            //resetButton.setEnabled(config.isModified(newText));
            this.config.setValueFromString(newText);
        });
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getIntegerValue() != this.initialValue;
    }
}

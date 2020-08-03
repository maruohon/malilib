package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;

public class StringConfigWidget extends BaseConfigOptionWidget<StringConfig>
{
    protected final StringConfig config;
    protected final String initialValue;

    public StringConfigWidget(int x, int y, int width, int height, int listIndex, StringConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getStringValue();

        // TODO config refactor
        WidgetTextFieldBase textField = new WidgetTextFieldBase(x + 120, y + 3, 120, 16, this.initialValue);
        this.addTextField(textField, (newText) -> {
            //resetButton.setEnabled(config.isModified(newText));
            this.config.setValueFromString(newText);
        });
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getStringValue().equals(this.initialValue) == false;
    }
}

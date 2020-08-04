package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
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

        this.reCreateWidgets(x, y);
    }

    @Override
    protected void reCreateWidgets(int x, int y)
    {
        super.reCreateWidgets(x, y);

        int xOff = this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();
        WidgetTextFieldBase textField = new WidgetTextFieldBase(x + xOff, y + 3, elementWidth, 16, this.config.getStringValue());

        textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });

        final ButtonGeneric resetButton = this.createResetButton(x + xOff + elementWidth + 4, y + 1, this.config);
        this.resetButton = resetButton;

        resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.reCreateWidgets(this.getX(), this.getY());
        });

        this.addWidget(textField);
        this.addWidget(resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getStringValue().equals(this.initialValue) == false;
    }
}

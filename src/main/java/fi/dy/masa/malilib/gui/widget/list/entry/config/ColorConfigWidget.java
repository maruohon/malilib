package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetColorIndicator;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;

public class ColorConfigWidget extends BaseConfigOptionWidget<ColorConfig>
{
    protected final ColorConfig config;
    protected final int initialValue;

    public ColorConfigWidget(int x, int y, int width, int height, int listIndex, ColorConfig config, BaseConfigScreen gui)
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
        int elementWidth = this.gui.getConfigElementsWidth();

        final ButtonGeneric resetButton = this.createResetButton(x + elementWidth + 4, y + 1, this.config);
        this.resetButton = resetButton;

        WidgetColorIndicator colorWidget = new WidgetColorIndicator(x, y + 1, 19, 19, this.config, (newValue) -> {
            this.config.setIntegerValue(newValue);
            this.reCreateWidgets(this.getX(), this.getY());
        });
        this.addWidget(colorWidget);

        x += colorWidget.getWidth() + 4;
        WidgetTextFieldBase textField = new WidgetTextFieldBase(x, y + 3, 80, 16, this.config.getStringValue());

        textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });

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
        return this.config.getIntegerValue() != this.initialValue;
    }
}

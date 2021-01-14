package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.DoubleTextFieldWidget;

public class DoubleConfigWidget extends NumericConfigWidget<Double, DoubleConfig>
{
    protected final String initialStringValue;

    public DoubleConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, DoubleConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.initialStringValue = String.valueOf(this.initialValue);

        this.textField.setTextValidator(new DoubleTextFieldWidget.DoubleValidator(this.config.getMinDoubleValue(), this.config.getMaxDoubleValue()));
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    protected String getCurrentValueAsString()
    {
        return String.valueOf(this.config.getDoubleValue());
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialStringValue) == false)
        {
            this.config.setValueFromString(text);
        }
    }
}


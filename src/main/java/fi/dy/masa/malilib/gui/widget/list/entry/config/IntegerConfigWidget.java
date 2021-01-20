package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;

public class IntegerConfigWidget extends NumericConfigWidget<Integer, IntegerConfig>
{
    protected final String initialStringValue;

    public IntegerConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, IntegerConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.initialStringValue = String.valueOf(this.initialValue);

        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(this.config.getMinIntegerValue(), this.config.getMaxIntegerValue()));
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.updateResetButtonState();
        });
    }

    @Override
    protected String getCurrentValueAsString()
    {
        return String.valueOf(this.config.getIntegerValue());
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (this.config.isSliderActive() == false && text.equals(this.initialStringValue) == false)
        {
            this.config.setValueFromString(text);
        }
    }
}

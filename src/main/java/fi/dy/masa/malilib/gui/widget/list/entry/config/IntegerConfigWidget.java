package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;
import fi.dy.masa.malilib.listener.EventListener;

public class IntegerConfigWidget extends NumericConfigWidget<IntegerConfig>
{
    protected final IntegerConfig integerConfig;
    protected final int initialValue;
    protected final String initialStringValue;

    public IntegerConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, IntegerConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.integerConfig = config;
        this.initialValue = this.config.getIntegerValue();
        this.initialStringValue = String.valueOf(this.initialValue);

        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(this.config.getMinIntegerValue(), this.config.getMaxIntegerValue()));
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    protected SliderCallback createSliderCallback(IntegerConfig config, EventListener changeListener)
    {
        return new IntegerSliderCallback(config, changeListener);
    }

    @Override
    protected String getCurrentValueAsString()
    {
        return String.valueOf(this.integerConfig.getIntegerValue());
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

    @Override
    public boolean wasModified()
    {
        return this.config.getIntegerValue() != this.initialValue;
    }
}

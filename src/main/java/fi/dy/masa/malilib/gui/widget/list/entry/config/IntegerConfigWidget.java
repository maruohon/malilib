package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;

public class IntegerConfigWidget extends NumericConfigWidget<Integer, IntegerConfig>
{
    public IntegerConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, IntegerConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx,
              IntegerConfig::setValueFromString, IntegerConfig::getStringValue);

        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(config.getMinIntegerValue(),
                                                                                config.getMaxIntegerValue()));
        this.textField.translateAndAddHoverString("malilib.hover.config.numeric.range_and_default",
                                                  config.getMinIntegerValue(),
                                                  config.getMaxIntegerValue(),
                                                  config.getDefaultIntegerValue());
    }
}

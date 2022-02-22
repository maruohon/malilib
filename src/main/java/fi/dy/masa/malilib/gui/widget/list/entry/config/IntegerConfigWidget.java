package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class IntegerConfigWidget extends NumericConfigWidget<Integer, IntegerConfig>
{
    public IntegerConfigWidget(IntegerConfig config,
                               DataListEntryWidgetData constructData,
                               ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx,
              IntegerConfig::setValueFromString, IntegerConfig::getStringValue);

        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(config.getMinIntegerValue(),
                                                                                config.getMaxIntegerValue()));
        this.textField.translateAndAddHoverString("malilib.hover.config.numeric.range_and_default",
                                                  config.getMinIntegerValue(),
                                                  config.getMaxIntegerValue(),
                                                  config.getDefaultIntegerValue());
    }

    @Override
    protected boolean onValueAdjustButtonClick(int mouseButton)
    {
        int amount = mouseButton == 1 ? -1 : 1;
        if (BaseScreen.isShiftDown()) { amount *= 8; }
        if (BaseScreen.isAltDown()) { amount *= 4; }

        this.config.setIntegerValue(this.config.getIntegerValue() + amount);
        this.updateWidgetDisplayValues();

        return true;
    }
}

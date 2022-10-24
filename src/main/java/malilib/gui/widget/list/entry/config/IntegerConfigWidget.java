package malilib.gui.widget.list.entry.config;

import malilib.config.option.IntegerConfig;
import malilib.gui.BaseScreen;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.IntegerTextFieldWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

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
        this.sliderWidget.translateAndAddHoverString("malilib.hover.config.numeric.range_and_default",
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
        this.updateWidgetState();

        return true;
    }
}

package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BooleanAndIntConfigWidget extends BaseBooleanAndNumberConfigWidget<BooleanAndInt, BooleanAndIntConfig>
{
    public BooleanAndIntConfigWidget(BooleanAndIntConfig config,
                                     DataListEntryWidgetData constructData,
                                     ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx,
              BooleanAndIntConfig::setValueFromString, BooleanAndIntConfig::getStringValue);

        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(config.getMinIntegerValue(),
                                                                                config.getMaxIntegerValue()));
        this.textField.translateAndAddHoverString("malilib.hover.config.numeric.range_and_default",
                                                  config.getMinIntegerValue(),
                                                  config.getMaxIntegerValue(),
                                                  config.getDefaultValue().intValue);
        this.sliderWidget.translateAndAddHoverString("malilib.hover.config.numeric.range_and_default",
                                                     config.getMinIntegerValue(),
                                                     config.getMaxIntegerValue(),
                                                     config.getDefaultValue().intValue);
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

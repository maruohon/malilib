package malilib.gui.widget.list.entry.config;

import malilib.config.option.BooleanAndIntConfig;
import malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import malilib.gui.BaseScreen;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.IntegerTextFieldWidget.IntValidator;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BooleanAndIntConfigWidget extends BaseBooleanAndNumberConfigWidget<BooleanAndInt, BooleanAndIntConfig>
{
    public BooleanAndIntConfigWidget(BooleanAndIntConfig config,
                                     DataListEntryWidgetData constructData,
                                     ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx,
              BooleanAndIntConfig::setValueFromString, BooleanAndIntConfig::getStringValue);

        this.textField.setTextValidator(new IntValidator(config.getMinIntegerValue(),
                                                         config.getMaxIntegerValue()));
        this.textField.translateAndAddHoverString("malilibdev.hover.config.numeric.range_and_default",
                                                  config.getMinIntegerValue(),
                                                  config.getMaxIntegerValue(),
                                                  config.getDefaultValue().intValue);
        this.sliderWidget.translateAndAddHoverString("malilibdev.hover.config.numeric.range_and_default",
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

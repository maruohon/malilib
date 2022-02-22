package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.DoubleTextFieldWidget;

public class DoubleConfigWidget extends NumericConfigWidget<Double, DoubleConfig>
{
    public DoubleConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, DoubleConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx,
              DoubleConfig::setValueFromString, DoubleConfig::getStringValue);

        this.textField.setTextValidator(new DoubleTextFieldWidget.DoubleValidator(config.getMinDoubleValue(),
                                                                                  config.getMaxDoubleValue()));
        this.textField.translateAndAddHoverString("malilib.hover.config.numeric.range_and_default",
                                                  config.getMinDoubleValue(),
                                                  config.getMaxDoubleValue(),
                                                  config.getDefaultValue());
    }

    @Override
    protected boolean onValueAdjustButtonClick(int mouseButton)
    {
        double amount = mouseButton == 1 ? -0.25 : 0.25;
        if (BaseScreen.isShiftDown()) { amount *= 4.0; }
        if (BaseScreen.isAltDown()) { amount *= 8.0; }

        this.config.setDoubleValue(this.config.getDoubleValue() + amount);
        this.updateWidgetDisplayValues();

        return true;
    }
}


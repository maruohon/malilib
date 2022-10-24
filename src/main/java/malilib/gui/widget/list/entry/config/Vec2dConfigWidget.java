package malilib.gui.widget.list.entry.config;

import malilib.config.option.Vec2dConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.BaseDualNumberEditWidget;
import malilib.gui.widget.DoubleEditWidget;
import malilib.gui.widget.Vec2dEditWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.position.Vec2d;

public class Vec2dConfigWidget extends BaseDualNumberConfigWidget<Vec2d, Vec2dConfig, DoubleEditWidget>
{
    public Vec2dConfigWidget(Vec2dConfig config,
                             DataListEntryWidgetData constructData,
                             ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected BaseDualNumberEditWidget<Vec2d, DoubleEditWidget> createEditWidget(Vec2dConfig config)
    {
        Vec2dEditWidget widget = new Vec2dEditWidget(120, 18, 6, false, config.getValue(), this.config::setValue);
        double minValue = config.getMinValue();
        double maxValue = config.getMaxValue();
        widget.setValidRange(minValue, minValue, maxValue, maxValue);
        return widget;
    }
}

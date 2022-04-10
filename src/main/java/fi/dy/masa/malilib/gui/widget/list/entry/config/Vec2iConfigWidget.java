package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseDualNumberEditWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.Vec2iEditWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.position.Vec2i;

public class Vec2iConfigWidget extends BaseDualNumberConfigWidget<Vec2i, Vec2iConfig, IntegerEditWidget>
{
    public Vec2iConfigWidget(Vec2iConfig config,
                             DataListEntryWidgetData constructData,
                             ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected BaseDualNumberEditWidget<Vec2i, IntegerEditWidget> createEditWidget(Vec2iConfig config)
    {
        Vec2iEditWidget widget = new Vec2iEditWidget(120, 18, 6, false, config.getValue(), this.config::setValue);
        int minValue = config.getMinValue();
        int maxValue = config.getMaxValue();
        widget.setValidRange(minValue, minValue, maxValue, maxValue);
        return widget;
    }
}

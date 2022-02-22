package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class HotkeyedBooleanConfigWidget extends BaseHotkeyedBooleanConfigWidget<HotkeyedBooleanConfig>
{
    public HotkeyedBooleanConfigWidget(HotkeyedBooleanConfig config,
                                       DataListEntryWidgetData constructData,
                                       ConfigWidgetContext ctx)
    {
        super(config, config, config.getKeyBind(), constructData, ctx);
    }
}

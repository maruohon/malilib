package malilib.gui.widget.list.entry.config;

import malilib.config.option.HotkeyedBooleanConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class HotkeyedBooleanConfigWidget extends BaseHotkeyedBooleanConfigWidget<HotkeyedBooleanConfig>
{
    public HotkeyedBooleanConfigWidget(HotkeyedBooleanConfig config,
                                       DataListEntryWidgetData constructData,
                                       ConfigWidgetContext ctx)
    {
        super(config, config, config.getKeyBind(), constructData, ctx);
    }
}

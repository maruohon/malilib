package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;

public class CustomHotkeyEntryWidget extends BaseKeyBindConfigWidget
{
    public CustomHotkeyEntryWidget(CustomHotkeyDefinition config,
                                   DataListEntryWidgetData constructData,
                                   ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx, config.getKeyBind());
    }
}

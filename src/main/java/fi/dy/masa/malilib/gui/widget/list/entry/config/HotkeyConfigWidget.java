package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class HotkeyConfigWidget extends BaseKeyBindConfigWidget
{
    public HotkeyConfigWidget(HotkeyConfig config,
                              DataListEntryWidgetData constructData,
                              ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx, config.getKeyBind());

        this.keybindButton.setHoverStringProvider("locked", config::getLockAndOverrideMessages);
    }
}

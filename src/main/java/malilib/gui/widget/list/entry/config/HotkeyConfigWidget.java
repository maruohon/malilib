package malilib.gui.widget.list.entry.config;

import malilib.config.option.HotkeyConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

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

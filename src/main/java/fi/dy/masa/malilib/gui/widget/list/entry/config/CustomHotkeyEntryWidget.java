package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;

/**
 * This class is used when the custom hotkeys get pulled to the config search results.
 * This is not used on the Custom Hotkeys config screen,
 * see {@link fi.dy.masa.malilib.gui.widget.list.entry.CustomHotkeyDefinitionEntryWidget} for that.
  */
public class CustomHotkeyEntryWidget extends BaseKeyBindConfigWidget
{
    public CustomHotkeyEntryWidget(CustomHotkeyDefinition config,
                                   DataListEntryWidgetData constructData,
                                   ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx, config.getKeyBind());
    }
}

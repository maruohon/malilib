package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;

public class HotkeyedBooleanConfigWidget extends BaseHotkeyedBooleanConfigWidget
{
    public HotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex,
                                       int originalListIndex, HotkeyedBooleanConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, config, config.getKeyBind(), ctx);
    }
}

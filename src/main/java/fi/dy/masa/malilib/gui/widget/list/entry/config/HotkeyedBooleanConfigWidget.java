package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.input.KeyBind;

public class HotkeyedBooleanConfigWidget extends BaseHotkeyedBooleanConfigWidget<HotkeyedBooleanConfig>
{
    public HotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex,
                                       int originalListIndex, HotkeyedBooleanConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected BooleanConfig getBooleanConfig(HotkeyedBooleanConfig config)
    {
        return this.config;
    }

    @Override
    protected KeyBind getKeyBind(HotkeyedBooleanConfig config)
    {
        return this.config.getKeyBind();
    }
}

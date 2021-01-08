package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;

public interface ConfigOptionWidgetFactory<C extends ConfigInfo>
{
    BaseConfigOptionWidget<? extends ConfigInfo> create(int x, int y, int width, int height, int listIndex,
                                                        int originalListIndex, C config, ConfigWidgetContext ctx);
}

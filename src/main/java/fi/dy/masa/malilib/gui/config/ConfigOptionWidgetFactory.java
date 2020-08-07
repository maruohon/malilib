package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;

public interface ConfigOptionWidgetFactory<C extends ConfigInfo>
{
    BaseConfigOptionWidget<C> create(int x, int y, int width, int height, int listIndex,
                                     int originalListIndex, C config, BaseConfigScreen gui);
}

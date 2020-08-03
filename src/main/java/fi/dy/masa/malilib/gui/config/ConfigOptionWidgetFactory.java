package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;

public interface ConfigOptionWidgetFactory
{
    BaseConfigOptionWidget create(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui);
}

package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MissingConfigTypeFactory implements ConfigOptionWidgetFactory
{
    @Override
    public BaseConfigOptionWidget create(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui)
    {
        BaseConfigOptionWidget widget = new BaseConfigOptionWidget(x, y, width, height, listIndex, config, gui);

        widget.addLabel(x + 10, y + 4, 0xFFFFFFFF, StringUtils.translate(
                "malilib.gui.label_error.no_element_placer_for_config_type", config.getType().getName()));

        return widget;
    }
}

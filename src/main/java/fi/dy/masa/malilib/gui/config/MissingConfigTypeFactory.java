package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MissingConfigTypeFactory implements ConfigOptionWidgetFactory<ConfigInfo>
{
    @Override
    public BaseConfigOptionWidget<ConfigInfo> create(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui)
    {
        BaseConfigOptionWidget<ConfigInfo> widget = new BaseConfigOptionWidget<>(x, y, width, 22, listIndex, config, gui);

        widget.addLabel(x + 2, y + 6, 0xFFFFFFFF, config.getDisplayName());
        // TODO config refactor
        widget.addLabel(x + 120, y + 6, 0xFFFFFFFF, StringUtils.translate(
                "malilib.gui.label_error.no_element_placer_for_config_type", config.getClass().getName()));

        return widget;
    }
}

package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MissingConfigTypeFactory implements ConfigOptionWidgetFactory<ConfigInfo>
{
    @Override
    public BaseConfigOptionWidget<ConfigInfo> create(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui)
    {
        return new MissingConfigOptionWidget(x, y, width, 22, listIndex, config, gui);
    }

    public static class MissingConfigOptionWidget extends BaseConfigOptionWidget<ConfigInfo>
    {
        public MissingConfigOptionWidget(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui)
        {
            super(x, y, width, height, listIndex, config, gui);

            this.reCreateWidgets(x, y);
        }

        @Override
        protected void reCreateWidgets(int x, int y)
        {
            super.reCreateWidgets(x, y);

            x += this.getMaxLabelWidth() + 10;
            this.addLabel(x, y + 6, 0xFFFFFFFF, StringUtils.translate(
                    "malilib.gui.label_error.no_widget_factory_for_config_type", this.data.getClass().getName()));
        }
    }
}

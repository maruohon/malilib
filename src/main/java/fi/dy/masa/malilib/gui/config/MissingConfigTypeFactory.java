package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MissingConfigTypeFactory implements ConfigOptionWidgetFactory<ConfigInfo>
{
    @Override
    public BaseConfigWidget<ConfigInfo> create(int x, int y, int width, int height, int listIndex,
                                               int originalListIndex, ConfigInfo config, ConfigWidgetContext ctx)
    {
        return new MissingConfigWidget(x, y, width, 22, listIndex, originalListIndex, config, ctx);
    }

    public static class MissingConfigWidget extends BaseConfigWidget<ConfigInfo>
    {
        public MissingConfigWidget(int x, int y, int width, int height, int listIndex,
                                   int originalListIndex, ConfigInfo config, ConfigWidgetContext ctx)
        {
            super(x, y, width, height, listIndex, originalListIndex, config, ctx);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();

            int x = this.getElementsStartPosition();
            int y = this.getY();

            this.addLabel(x, y + 7, 0xFFFFFFFF, StringUtils.translate(
                    "malilib.gui.label_error.no_widget_factory_for_config_type", this.data.getClass().getName()));
        }
    }
}

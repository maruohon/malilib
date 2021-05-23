package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
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
        protected final LabelWidget labelWidget;

        public MissingConfigWidget(int x, int y, int width, int height, int listIndex,
                                   int originalListIndex, ConfigInfo config, ConfigWidgetContext ctx)
        {
            super(x, y, width, height, listIndex, originalListIndex, config, ctx);

            String label = StringUtils.translate("malilib.gui.label_error.no_widget_factory_for_config_type",
                                                 this.data.getClass().getName());
            this.labelWidget = new LabelWidget(label);
        }

        @Override
        public void reAddSubWidgets()
        {
            super.reAddSubWidgets();

            this.addWidget(this.labelWidget);
        }

        @Override
        public void updateSubWidgetsToGeometryChanges()
        {
            super.updateSubWidgetsToGeometryChanges();

            int x = this.getElementsStartPosition();
            int y = this.getY();

            this.labelWidget.setPosition(x, y + 7);
        }
    }
}

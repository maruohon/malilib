package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MissingConfigTypeFactory implements ConfigOptionWidgetFactory<ConfigInfo>
{
    @Override
    public BaseConfigWidget<ConfigInfo> create(ConfigInfo config,
                                               DataListEntryWidgetData constructData,
                                               ConfigWidgetContext ctx)
    {
        return new MissingConfigWidget(config, constructData, ctx);
    }

    public static class MissingConfigWidget extends BaseConfigWidget<ConfigInfo>
    {
        protected final LabelWidget labelWidget;

        public MissingConfigWidget(ConfigInfo config,
                                   DataListEntryWidgetData constructData,
                                   ConfigWidgetContext ctx)
        {
            super(config, constructData, ctx);

            String label = StringUtils.translate("malilib.label.config.no_widget_factory_for_config",
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
        public void updateSubWidgetPositions()
        {
            super.updateSubWidgetPositions();

            this.labelWidget.setPosition(this.getElementsStartPosition(), this.getY() + 7);
        }
    }
}

package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.NestedConfig;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;
import fi.dy.masa.malilib.registry.Registry;

public class NestedConfigWidgetFactory implements ConfigOptionWidgetFactory<NestedConfig>
{
    @Override
    public BaseConfigWidget<? extends ConfigInfo> create(NestedConfig config,
                                                         DataListEntryWidgetData constructData,
                                                         ConfigWidgetContext ctx)
    {
        ConfigInfo nestedConfig = config.getConfig();
        ConfigOptionWidgetFactory<ConfigInfo> factory = Registry.CONFIG_WIDGET.getWidgetFactory(nestedConfig);

        return factory.create(nestedConfig, constructData, ctx.withNestingLevel(config.getNestingLevel()));
    }
}

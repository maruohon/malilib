package malilib.gui.config;

import malilib.config.option.ConfigInfo;
import malilib.config.option.NestedConfig;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.config.BaseConfigWidget;
import malilib.registry.Registry;

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

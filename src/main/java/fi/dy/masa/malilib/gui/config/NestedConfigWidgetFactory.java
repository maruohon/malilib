package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.NestedConfig;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;

public class NestedConfigWidgetFactory implements ConfigOptionWidgetFactory<NestedConfig>
{
    @Override
    public BaseConfigWidget<? extends ConfigInfo> create(int x, int y, int width, int height,
                                                         int listIndex, int originalListIndex,
                                                         NestedConfig config, ConfigWidgetContext ctx)
    {
        ConfigInfo nestedConfig = config.getConfig();
        ConfigOptionWidgetFactory<ConfigInfo> factory = ConfigWidgetRegistry.INSTANCE.getWidgetFactory(nestedConfig);
        return factory.create(x, y, width, height, listIndex, originalListIndex,
                              nestedConfig, ctx.withNestingLevel(config.getNestingLevel()));
    }
}

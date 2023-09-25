package malilib.gui.config;

import malilib.gui.widget.list.ConfigOptionListWidget;
import malilib.util.data.ConfigOnTab;

public class ConfigWidgetContext
{
    protected final ConfigOnTab configOnTab;
    protected final ConfigOptionListWidget listWidget;

    public ConfigWidgetContext(ConfigOnTab configOnTab,
                               ConfigOptionListWidget listWidget)
    {
        this.configOnTab = configOnTab;
        this.listWidget = listWidget;
    }

    public ConfigOnTab getConfigOnTab()
    {
        return this.configOnTab;
    }

    public ConfigOptionListWidget getListWidget()
    {
        return this.listWidget;
    }
}

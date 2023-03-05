package malilib.gui.config;

import javax.annotation.Nullable;

import malilib.gui.widget.list.ConfigOptionListWidget;
import malilib.util.data.ConfigOnTab;

public class ConfigWidgetContext
{
    protected final ConfigOnTab configOnTab;
    protected final ConfigOptionListWidget listWidget;
    @Nullable protected final KeybindEditScreen keyBindScreen;

    public ConfigWidgetContext(ConfigOnTab configOnTab,
                               ConfigOptionListWidget listWidget,
                               @Nullable KeybindEditScreen keyBindScreen)
    {
        this.configOnTab = configOnTab;
        this.listWidget = listWidget;
        this.keyBindScreen = keyBindScreen;
    }

    public ConfigOnTab getConfigOnTab()
    {
        return this.configOnTab;
    }

    public ConfigOptionListWidget getListWidget()
    {
        return this.listWidget;
    }

    @Nullable
    public KeybindEditScreen getKeybindEditingScreen()
    {
        return this.keyBindScreen;
    }
}

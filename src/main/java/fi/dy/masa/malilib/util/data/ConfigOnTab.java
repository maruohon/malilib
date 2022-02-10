package fi.dy.masa.malilib.util.data;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class ConfigOnTab
{
    protected final ConfigTab tab;
    protected final ConfigInfo config;

    public ConfigOnTab(ConfigTab tab, ConfigInfo config)
    {
        this.tab = tab;
        this.config = config;
    }

    public ConfigTab getTab()
    {
        return this.tab;
    }

    public ConfigInfo getConfig()
    {
        return this.config;
    }

    public String getConfigPath()
    {
        return this.config.getModInfo().getModId() + "." + this.tab.getName() + "." + this.config.getName();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ConfigOnTab that = (ConfigOnTab) o;

        if (!this.tab.equals(that.tab)) { return false; }
        return this.config.equals(that.config);
    }

    @Override
    public int hashCode()
    {
        int result = this.tab.hashCode();
        result = 31 * result + this.config.hashCode();
        return result;
    }
}

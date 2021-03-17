package fi.dy.masa.malilib.util.data;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class ConfigOnTab
{
    public final ConfigTab tab;
    public final ConfigInfo config;

    public ConfigOnTab(ConfigTab tab, ConfigInfo config)
    {
        this.tab = tab;
        this.config = config;
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

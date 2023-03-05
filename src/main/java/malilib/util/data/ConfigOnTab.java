package malilib.util.data;

import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigTab;

public class ConfigOnTab
{
    protected final ConfigTab tab;
    protected final ConfigInfo config;
    protected final int nestingLevel;

    public ConfigOnTab(ConfigTab tab, ConfigInfo config)
    {
        this(tab, config, 0);
    }

    public ConfigOnTab(ConfigTab tab, ConfigInfo config, int nestingLevel)
    {
        this.tab = tab;
        this.config = config;
        this.nestingLevel = nestingLevel;
    }

    public ConfigTab getTab()
    {
        return this.tab;
    }

    public ConfigInfo getConfig()
    {
        return this.config;
    }

    public int getNestingLevel()
    {
        return this.nestingLevel;
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

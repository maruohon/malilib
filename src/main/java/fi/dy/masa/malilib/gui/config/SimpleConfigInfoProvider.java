package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;

public class SimpleConfigInfoProvider implements ConfigInfoProvider
{
    protected final String prefix;
    protected final String suffix;

    public SimpleConfigInfoProvider(String prefix, String suffix)
    {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getHoverInfo(ConfigInfo config)
    {
        return this.prefix + config.getDisplayName() + this.suffix;
    }
}

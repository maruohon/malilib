package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;

public class ConfigInfoProviderSimple implements IConfigInfoProvider
{
    protected final String prefix;
    protected final String suffix;

    public ConfigInfoProviderSimple(String prefix, String suffix)
    {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getHoverInfo(ConfigOptionWrapper wrapper)
    {
        return this.prefix + wrapper.getConfig().getPrettyName() + this.suffix;
    }
}

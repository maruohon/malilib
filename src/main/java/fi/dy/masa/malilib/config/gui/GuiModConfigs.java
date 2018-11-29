package fi.dy.masa.malilib.config.gui;

import java.util.List;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;

public class GuiModConfigs extends GuiConfigsBase
{
    protected final List<ConfigOptionWrapper> configs;

    public GuiModConfigs(String modId, String title, List<? extends IConfigBase> configs)
    {
        this(modId, title, ConfigOptionWrapper.createFor(configs), false);
    }

    public GuiModConfigs(String modId, String title, List<ConfigOptionWrapper> wrappers, boolean unused)
    {
        super(10, 0, modId, null);

        this.title = title;
        this.configs = wrappers;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 70;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        return this.configs;
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        // NO-OP
    }
}

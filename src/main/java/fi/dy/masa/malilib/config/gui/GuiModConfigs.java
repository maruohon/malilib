package fi.dy.masa.malilib.config.gui;

import java.util.List;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.GuiConfigsBase;

public class GuiModConfigs extends GuiConfigsBase
{
    protected final List<? extends IConfigValue> configs;

    public GuiModConfigs(String modId, String title, List<? extends IConfigValue> configs)
    {
        super(10, 10, modId, null);

        this.title = title;
        this.configs = configs;
    }

    @Override
    public List<? extends IConfigValue> getConfigs()
    {
        return this.configs;
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        // NO-OP
    }
}

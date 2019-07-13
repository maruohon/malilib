package fi.dy.masa.malilib;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.gui.ConfigGuiTabBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;

public class MaLiLibConfigGui extends GuiConfigsBase
{
    private static final ConfigGuiTabBase GENERIC = new ConfigGuiTabBase("malilib.gui.title.generic",   204, true, MaLiLibConfigs.Generic.OPTIONS);
    private static final ConfigGuiTabBase DEBUG   = new ConfigGuiTabBase("malilib.gui.title.debug",      80, false, MaLiLibConfigs.Debug.OPTIONS);

    private static final ImmutableList<IConfigGuiTab> TABS = ImmutableList.of(
            GENERIC,
            DEBUG
    );

    private static IConfigGuiTab tab = GENERIC;

    public MaLiLibConfigGui()
    {
        super(10, 50, MaLiLibReference.MOD_ID, null, TABS, "malilib.gui.title.configs");
    }

    @Override
    public IConfigGuiTab getCurrentTab()
    {
        return tab;
    }

    @Override
    public void setCurrentTab(IConfigGuiTab tab)
    {
        MaLiLibConfigGui.tab = tab;
    }
}

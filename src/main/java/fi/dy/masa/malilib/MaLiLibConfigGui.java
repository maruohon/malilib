package fi.dy.masa.malilib;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.gui.ConfigGuiTabBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;

public class MaLiLibConfigGui extends GuiConfigsBase
{
    private static final ConfigGuiTabBase GENERIC       = new ConfigGuiTabBase("malilib.gui.title.generic",     204, true,  MaLiLibConfigs.Generic.OPTIONS);
    private static final ConfigGuiTabBase DEBUG         = new ConfigGuiTabBase("malilib.gui.title.debug",        80, false, MaLiLibConfigs.Debug.OPTIONS);
    private static final ConfigGuiTabBase ALL_HOTKEYS   = new ConfigGuiTabBase("malilib.gui.title.all_hotkeys", 204, true,  Collections.emptyList());

    private static final ImmutableList<IConfigGuiTab> TABS = ImmutableList.of(
            GENERIC,
            DEBUG,
            ALL_HOTKEYS
    );

    private static IConfigGuiTab tab = GENERIC;

    public MaLiLibConfigGui()
    {
        super(10, 50, MaLiLibReference.MOD_ID, null, TABS, "malilib.gui.title.configs");

        this.setHoverInfoProvider(new ConfigPanelAllHotkeys.HoverInfoProvider(this));
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

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        if (this.getCurrentTab() == ALL_HOTKEYS)
        {
            return ConfigPanelAllHotkeys.createWrappers();
        }

        return super.getConfigs();
    }
}

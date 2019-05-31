package fi.dy.masa.malilib;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.gui.ConfigPanelAllHotkeys;
import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.GuiModConfigs;

public class MaLiLibConfigPanel extends ConfigPanelBase
{
    @Override
    protected String getPanelTitlePrefix()
    {
        return MaLiLibReference.MOD_NAME;
    }

    @Override
    protected void createSubPanels()
    {
        this.addSubPanel((new GuiModConfigs(MaLiLibReference.MOD_ID, "Generic", MaLiLibConfigs.Generic.OPTIONS)).setConfigWidth(240));
        this.addSubPanel((new GuiModConfigs(MaLiLibReference.MOD_ID, "Debug", ImmutableList.of(MaLiLibConfigs.Debug.KEYBIND_DEBUG, MaLiLibConfigs.Debug.KEYBIND_DEBUG_ACTIONBAR))).setConfigWidth(120));
        this.addSubPanel(new ConfigPanelAllHotkeys());
    }
}

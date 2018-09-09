package fi.dy.masa.malilib.config.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.reference.MaLiLibReference;

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
        //this.addSubPanel(new ConfigPanelAllHotkeys(this));
        this.addSubPanel((new GuiModConfigs(MaLiLibReference.MOD_ID, "Debug", ImmutableList.of(KeybindMulti.KEYBIND_DEBUG))).setConfigWidth(120));
    }
}

package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IConfigValue;
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
        this.addSubPanel(new ConfigPanelAllHotkeys(this));
        this.addSubPanel(new ConfigPanelSub(MaLiLibReference.MOD_ID, "Debug", new IConfigValue[] { KeybindMulti.KEYBIND_DEBUG }, this));
    }
}

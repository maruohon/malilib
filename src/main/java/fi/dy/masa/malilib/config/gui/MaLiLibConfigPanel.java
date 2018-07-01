package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.reference.Reference;

public class MaLiLibConfigPanel extends ConfigPanelBase
{
    @Override
    protected String getPanelTitlePrefix()
    {
        return Reference.MOD_NAME;
    }

    @Override
    protected void createSubPanels()
    {
        this.addSubPanel(new ConfigPanelAllHotkeys(this));
        this.addSubPanel(new ConfigPanelSub(Reference.MOD_ID, "Debug", new IConfigValue[] { InputEventHandler.KEYBIND_DEBUG }, this));
    }
}

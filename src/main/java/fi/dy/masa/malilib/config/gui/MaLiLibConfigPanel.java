package fi.dy.masa.malilib.config.gui;

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
    }
}

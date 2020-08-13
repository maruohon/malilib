package fi.dy.masa.malilib;

import fi.dy.masa.malilib.gui.config.ModConfigScreen;
import fi.dy.masa.malilib.gui.config.liteloader.BaseConfigPanel;

public class MaLiLibConfigPanel extends BaseConfigPanel
{
    @Override
    protected String getPanelTitlePrefix()
    {
        return MaLiLibReference.MOD_NAME;
    }

    @Override
    protected void createSubPanels()
    {
        this.addSubPanel((new ModConfigScreen(MaLiLibReference.MOD_ID, MaLiLibConfigs.Generic.OPTIONS,
                                              "malilib.gui.title.generic")).setConfigElementsWidth(240));

        this.addSubPanel((new ModConfigScreen(MaLiLibReference.MOD_ID, MaLiLibConfigs.Debug.OPTIONS,
                                              "malilib.gui.title.debug")).setConfigElementsWidth(120));
    }
}

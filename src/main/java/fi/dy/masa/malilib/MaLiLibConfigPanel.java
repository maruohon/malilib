package fi.dy.masa.malilib;

import fi.dy.masa.malilib.gui.config.liteloader.RedirectingConfigPanel;

public class MaLiLibConfigPanel extends RedirectingConfigPanel
{
    public MaLiLibConfigPanel()
    {
        super(MaLiLibConfigScreen::create);
    }
}

package malilib;

import malilib.gui.config.liteloader.RedirectingConfigPanel;

public class MaLiLibConfigPanel extends RedirectingConfigPanel
{
    public MaLiLibConfigPanel()
    {
        super(MaLiLibConfigScreen::create);
    }
}

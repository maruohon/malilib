package fi.dy.masa.malilib.gui.listener;

import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;

public class ConfigScreenTabButtonListener implements ButtonActionListener
{
    private final BaseConfigScreen gui;
    private final ConfigTab tab;

    public ConfigScreenTabButtonListener(ConfigTab tab, BaseConfigScreen gui)
    {
        this.tab = tab;
        this.gui = gui;
    }

    @Override
    public void actionPerformedWithButton(BaseButton button, int mouseButton)
    {
        this.gui.saveScrollBarPositionForCurrentTab();
        this.gui.setCurrentTab(this.tab);
        this.gui.restoreScrollBarPositionForCurrentTab();
        this.gui.reCreateConfigWidgets();
    }
}

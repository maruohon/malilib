package fi.dy.masa.malilib.gui.listener;

import fi.dy.masa.malilib.gui.button.BaseButton;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigTab;

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
        this.gui.setCurrentTab(this.tab);
        this.gui.reCreateConfigWidgets(); // apply the new config width
        this.gui.getListWidget().resetScrollbarPosition();
        this.gui.initGui();
    }
}

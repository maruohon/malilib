package fi.dy.masa.malilib.gui.listener;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class ButtonListenerConfigGuiTab implements IButtonActionListener
{
    private final BaseConfigScreen gui;
    private final ConfigTab tab;

    public ButtonListenerConfigGuiTab(ConfigTab tab, BaseConfigScreen gui)
    {
        this.tab = tab;
        this.gui = gui;
    }

    @Override
    public void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {
        this.gui.setCurrentTab(this.tab);
        this.gui.reCreateConfigWidgets(); // apply the new config width
        //this.gui.getConfigsListWidget().resetScrollbarPosition(); // TODO config refactor
        this.gui.initGui();
    }
}

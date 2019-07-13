package fi.dy.masa.malilib.gui.listener;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;

public class ButtonListenerConfigGuiTab implements IButtonActionListener
{
    private final GuiConfigsBase gui;
    private final IConfigGuiTab tab;

    public ButtonListenerConfigGuiTab(IConfigGuiTab tab, GuiConfigsBase gui)
    {
        this.tab = tab;
        this.gui = gui;
    }

    @Override
    public void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {
        this.gui.setCurrentTab(this.tab);
        this.gui.reCreateConfigWidgets(); // apply the new config width
        this.gui.getConfigsListWidget().resetScrollbarPosition();
        this.gui.initGui();
    }
}

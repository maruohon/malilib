package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IKeybind;

public class ConfigOptionChangeListenerKeybind implements IButtonActionListener
{
    private final IKeybindConfigGui host;
    private final ConfigButtonKeybind buttonHotkey;
    private final ButtonGeneric button;
    private final IKeybind keybind;

    public ConfigOptionChangeListenerKeybind(IKeybind keybind, ConfigButtonKeybind buttonHotkey, ButtonGeneric button, IKeybindConfigGui host)
    {
        this.buttonHotkey = buttonHotkey;
        this.button = button;
        this.keybind = keybind;
        this.host = host;
    }

    @Override
    public void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {
        this.keybind.resetToDefault();
        this.updateButtons();
        this.host.getButtonPressListener().actionPerformedWithButton(button, mouseButton);
    }

    public void updateButtons()
    {
        this.button.setEnabled(this.keybind.isModified());
        this.buttonHotkey.updateDisplayString();
    }
}

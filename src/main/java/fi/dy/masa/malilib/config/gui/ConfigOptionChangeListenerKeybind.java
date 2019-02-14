package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IKeybind;

public class ConfigOptionChangeListenerKeybind implements IButtonActionListener<ButtonGeneric>
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
    public void actionPerformed(ButtonGeneric control)
    {
        this.keybind.resetToDefault();
        this.updateButtons();
    }

    @Override
    public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
    {
        this.actionPerformed(control);
        this.host.getButtonPressListener().actionPerformedWithButton(control, mouseButton);
    }

    public void updateButtons()
    {
        this.button.enabled = this.keybind.isModified();
        this.buttonHotkey.updateDisplayString();
    }
}

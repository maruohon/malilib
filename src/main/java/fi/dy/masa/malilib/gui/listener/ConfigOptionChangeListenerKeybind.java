package fi.dy.masa.malilib.gui.listener;

import fi.dy.masa.malilib.gui.button.BaseButton;
import fi.dy.masa.malilib.gui.button.GenericButton;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.input.KeyBind;

public class ConfigOptionChangeListenerKeybind implements ButtonActionListener
{
    private final IKeybindConfigGui host;
    private final KeyBindConfigButton buttonHotkey;
    private final GenericButton button;
    private final KeyBind keybind;

    public ConfigOptionChangeListenerKeybind(KeyBind keybind, KeyBindConfigButton buttonHotkey, GenericButton button, IKeybindConfigGui host)
    {
        this.buttonHotkey = buttonHotkey;
        this.button = button;
        this.keybind = keybind;
        this.host = host;
    }

    @Override
    public void actionPerformedWithButton(BaseButton button, int mouseButton)
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

package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.minecraft.client.Minecraft;

public class ConfigOptionListenerResetKeybind implements IButtonActionListener<ButtonGeneric>
{
    private final ConfigPanelSub configPanel;
    private final ConfigButtonKeybind buttonHotkey;
    private final ButtonGeneric button;
    private final IKeybind keybind;

    public ConfigOptionListenerResetKeybind(IKeybind keybind, ConfigButtonKeybind buttonHotkey, ButtonGeneric button, ConfigPanelSub configPanel)
    {
        this.buttonHotkey = buttonHotkey;
        this.button = button;
        this.keybind = keybind;
        this.configPanel = configPanel;
    }

    @Override
    public void actionPerformed(ButtonGeneric control)
    {
        this.keybind.resetToDefault();
        this.buttonHotkey.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        this.updateButtons();
    }

    @Override
    public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
    {
        this.actionPerformed(control);
        this.configPanel.getConfigListener().actionPerformedWithButton(control, mouseButton);
    }

    public void updateButtons()
    {
        this.button.enabled = this.keybind.isModified();
        this.buttonHotkey.updateDisplayString();
    }
}

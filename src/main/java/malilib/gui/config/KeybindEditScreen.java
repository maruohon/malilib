package malilib.gui.config;

import javax.annotation.Nullable;

import malilib.gui.widget.button.KeyBindConfigButton;

public interface KeybindEditScreen
{
    /**
     * Sets or clears the currently active/selected hotkey config button.
     * The active button reference is used for capturing the new hotkey combination,
     * and for updating the button appearance when gaining or losing focus.
     * @param button The keybind config button that is currently active
     */
    void setActiveKeyBindButton(@Nullable KeyBindConfigButton button);
}

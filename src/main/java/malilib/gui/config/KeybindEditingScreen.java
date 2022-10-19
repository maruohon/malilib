package malilib.gui.config;

import javax.annotation.Nullable;
import malilib.gui.widget.button.KeyBindConfigButton;

public interface KeybindEditingScreen
{
    /**
     * Sets or clears the currently active/selected hotkey config button.
     * The active button reference is used for capturing the new hotkey combination,
     * and for updating the button appearance when gaining or losing the focus.
     * @param button
     */
    void setActiveKeyBindButton(@Nullable KeyBindConfigButton button);
}

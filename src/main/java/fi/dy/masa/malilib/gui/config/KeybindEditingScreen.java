package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;

public interface KeybindEditingScreen extends ConfigScreen
{
    /**
     * Sets or clears the currently active/selected hotkey config button.
     * The active button reference is used for capturing the new hotkey combination,
     * and for updating the button appearance when gaining or losing the focus.
     * @param button
     */
    void setActiveKeyBindButton(@Nullable KeyBindConfigButton button);
}

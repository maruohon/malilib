package malilib.input;

public interface Hotkey
{
    /**
     * Returns the name of this hotkey.
     * This is mostly used in the keybind conflict/overlap hover info
     * @return
     */
    String getName();

    /**
     * Returns the pretty/human readable display name for this hotkey.
     * This is used in the overlap info.
     * @return
     */
    String getDisplayName();

    /**
     * Returns the keybind used by this hotkey
     * @return
     */
    KeyBind getKeyBind();

    /**
     * Convenience method for checking if the keybind is currently held/active
     * @return
     */
    default boolean isHeld()
    {
        return this.getKeyBind().isKeyBindHeld();
    }

    /**
     * Returns true if the value has been changed from the default value
     * @return
     */
    default boolean isModified()
    {
        return this.getKeyBind().isModified();
    }

    /**
     * Resets the value back to the default value
     */
    default void resetToDefault()
    {
        this.getKeyBind().resetToDefault();
    }
}

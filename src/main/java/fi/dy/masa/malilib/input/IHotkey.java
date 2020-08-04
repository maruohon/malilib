package fi.dy.masa.malilib.input;

public interface IHotkey
{
    String getName();

    /**
     * Returns the keybind used by this hotkey
     * @return
     */
    IKeyBind getKeyBind();

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

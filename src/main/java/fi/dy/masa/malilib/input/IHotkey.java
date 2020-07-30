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
     * Returns the String representation of the value of this config. Used in the config GUI to
     * fill in the text field contents.
     * @return the String representation of the current value
     */
    default String getStringValue()
    {
        return this.getKeyBind().getStringValue();
    }

    default String getDefaultStringValue()
    {
        return this.getKeyBind().getDefaultStringValue();
    }

    /**
     * Parses the value of this config from a String. Used for example to get the new value from
     * the config GUI textfield.
     * @param value
     */
    default void setValueFromString(String value)
    {
        this.getKeyBind().setValueFromString(value);
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
     * Checks whether or not the given value would be modified from the default value.
     * @param newValue
     * @return
     */
    default boolean isModified(String newValue)
    {
        return this.getKeyBind().isModified(newValue);
    }

    /**
     * Resets the value back to the default value
     */
    default void resetToDefault()
    {
        this.getKeyBind().resetToDefault();
    }
}

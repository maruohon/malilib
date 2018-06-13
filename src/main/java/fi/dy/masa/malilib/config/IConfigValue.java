package fi.dy.masa.malilib.config;

public interface IConfigValue extends IConfigBase
{
    /**
     * Returns the type of this config. Used by the config GUI to determine what kind of control
     * to use for this config.
     * @return the type of this config
     */
    ConfigType getType();

    /**
     * Returns the String representation of the value of this config. Used in the config GUI to
     * fill in the text field contents.
     * @return the String representation of the current value
     */
    String getStringValue();

    /**
     * Parses the value of this config from a String. Used for example to get the new value from
     * the config GUI textfield.
     * @param value
     */
    void setValueFromString(String value);

    /**
     * Returns true if the value has been changed from the default value
     * @return
     */
    boolean isModified();

    /**
     * Checks whether or not the given value would be modified from the default value.
     * @param newValue
     * @return
     */
    boolean isModified(String newValue);

    /**
     * Resets the value back to the default value
     */
    void resetToDefault();
}

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
    public void setValueFromString(String value);
}

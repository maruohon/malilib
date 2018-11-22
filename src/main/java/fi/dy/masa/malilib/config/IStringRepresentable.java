package fi.dy.masa.malilib.config;

public interface IStringRepresentable
{
    /**
     * Returns the String representation of the value of this config. Used in the config GUI to
     * fill in the text field contents.
     * @return the String representation of the current value
     */
    String getStringValue();

    String getDefaultStringValue();

    /**
     * Parses the value of this config from a String. Used for example to get the new value from
     * the config GUI textfield.
     * @param value
     */
    void setValueFromString(String value);

    /**
     * Checks whether or not the given value would be modified from the default value.
     * @param newValue
     * @return
     */
    boolean isModified(String newValue);
}

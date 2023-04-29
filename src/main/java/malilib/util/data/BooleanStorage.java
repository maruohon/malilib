package malilib.util.data;

public interface BooleanStorage
{
    /**
     * @return the current value
     */
    boolean getBooleanValue();

    /**
     * Sets the value.
     * @param value the new value
     * @return true if the value changed (NOT the new value!)
     */
    boolean setBooleanValue(boolean value);

    /**
     * Toggles the value to the opposite value
     * @return true if the value changed (which in this case is always,
     * unless the config is locked or overridden!)
     */
    default boolean toggleBooleanValue()
    {
        return this.setBooleanValue(! this.getBooleanValue());
    }
}

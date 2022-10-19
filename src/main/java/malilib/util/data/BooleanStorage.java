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

    default void toggleBooleanValue()
    {
        this.setBooleanValue(! this.getBooleanValue());
    }
}

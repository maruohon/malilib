package fi.dy.masa.malilib.config.option;

public interface IConfigResettable
{
    /**
     * Returns true if the value has been changed from the default value
     * @return
     */
    boolean isModified();

    /**
     * Resets the value back to the default value
     */
    void resetToDefault();
}

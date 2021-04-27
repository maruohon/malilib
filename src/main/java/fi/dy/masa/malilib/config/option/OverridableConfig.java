package fi.dy.masa.malilib.config.option;

public interface OverridableConfig<T>
{
    /**
     * @return true if this config currently has an active override
     */
    boolean hasOverride();

    /**
     * Enables an override for this config, using the provided override value
     * @param overrideValue the value to force the config's returned/visible value to
     */
    void enableOverrideWithValue(T overrideValue);

    /**
     * Disables the override, restoring the normal value as the
     * returned/visible value of the config.
     */
    void disableOverride();
}

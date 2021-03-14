package fi.dy.masa.malilib.config;

public interface ValueChangeCallback<T>
{
    /**
     * Called after a config's value changes
     * @param newValue the new value the config was set to
     * @param oldValue the old value before the value was changed
     */
    void onValueChanged(T newValue, T oldValue);
}

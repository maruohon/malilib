package fi.dy.masa.malilib.config;

public interface IValueChangeCallback<T>
{
    /**
     * Called after a config's value changes
     * @param newValue the new value that is being set
     * @param oldValue the old value before the value was changed
     */
    void onValueChanged(T newValue, T oldValue);
}

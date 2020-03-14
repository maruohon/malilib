package fi.dy.masa.malilib.interfaces;

public interface IValueChangeCallback<T>
{
    /**
     * Called when (= after) a config's value changes
     * @param newValue the new value that is being set
     * @param oldValue the old value before the value was changed
     */
    void onValueChanged(T newValue, T oldValue);
}

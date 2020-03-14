package fi.dy.masa.malilib.config.options;

import fi.dy.masa.malilib.interfaces.IValueChangeCallback;

public interface IConfigNotifiable<T>
{
    void onValueChanged(T newValue, T oldValue);

    void setValueChangeCallback(IValueChangeCallback<T> callback);
}

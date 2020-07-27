package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.config.IValueChangeCallback;

public interface IConfigNotifiable<T>
{
    void onValueChanged(T newValue, T oldValue);

    void setValueChangeCallback(IValueChangeCallback<T> callback);
}

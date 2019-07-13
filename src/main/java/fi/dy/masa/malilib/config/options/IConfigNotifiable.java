package fi.dy.masa.malilib.config.options;

import fi.dy.masa.malilib.interfaces.IValueChangeCallback;

public interface IConfigNotifiable<T extends IConfigBase>
{
    void onValueChanged();

    void setValueChangeCallback(IValueChangeCallback<T> callback);
}

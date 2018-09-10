package fi.dy.masa.malilib.config;

import fi.dy.masa.malilib.interfaces.IValueChangeCallback;

public interface IConfigValueNotifyable extends IConfigValue
{
    void onValueChanged();

    void setValueChangeCallback(IValueChangeCallback callback);
}

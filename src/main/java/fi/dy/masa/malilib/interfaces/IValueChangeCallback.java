package fi.dy.masa.malilib.interfaces;

import fi.dy.masa.malilib.config.IConfigValue;

public interface IValueChangeCallback
{
    /**
     * Called when (= after) the config's value is changed
     * @param feature
     */
    void onValueChanged(IConfigValue config);
}

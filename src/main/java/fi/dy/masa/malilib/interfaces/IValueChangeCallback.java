package fi.dy.masa.malilib.interfaces;

import fi.dy.masa.malilib.config.options.IConfigBase;

public interface IValueChangeCallback<T extends IConfigBase>
{
    /**
     * Called when (= after) the config's value is changed
     * @param feature
     */
    void onValueChanged(T config);
}

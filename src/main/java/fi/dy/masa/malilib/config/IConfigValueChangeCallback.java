package fi.dy.masa.malilib.config;

public interface IConfigValueChangeCallback
{
    /**
     * Called when (= after) the config's value is changed
     * @param feature
     */
    void onValueChanged(IConfigValue config);
}

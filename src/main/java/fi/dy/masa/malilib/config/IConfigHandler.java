package fi.dy.masa.malilib.config;

public interface IConfigHandler
{
    /**
     * Called when some settings have (potentially) been changed via some of the config GUIs
     */
    void onConfigsChanged();

    /**
     * Called to save any potential config changes to a file
     */
    void save();
}

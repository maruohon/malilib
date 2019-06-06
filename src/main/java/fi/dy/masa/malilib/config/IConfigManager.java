package fi.dy.masa.malilib.config;

public interface IConfigManager
{
    /**
     * Registers a config handler
     * @param modId
     * @param handler
     */
    void registerConfigHandler(String modId, IConfigHandler handler);

    /**
     * Can be called to save and reload the configs for the given mod.
     * @param modId
     */
    void onConfigsChanged(String modId);
}

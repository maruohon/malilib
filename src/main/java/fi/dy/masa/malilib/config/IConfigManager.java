package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;

public interface IConfigManager
{
    /**
     * Registers a config handler
     * @param modId
     * @param handler
     */
    void registerConfigHandler(String modId, IConfigHandler handler);

    /**
     * Get the config handler for the given mod ID
     * @param modId
     * @return
     */
    @Nullable
    IConfigHandler getConfigHandler(String modId);

    /**
     * Can be called to save and reload the configs for the given mod.
     * @param modId
     */
    void onConfigsChanged(String modId);
}

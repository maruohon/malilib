package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;

public interface ConfigManager
{
    ConfigManager INSTANCE = new ConfigManagerImpl();

    /**
     * Registers a config handler
     * @param handler
     */
    void registerConfigHandler(ModConfig handler);

    /**
     * Get the config handler for the given mod ID
     * @param modId
     * @return
     */
    @Nullable
    ModConfig getConfigHandler(String modId);

    /**
     * Can be called to save and reload the configs for the given mod.
     * @param modId
     */
    void onConfigsChanged(String modId);
}

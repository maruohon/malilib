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
     * @return the config handler for the given mod ID, or null if there isn't one
     */
    @Nullable
    ModConfig getConfigHandler(String modId);

    /**
     * Can be called to save the configs for the given mod,
     * if there are any configs that have changed since last saving (or being loaded).
     * @param modId
     * @return true if some setting were dirty and thus the configs got saved to file
     */
    boolean saveConfigsIfChanged(String modId);
}

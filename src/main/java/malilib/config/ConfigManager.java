package malilib.config;

import javax.annotation.Nullable;

import malilib.util.data.ModInfo;

public interface ConfigManager
{
    /**
     * Registers a config handler
     * @param handler
     */
    void registerConfigHandler(ModConfig handler);

    /**
     * Get the config handler for the given mod
     * @param modInfo
     * @return the config handler for the given mod, or null if there isn't one registered
     */
    @Nullable
    ModConfig getConfigHandler(ModInfo modInfo);

    /**
     * Can be called to save the configs for the given mod,
     * if there are any configs that have changed since last saving (or being loaded).
     * @param modInfo
     * @return true if some setting were dirty and thus the configs got saved to file
     */
    boolean saveConfigsIfChanged(ModInfo modInfo);
}

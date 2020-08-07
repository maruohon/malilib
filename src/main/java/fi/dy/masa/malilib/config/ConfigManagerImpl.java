package fi.dy.masa.malilib.config;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ConfigManagerImpl implements ConfigManager
{
    private final Map<String, ModConfig> configHandlers = new HashMap<>();

    ConfigManagerImpl()
    {
    }

    @Override
    public void registerConfigHandler(ModConfig handler)
    {
        final String modId = handler.getModId();

        if (this.configHandlers.containsKey(modId))
        {
            throw new IllegalArgumentException("Tried to override an existing config handler for mod ID '" + modId + "'");
        }

        handler.getConfigsPerCategories().values().forEach((list) -> list.forEach((config) -> config.setModId(modId) ));

        this.configHandlers.put(modId, handler);
    }

    @Override
    @Nullable
    public ModConfig getConfigHandler(String modId)
    {
        return this.configHandlers.get(modId);
    }

    @Override
    public void onConfigsChanged(String modId)
    {
        ModConfig handler = this.configHandlers.get(modId);

        if (handler != null)
        {
            handler.onConfigsChanged();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void loadAllConfigs()
    {
        for (ModConfig handler : this.configHandlers.values())
        {
            handler.load();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void saveAllConfigs()
    {
        for (ModConfig handler : this.configHandlers.values())
        {
            handler.saveIfDirty();
        }
    }
}

package fi.dy.masa.malilib.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager implements IConfigManager
{
    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Map<String, IConfigHandler> configHandlers = new HashMap<>();

    public static IConfigManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void registerConfigHandler(String modId, IConfigHandler handler)
    {
        this.configHandlers.put(modId, handler);
    }

    @Override
    public void onConfigsChanged(String modId)
    {
        IConfigHandler handler = this.configHandlers.get(modId);

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
        for (IConfigHandler handler : this.configHandlers.values())
        {
            handler.load();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void saveAllConfigs()
    {
        for (IConfigHandler handler : this.configHandlers.values())
        {
            handler.save();
        }
    }
}

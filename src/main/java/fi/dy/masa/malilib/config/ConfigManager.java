package fi.dy.masa.malilib.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager
{
    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Map<String, IConfigHandler> configHandlers = new HashMap<>();

    public static ConfigManager getInstance()
    {
        return INSTANCE;
    }

    public void registerConfigHandler(String modId, IConfigHandler handler)
    {
        this.configHandlers.put(modId, handler);
    }

    public void onConfigsChanged(String modId)
    {
        IConfigHandler handler = this.configHandlers.get(modId);

        if (handler != null)
        {
            handler.onConfigsChanged();
        }
    }

    public void saveAllConfigs()
    {
        for (IConfigHandler handler : this.configHandlers.values())
        {
            handler.save();
        }
    }
}

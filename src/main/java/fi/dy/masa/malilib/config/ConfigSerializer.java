package fi.dy.masa.malilib.config;

import com.google.gson.JsonElement;

public interface ConfigSerializer
{
    /**
     * Writes the given config to a JsonElement, usually for example for saving to a config file.
     * @param config
     * @return
     */
    JsonElement serialize(ConfigOption<?> config);
}

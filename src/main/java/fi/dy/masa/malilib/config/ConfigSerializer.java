package fi.dy.masa.malilib.config;

import com.google.gson.JsonElement;

public interface ConfigSerializer
{
    /**
     * Writes the given config to a JsonElement, for saving in a config file.
     * @param config
     * @return
     */
    JsonElement serialize(ConfigOption<?> config);
}

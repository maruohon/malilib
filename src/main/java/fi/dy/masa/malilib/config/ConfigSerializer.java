package fi.dy.masa.malilib.config;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.option.ConfigOption;

public interface ConfigSerializer
{
    /**
     * Writes the given config to a JsonElement, for saving in a config file.
     * @param config
     * @return
     */
    JsonElement serialize(ConfigOption<?> config);
}

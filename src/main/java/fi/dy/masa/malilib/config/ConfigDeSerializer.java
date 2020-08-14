package fi.dy.masa.malilib.config;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.option.ConfigOption;

public interface ConfigDeSerializer
{
    /**
     * Reads the given config from the provided JsonElement
     * @param config
     * @param element
     * @param configName
     */
    void deSerializeConfig(ConfigOption<?> config, JsonElement element, String configName);
}

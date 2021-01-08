package fi.dy.masa.malilib.config;

import com.google.gson.JsonElement;

public interface ConfigDeserializer
{
    /**
     * Reads the given config from the provided JsonElement
     * @param config
     * @param element
     * @param configName
     */
    void deserialize(ConfigOption<?> config, JsonElement element, String configName);
}

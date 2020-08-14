package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfig(JsonObject root, String category, List<? extends ConfigOption<?>> options)
    {
        readConfig(root, category, options, ConfigOption::setValueFromJsonElement);
    }

    public static void readConfig(JsonObject root, String category, List<? extends ConfigOption<?>> options, ConfigDeSerializer deSerializer)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null)
        {
            for (ConfigOption<?> config : options)
            {
                String name = config.getName();

                if (obj.has(name))
                {
                    deSerializer.deSerializeConfig(config, obj.get(name), name);
                }
            }
        }
    }

    public static void writeConfig(JsonObject root, String category, List<? extends ConfigOption<?>> options)
    {
        writeConfig(root, category, options, ConfigOption::getAsJsonElement);
    }

    public static void writeConfig(JsonObject root, String category, List<? extends ConfigOption<?>> options, ConfigSerializer serializer)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (ConfigOption<?> config : options)
        {
            obj.add(config.getName(), serializer.serialize(config));
            config.cacheSavedValue();
        }
    }
}

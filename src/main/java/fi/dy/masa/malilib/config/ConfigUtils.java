package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfigBase(JsonObject root, String category, List<? extends ConfigOption<?>> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null)
        {
            for (ConfigOption<?> config : options)
            {
                String name = config.getName();

                if (obj.has(name))
                {
                    config.setValueFromJsonElement(obj.get(name), name);
                }
            }
        }
    }

    public static void writeConfigBase(JsonObject root, String category, List<? extends ConfigOption<?>> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (ConfigOption<?> option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
            option.cacheSavedValue();
        }
    }
}

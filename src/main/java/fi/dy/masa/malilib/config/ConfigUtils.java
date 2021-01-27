package fi.dy.masa.malilib.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.util.text.TextFormatting;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfig(JsonObject root, ConfigOptionCategory category, int configVersion)
    {
        readConfig(root, category, category.getDeserializer(), configVersion);
    }

    public static void readConfig(JsonObject root, ConfigOptionCategory category,
                                  ConfigDeserializer deSerializer, int configVersion)
    {
        String categoryName = category.getName();
        List<? extends ConfigOption<?>> options = category.getConfigOptions();
        JsonObject obj = JsonUtils.getNestedObject(root, categoryName, false);

        if (obj != null)
        {
            for (ConfigOption<?> config : options)
            {
                String name = config.getName();

                if (obj.has(name))
                {
                    deSerializer.deserialize(config, obj.get(name), name);
                }
                else
                {
                    for (String oldName : config.getOldNames())
                    {
                        if (obj.has(oldName))
                        {
                            deSerializer.deserialize(config, obj.get(oldName), oldName);
                            break;
                        }
                    }
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

    public static void sortConfigsByDisplayName(ArrayList<ConfigInfo> configs)
    {
        configs.sort(Comparator.comparing((c) -> TextFormatting.getTextWithoutFormattingCodes(c.getDisplayName())));
    }
}

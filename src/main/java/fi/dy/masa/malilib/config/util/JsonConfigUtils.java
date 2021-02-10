package fi.dy.masa.malilib.config.util;

import java.io.File;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.serialization.JsonConfigSerializerRegistry;
import fi.dy.masa.malilib.message.MessageType;
import fi.dy.masa.malilib.message.MessageUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public class JsonConfigUtils
{
    public static void loadFromFile(File configFile, List<ConfigOptionCategory> categories)
    {
        JsonElement element = JsonUtils.parseJsonFile(configFile);

        if (element != null && element.isJsonObject())
        {
            JsonObject root = element.getAsJsonObject();
            int configVersion = JsonUtils.getIntegerOrDefault(root, "config_version", -1);

            for (ConfigOptionCategory category : categories)
            {
                readConfigs(root, category, configVersion);
            }
        }
        else
        {
            for (ConfigOptionCategory category : categories)
            {
                for (ConfigOption<?> config : category.getConfigOptions())
                {
                    config.resetToDefault();
                }
            }
        }
    }

    public static void readConfigs(JsonObject root, ConfigOptionCategory category, int configVersion)
    {
        String categoryName = category.getName();
        List<? extends ConfigOption<?>> options = category.getConfigOptions();
        JsonObject obj = JsonUtils.getNestedObject(root, categoryName, false);

        if (obj != null)
        {
            for (ConfigOption<?> config : options)
            {
                tryLoadConfig(obj, config, categoryName);
            }
        }
        else
        {
            for (ConfigOption<?> config : options)
            {
                config.resetToDefault();
            }
        }
    }

    public static <T, C extends ConfigOption<T>> void tryLoadConfig(JsonObject obj, C config, String categoryName)
    {
        JsonConfigSerializerRegistry.JsonConfigDeSerializer<C> deSerializer = JsonConfigSerializerRegistry.INSTANCE.getDeSerializer(config);

        if (deSerializer != null)
        {
            String name = config.getName();

            if (obj.has(name))
            {
                deSerializer.deSerializeConfigValue(config, obj.get(name), name);
                return;
            }
            else
            {
                for (String oldName : config.getOldNames())
                {
                    if (obj.has(oldName))
                    {
                        deSerializer.deSerializeConfigValue(config, obj.get(name), name);
                        return;
                    }
                }
            }
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to get a config de-serializer for '{}'.'{}'", categoryName, config.getName());
        }

        // Reset the config to default if it wasn't successfully read from the config file
        config.resetToDefault();
    }

    public static boolean saveToFile(File configFile, List<ConfigOptionCategory> categories, int configVersion)
    {
        JsonObject root = new JsonObject();
        root.add("config_version", new JsonPrimitive(configVersion));
        boolean success = true;

        for (ConfigOptionCategory category : categories)
        {
            if (category.shouldSaveToFile())
            {
                success &= writeConfigs(root, category);
            }
        }

        if (success == false)
        {
            MessageUtils.showGuiOrInGameMessage(MessageType.ERROR, "malilib.error.failed_to_save_all_configs");
        }

        return JsonUtils.writeJsonToFile(root, configFile);
    }

    public static boolean writeConfigs(JsonObject root, ConfigOptionCategory category)
    {
        String categoryName = category.getName();
        List<? extends ConfigOption<?>> options = category.getConfigOptions();
        JsonObject obj = JsonUtils.getNestedObject(root, categoryName, true);
        boolean success = true;

        for (ConfigOption<?> config : options)
        {
            success &= tryWriteConfig(obj, config, categoryName);
            config.cacheSavedValue();
        }

        return success;
    }

    public static <C extends ConfigInfo> boolean tryWriteConfig(JsonObject obj, C config, String categoryName)
    {
        JsonConfigSerializerRegistry.JsonConfigSerializer<C> serializer = JsonConfigSerializerRegistry.INSTANCE.getSerializer(config);

        if (serializer != null)
        {
            String name = config.getName();
            obj.add(name, serializer.serializeConfigValue(config));
            return true;
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to get a config serializer for '{}'.'{}'", categoryName, config.getName());
        }

        return false;
    }
}

package malilib.config.util;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import malilib.MaLiLib;
import malilib.config.category.ConfigOptionCategory;
import malilib.config.option.ConfigInfo;
import malilib.config.option.ConfigOption;
import malilib.config.serialization.JsonConfigSerializerRegistry.ConfigFromJsonLoader;
import malilib.config.serialization.JsonConfigSerializerRegistry.ConfigToJsonSerializer;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.data.json.JsonUtils;

public class JsonConfigUtils
{
    public static void loadFromFile(Path configFile, List<ConfigOptionCategory> categories,
                                    BiConsumer<Integer, JsonObject> configVersionUpdater)
    {
        JsonElement element = JsonUtils.parseJsonFile(configFile);

        if (element != null && element.isJsonObject())
        {
            JsonObject root = element.getAsJsonObject();
            int configVersion = JsonUtils.getIntegerOrDefault(root, "config_version", 0);
            configVersionUpdater.accept(configVersion, root);

            for (ConfigOptionCategory category : categories)
            {
                readConfigs(root, category);
            }
        }
        else
        {
            categories.forEach(ConfigOptionCategory::resetAllOptionsToDefaults);
        }
    }

    public static void readConfigs(JsonObject root, ConfigOptionCategory category)
    {
        String categoryName = category.getName();
        List<? extends ConfigOption<?>> options = category.getConfigOptions();

        readConfigs(root, categoryName, options, true);
    }

    public static void readConfigs(JsonObject root,
                                   String categoryName,
                                   List<? extends ConfigOption<?>> options,
                                   boolean resetIfNoCategoryData)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, categoryName, false);

        if (obj != null)
        {
            for (ConfigOption<?> config : options)
            {
                tryLoadConfig(obj, config, categoryName);
            }
        }
        else if (resetIfNoCategoryData)
        {
            for (ConfigOption<?> config : options)
            {
                config.resetToDefault();
            }
        }
    }

    public static <T, C extends ConfigOption<T>> void tryLoadConfig(JsonObject obj, C config, String categoryName)
    {
        ConfigFromJsonLoader<C> valueLoader = Registry.JSON_CONFIG_SERIALIZER.getConfigValueLoader(config);

        if (valueLoader != null)
        {
            String name = config.getName();

            if (obj.has(name))
            {
                valueLoader.loadConfigValue(config, obj.get(name));
                return;
            }
            else
            {
                for (String oldName : config.getOldNames())
                {
                    if (obj.has(oldName))
                    {
                        valueLoader.loadConfigValue(config, obj.get(name));
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

        // This needs to be called in case the config did not exist in the file yet, and thus the value load
        // callback was not called from the normal load method. And in such a case the reset method above also
        // would not have changed the value, so a possible value change callback also didn't get called.
        config.onValueLoaded(config.getValue());
    }

    public static boolean saveToFile(Path configFile, List<ConfigOptionCategory> categories, int configVersion)
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
            MessageDispatcher.error().console().translate("malilib.message.error.failed_to_save_all_configs");
        }

        return JsonUtils.writeJsonToFile(root, configFile) && success;
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
        ConfigToJsonSerializer<C> serializer = Registry.JSON_CONFIG_SERIALIZER.getSerializer(config);

        if (serializer != null)
        {
            String name = config.getName();
            obj.add(name, serializer.configValueToJson(config));
            return true;
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to get a config serializer for '{}'.'{}'", categoryName, config.getName());
        }

        return false;
    }
}

package malilib.config.serialization;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.MaLiLib;
import malilib.config.option.BaseGenericConfig;
import malilib.config.option.HotkeyConfig;
import malilib.config.option.HotkeyedBooleanConfig;
import malilib.util.data.json.JsonUtils;

public class JsonConfigDeserializers
{
    public static <T> void loadPrimitiveConfig(JsonElement element,
                                               Function<JsonElement, T> deserializer,
                                               Consumer<T> consumer,
                                               Supplier<String> configNameSupplier)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                consumer.accept(deserializer.apply(element));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}' - not a JSON primitive", configNameSupplier.get(), element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configNameSupplier.get(), element, e);
        }
    }

    public static void loadHotkeyConfig(HotkeyConfig config, JsonElement element)
    {
        config.getKeyBind().setValueFromJsonElement(element, config.getName());
        config.onValueLoaded(config.getKeyBind());
    }

    public static Optional<Boolean> readBooleanFromHotkeyedBoolean(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                return Optional.of(JsonUtils.getBooleanOrDefault(obj, "enabled", false));
            }

            // Fallback support for loading old configs
            if (element.isJsonPrimitive())
            {
                return Optional.of(element.getAsBoolean());
            }
        }
        catch (Exception ignore) {}

        MaLiLib.LOGGER.warn("Failed to read HotkeyedBoolean value from the JSON element '{}'", element);

        return Optional.empty();
    }

    public static void loadHotkeyedBooleanConfig(HotkeyedBooleanConfig config, JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                boolean booleanValue = JsonUtils.getBooleanOrDefault(obj, "enabled", false);

                if (JsonUtils.hasObject(obj, "hotkey"))
                {
                    config.getKeyBind().setValueFromJsonElement(JsonUtils.getNestedObject(obj, "hotkey", false), config.getName());
                }

                config.loadHotkeyedBooleanValueFromConfig(booleanValue);
                return;
            }
            // Fallback support for loading old configs
            else if (element.isJsonPrimitive())
            {
                config.loadHotkeyedBooleanValueFromConfig(element.getAsBoolean());
                return;
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", config.getName(), element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", config.getName(), element, e);
        }

        config.loadValue(config.getDefaultValue());
    }

    public static <T, C extends BaseGenericConfig<T>> void loadConfigValue(C config,
                                                                           JsonElement element,
                                                                           Optional<T> optional)
    {
        if (optional.isPresent())
        {
            config.loadValue(optional.get());
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to load the config value for '{}' from the JSON element '{}'", config.getName(), element);
            config.loadValue(config.getDefaultValue());
        }
    }
}

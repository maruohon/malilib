package fi.dy.masa.malilib.config.serialization;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig.BooleanAndDouble;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig.BooleanAndFile;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.DualColorConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.Vec2dConfig;
import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.json.DataJsonDeserializers;
import fi.dy.masa.malilib.util.data.json.DataJsonSerializers;
import fi.dy.masa.malilib.util.data.json.JsonUtils;
import fi.dy.masa.malilib.util.position.Vec2d;
import fi.dy.masa.malilib.util.position.Vec2i;

public class JsonConfigSerializers
{
    public static JsonElement serializeHotkeyedBooleanConfig(HotkeyedBooleanConfig config)
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(config.getValueForSerialization()));
        obj.add("hotkey", config.getKeyBind().getAsJsonElement());
        return obj;
    }

    public static JsonElement serializeBooleanConfig(BooleanConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeIntegerConfig(IntegerConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeDoubleConfig(DoubleConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeStringConfig(StringConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeColorConfig(ColorConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization().toString());
    }

    public static JsonElement serializeFileConfig(FileConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization().toAbsolutePath().toString());
    }

    public static JsonElement serializeHotkeyConfig(HotkeyConfig config)
    {
        return config.getKeyBind().getAsJsonElement();
    }

    public static JsonElement serializeDualColorConfig(DualColorConfig config)
    {
        return DataJsonSerializers.serializeDualColorValue(config.getValueForSerialization());
    }

    public static JsonElement serializeVec2dConfig(Vec2dConfig config)
    {
        return DataJsonSerializers.serializeVec2dValue(config.getValueForSerialization());
    }

    public static JsonElement serializeVec2iConfig(Vec2iConfig config)
    {
        return DataJsonSerializers.serializeVec2iValue(config.getValueForSerialization());
    }

    public static JsonElement serializeBooleanAndIntConfig(BooleanAndIntConfig config)
    {
        return DataJsonSerializers.serializeBooleanAndIntValue(config.getValueForSerialization());
    }

    public static JsonElement serializeBooleanAndDoubleConfig(BooleanAndDoubleConfig config)
    {
        return DataJsonSerializers.serializeBooleanAndDoubleValue(config.getValueForSerialization());
    }

    public static JsonElement serializeBooleanAndFileConfig(BooleanAndFileConfig config)
    {
        return DataJsonSerializers.serializeBooleanAndFileValue(config.getValueForSerialization());
    }

    public static <T extends OptionListConfigValue> JsonElement serializeOptionListConfig(OptionListConfig<T> config)
    {
        return DataJsonSerializers.serializeOptionListValue(config.getValueForSerialization());
    }

    public static <T> JsonElement serializeValueListConfig(ValueListConfig<T> config)
    {
        return DataJsonSerializers.serializeValueListAsString(config.getValueForSerialization(), config.getToStringConverter());
    }

    public static <T> JsonElement serializeBlackWhiteListConfig(BlackWhiteListConfig<T> config)
    {
        return DataJsonSerializers.serializeBlackWhiteList(config.getValueForSerialization());
    }

    public static <T> void loadPrimitiveConfig(Consumer<T> consumer,
                                               Supplier<T> supplier,
                                               JsonElement element,
                                               String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                consumer.accept(supplier.get());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}' - not a JSON primitive", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }
    }

    public static void loadHotkeyConfig(HotkeyConfig config, JsonElement element, String configName)
    {
        config.getKeyBind().setValueFromJsonElement(element, configName);
        config.onValueLoaded(config.getKeyBind());
    }

    public static void loadHotkeyedBooleanConfig(HotkeyedBooleanConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                boolean booleanValue = JsonUtils.getBooleanOrDefault(obj, "enabled", false);

                if (JsonUtils.hasObject(obj, "hotkey"))
                {
                    config.getKeyBind().setValueFromJsonElement(JsonUtils.getNestedObject(obj, "hotkey", false), configName);
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
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        config.loadValue(config.getDefaultValue());
    }

    public static void loadDualColorConfig(DualColorConfig config, JsonElement element, String configName)
    {
        Optional<Pair<Color4f, Color4f>> optional = DataJsonDeserializers.readDualColorValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadBooleanAndFileConfig(BooleanAndFileConfig config, JsonElement element, String configName)
    {
        Optional<BooleanAndFile> optional = DataJsonDeserializers.readBooleanAndFileValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static <T extends OptionListConfigValue> void loadOptionListConfig(OptionListConfig<T> config, JsonElement element, String configName)
    {
        Optional<T> optional = DataJsonDeserializers.readOptionListValue(element, config.getAllValues());
        loadConfigValue(config, optional, element, configName);
    }

    public static <T> void loadValueListConfig(ValueListConfig<T> config, JsonElement element, String configName)
    {
        Optional<ImmutableList<T>> optional = DataJsonDeserializers.readValueList(element, config.getFromStringConverter());
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadVec2dConfig(Vec2dConfig config, JsonElement element, String configName)
    {
        Optional<Vec2d> optional = DataJsonDeserializers.readVec2dValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadVec2iConfig(Vec2iConfig config, JsonElement element, String configName)
    {
        Optional<Vec2i> optional = DataJsonDeserializers.readVec2iValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadBooleanAndIntConfig(BooleanAndIntConfig config, JsonElement element, String configName)
    {
        Optional<BooleanAndInt> optional = DataJsonDeserializers.readBooleanAndIntValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadBooleanAndDoubleConfig(BooleanAndDoubleConfig config, JsonElement element, String configName)
    {
        Optional<BooleanAndDouble> optional = DataJsonDeserializers.readBooleanAndDoubleValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static <T> void loadBlackWhiteListConfig(BlackWhiteListConfig<T> config, JsonElement element, String configName)
    {
        Optional<BlackWhiteList<T>> optional = DataJsonDeserializers.readBlackWhiteListValue(element, config);
        loadConfigValue(config, optional, element, configName);
    }

    public static <T, C extends BaseGenericConfig<T>> void loadConfigValue(C config,
                                                                           Optional<T> optional,
                                                                           JsonElement element,
                                                                           String configName)
    {
        if (optional.isPresent())
        {
            config.loadValue(optional.get());
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to load the config value for '{}' from the JSON element '{}'", configName, element);
            config.loadValue(config.getDefaultValue());
        }
    }
}

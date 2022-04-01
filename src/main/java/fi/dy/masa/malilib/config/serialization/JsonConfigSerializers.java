package fi.dy.masa.malilib.config.serialization;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig.BooleanAndDouble;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import fi.dy.masa.malilib.config.option.DualColorConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.OptionalDirectoryConfig;
import fi.dy.masa.malilib.config.option.OptionalDirectoryConfig.BooleanAndFile;
import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.position.Vec2i;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;

public class JsonConfigSerializers
{
    public static <T> void loadGenericConfig(Consumer<T> consumer,
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

    public static JsonElement saveDualColorConfig(DualColorConfig config)
    {
        JsonObject obj = new JsonObject();
        obj.add("color1", new JsonPrimitive(config.getFirstColorInt()));
        obj.add("color2", new JsonPrimitive(config.getSecondColorInt()));
        return obj;
    }

    public static void loadDualColorConfig(DualColorConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasInteger(obj, "color1") &&
                    JsonUtils.hasInteger(obj, "color2"))
                {
                    config.loadColorValueFromInts(JsonUtils.getInteger(obj, "color1"),
                                                  JsonUtils.getInteger(obj, "color2"));
                }
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
    }

    public static JsonElement saveHotkeyedBooleanConfig(HotkeyedBooleanConfig config)
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(config.getBooleanValue()));
        obj.add("hotkey", config.getKeyBind().getAsJsonElement());
        return obj;
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
    }

    public static JsonElement saveOptionalDirectoryConfig(OptionalDirectoryConfig config)
    {
        JsonObject obj = new JsonObject();
        BooleanAndFile value = config.getValue();
        obj.add("enabled", new JsonPrimitive(value.booleanValue));
        obj.add("directory", new JsonPrimitive(value.fileValue.getAbsolutePath()));
        return obj;
    }

    public static void loadOptionalDirectoryConfig(OptionalDirectoryConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "enabled") &&
                    JsonUtils.hasString(obj, "directory"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "enabled");
                    File fileValue = new File(JsonUtils.getString(obj, "directory"));
                    config.loadValueFromConfig(new BooleanAndFile(booleanValue, fileValue));
                }
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
    }

    public static <T extends OptionListConfigValue> JsonElement saveOptionListConfig(OptionListConfig<T> config)
    {
        return new JsonPrimitive(config.getValue().getName());
    }

    public static <T extends OptionListConfigValue> void loadOptionListConfig(OptionListConfig<T> config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                config.loadValueFromConfig(BaseOptionListConfigValue.findValueByName(element.getAsString(), config.getAllValues()));
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

    public static <T> JsonElement saveValueListConfig(ValueListConfig<T> config)
    {
        JsonArray arr = new JsonArray();

        for (String str : ValueListConfig.getValuesAsStringList(config.getValue(), config.getToStringConverter()))
        {
            arr.add(new JsonPrimitive(str));
        }

        return arr;
    }

    public static <T> void loadValueListConfig(ValueListConfig<T> config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonArray())
            {
                ImmutableList.Builder<T> builder = ImmutableList.builder();
                List<String> strings = JsonUtils.arrayAsStringList(element.getAsJsonArray());

                for (T value : ValueListConfig.getStringListAsValues(strings, config.getFromStringConverter()))
                {
                    builder.add(value);
                }

                config.loadValueFromConfig(builder.build());
            }
            else
            {
                // Make sure to clear the old value in any case
                config.loadValueFromConfig(ImmutableList.of());
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            // Make sure to clear the old value in any case
            config.loadValueFromConfig(ImmutableList.of());
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }
    }

    public static JsonElement saveVec2iConfig(Vec2iConfig config)
    {
        JsonObject obj = new JsonObject();
        Vec2i vec = config.getValue();

        obj.addProperty("x", vec.x);
        obj.addProperty("y", vec.y);

        return obj;
    }

    public static void loadVec2iConfig(Vec2iConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                int x = JsonUtils.getInteger(obj, "x");
                int y = JsonUtils.getInteger(obj, "y");
                config.loadValueFromConfig(new Vec2i(x, y));
            }
            else
            {
                // Make sure to clear the old value in any case
                config.loadValueFromConfig(config.getDefaultValue());
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            // Make sure to clear the old value in any case
            config.loadValueFromConfig(config.getDefaultValue());
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }
    }

    public static JsonElement saveBooleanAndIntConfig(BooleanAndIntConfig config)
    {
        JsonObject obj = new JsonObject();
        BooleanAndInt value = config.getValue();

        obj.addProperty("b", value.booleanValue);
        obj.addProperty("i", value.intValue);

        return obj;
    }

    public static void loadBooleanAndIntConfig(BooleanAndIntConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "b") && JsonUtils.hasInteger(obj, "i"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "b");
                    int intValue = JsonUtils.getInteger(obj, "i");
                    config.loadValueFromConfig(new BooleanAndInt(booleanValue, intValue));
                }
            }
            else
            {
                // Make sure to clear the old value in any case
                config.loadValueFromConfig(config.getDefaultValue());
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            // Make sure to clear the old value in any case
            config.loadValueFromConfig(config.getDefaultValue());
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }
    }

    public static JsonElement saveBooleanAndDoubleConfig(BooleanAndDoubleConfig config)
    {
        JsonObject obj = new JsonObject();
        BooleanAndDouble value = config.getValue();

        obj.addProperty("b", value.booleanValue);
        obj.addProperty("d", value.doubleValue);

        return obj;
    }

    public static void loadBooleanAndDoubleConfig(BooleanAndDoubleConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "b") && JsonUtils.hasDouble(obj, "d"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "b");
                    double doubleValue = JsonUtils.getDouble(obj, "d");
                    config.loadValueFromConfig(new BooleanAndDouble(booleanValue, doubleValue));
                }
            }
            else
            {
                // Make sure to clear the old value in any case
                config.loadValueFromConfig(config.getDefaultValue());
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            // Make sure to clear the old value in any case
            config.loadValueFromConfig(config.getDefaultValue());
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }
    }

    public static <T> JsonElement saveBlackWhiteListConfig(BlackWhiteListConfig<T> config)
    {
        JsonObject obj = new JsonObject();

        BlackWhiteList<T> list = config.getValue();
        obj.add("type", new JsonPrimitive(list.getListType().getName()));
        obj.add("blacklist", JsonUtils.stringListAsArray(list.getBlackListAsString()));
        obj.add("whitelist", JsonUtils.stringListAsArray(list.getWhiteListAsString()));

        return obj;
    }

    public static <T> void loadBlackWhiteListConfig(BlackWhiteListConfig<T> config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasString(obj, "type") &&
                    JsonUtils.hasArray(obj, "blacklist") &&
                    JsonUtils.hasArray(obj, "whitelist"))
                {
                    UsageRestriction.ListType type = BaseOptionListConfigValue.findValueByName(JsonUtils.getString(obj, "type"), UsageRestriction.ListType.VALUES);
                    List<String> blackListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("blacklist"));
                    List<String> whiteListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("whitelist"));

                    BlackWhiteList<T> list = config.getValue();
                    ValueListConfig<T> blackList = list.getBlackList().copy();
                    ValueListConfig<T> whiteList = list.getWhiteList().copy();

                    blackList.setValue(ValueListConfig.getStringListAsValues(blackListStr, list.getFromStringConverter()));
                    whiteList.setValue(ValueListConfig.getStringListAsValues(whiteListStr, list.getFromStringConverter()));

                    config.loadValueFromConfig(new BlackWhiteList<>(type, blackList, whiteList, list.getToStringConverter(), list.getFromStringConverter()));
                }
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
    }
}

package fi.dy.masa.malilib.config.serialization;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;

public class JsonConfigSerializers
{
    public static JsonElement saveHotkeydBooleanConfig(HotkeyedBooleanConfig config)
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(config.getBooleanValue()));
        obj.add("hotkey", config.getKeyBind().getAsJsonElement());
        return obj;
    }

    public static void loadHotkeydBooleanConfig(HotkeyedBooleanConfig config, JsonElement element, String configName)
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

                config.loadHotkeydBooleanValueFromConfig(booleanValue);
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

    public static JsonElement saveOptionListConfig(OptionListConfig<?> config)
    {
        return new JsonPrimitive(config.getValue().getStringValue());
    }

    public static <T extends ConfigOptionListEntry<T>> void loadOptionListConfig(OptionListConfig<T> config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                config.loadValueFromConfig(config.getValue().fromString(element.getAsString()));
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

    public static <T> JsonElement saveBlackWhiteListConfig(BlackWhiteListConfig<T> config)
    {
        JsonObject obj = new JsonObject();

        BlackWhiteList<T> list = config.getValue();
        obj.add("type", new JsonPrimitive(list.getListType().getStringValue()));
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
                    UsageRestriction.ListType type = BaseConfigOptionListEntry.findValueByName(JsonUtils.getString(obj, "type"), UsageRestriction.ListType.VALUES);
                    List<String> blackListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("blacklist"));
                    List<String> whiteListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("whitelist"));

                    BlackWhiteList<T> list = config.getValue();
                    ValueListConfig<T> blackList = list.getBlackList().copy();
                    ValueListConfig<T> whiteList = list.getWhiteList().copy();

                    blackList.setValues(ValueListConfig.getStringListAsValues(blackListStr, list.getFromStringConverter()));
                    whiteList.setValues(ValueListConfig.getStringListAsValues(whiteListStr, list.getFromStringConverter()));

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

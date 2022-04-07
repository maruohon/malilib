package fi.dy.masa.malilib.util.data.json;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec2i;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;

public class DataJsonDeserializers
{
    public static Optional<Boolean> readBooleanValue(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(element.getAsBoolean());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read boolean value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read boolean value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Integer> readIntValue(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(element.getAsInt());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read int value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read int value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Float> readFloatValue(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(element.getAsFloat());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read float value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read float value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Double> readDoubleValue(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(element.getAsDouble());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read double value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read double value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<String> readStringValue(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(element.getAsString());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read String value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read String value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Pair<Color4f, Color4f>> readDualColorValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasInteger(obj, "color1") &&
                    JsonUtils.hasInteger(obj, "color2"))
                {
                    int value1 = JsonUtils.getInteger(obj, "color1");
                    int value2 = JsonUtils.getInteger(obj, "color2");

                    return Optional.of(Pair.of(Color4f.fromColor(value1), Color4f.fromColor(value2)));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read DualColorConfig value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read DualColorConfig value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<BooleanAndFileConfig.BooleanAndFile> readBooleanAndFileValue(JsonElement element)
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

                    return Optional.of(new BooleanAndFileConfig.BooleanAndFile(booleanValue, fileValue));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BooleanAndFile value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BooleanAndFile value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static <T extends OptionListConfigValue> Optional<T> readOptionListValue(JsonElement element, List<T> allValues)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(BaseOptionListConfigValue.findValueByName(element.getAsString(), allValues));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read OptionList value from the JSON element '{}' - not a JSON primitive", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read OptionList value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static <T> Optional<ImmutableList<T>> readValueList(JsonElement element, Function<String, T> fromStringConverter)
    {
        try
        {
            if (element.isJsonArray())
            {
                ImmutableList.Builder<T> builder = ImmutableList.builder();
                List<String> strings = JsonUtils.arrayAsStringList(element.getAsJsonArray());

                for (T value : ValueListConfig.getStringListAsValues(strings, fromStringConverter))
                {
                    builder.add(value);
                }

                return Optional.of(builder.build());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read a list of values from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read a list of values from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Vec2i> readVec2iValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                int x = JsonUtils.getInteger(obj, "x");
                int y = JsonUtils.getInteger(obj, "y");

                return Optional.of(new Vec2i(x, y));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read Vec2i value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read Vec2i value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<BooleanAndIntConfig.BooleanAndInt> readBooleanAndIntValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "b") &&
                    JsonUtils.hasInteger(obj, "i"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "b");
                    int intValue = JsonUtils.getInteger(obj, "i");

                    return Optional.of(new BooleanAndIntConfig.BooleanAndInt(booleanValue, intValue));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BooleanAndInt value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BooleanAndInt value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<BooleanAndDoubleConfig.BooleanAndDouble> readBooleanAndDoubleValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "b") &&
                    JsonUtils.hasDouble(obj, "d"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "b");
                    double doubleValue = JsonUtils.getDouble(obj, "d");

                    return Optional.of(new BooleanAndDoubleConfig.BooleanAndDouble(booleanValue, doubleValue));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BooleanAndDouble value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BooleanAndDouble value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static <T> Optional<BlackWhiteList<T>> readBlackWhiteListValue(JsonElement element, BlackWhiteListConfig<T> config)
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
                    String typeStr = JsonUtils.getString(obj, "type");
                    UsageRestriction.ListType type = BaseOptionListConfigValue.findValueByName(typeStr, UsageRestriction.ListType.VALUES);
                    List<String> blackListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("blacklist"));
                    List<String> whiteListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("whitelist"));

                    BlackWhiteList<T> list = config.getValue();
                    ValueListConfig<T> blackList = list.getBlackList().copy();
                    ValueListConfig<T> whiteList = list.getWhiteList().copy();

                    blackList.setValue(ValueListConfig.getStringListAsValues(blackListStr, list.getFromStringConverter()));
                    whiteList.setValue(ValueListConfig.getStringListAsValues(whiteListStr, list.getFromStringConverter()));

                    return Optional.of(new BlackWhiteList<>(type, blackList, whiteList, list.getToStringConverter(), list.getFromStringConverter()));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BlackWhiteList value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BlackWhiteList value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }
}

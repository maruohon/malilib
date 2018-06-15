package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfigValues(JsonObject root, String category, List<IConfigValue> options)
    {
        readConfigBase(root, category, ImmutableList.copyOf(options));
    }

    public static void readConfigBase(JsonObject root, String category, List<IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null)
        {
            for (IConfigBase option : options)
            {
                if (obj.has(option.getName()))
                {
                    option.setValueFromJsonElement(obj.get(option.getName()));
                }
            }
        }
    }

    public static void writeConfigValues(JsonObject root, String category, List<IConfigValue> options)
    {
        writeConfigBase(root, category, ImmutableList.copyOf(options));
    }

    public static void writeConfigBase(JsonObject root, String category, List<IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (IConfigBase option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
        }
    }

    /**
     * Creates a wrapper for the provided configs that pretends to be another type.<br>
     * This is useful for example for enum configs, which may contain two values per entry.<br>
     * <b>** WARNING **</b>: The configs in toWrap are assumed to actually implement the
     * interface that the wrapped type is of!! Otherwise things will crash!
     * @param wrappedType
     * @param toWrap
     * @return
     */
    public static IConfigValue[] createConfigWrapperForType(ConfigType wrappedType, IConfigValue[] toWrap)
    {
        IConfigValue[] wrapped = new IConfigValue[toWrap.length];

        for (int i = 0; i < wrapped.length; ++i)
        {
            wrapped[i] = new ConfigTypeWrapper(wrappedType, toWrap[i]);
        }

        return wrapped;
    }
}

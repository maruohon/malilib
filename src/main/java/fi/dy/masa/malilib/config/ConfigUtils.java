package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfigBase(JsonObject root, String category, List<? extends IConfigBase> options)
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

    public static void readHotkeyToggleOptions(JsonObject root, String keyHotkey, String keyBoolean, List<? extends IHotkeyTogglable> options)
    {
        if (JsonUtils.hasObject(root, keyHotkey))
        {
            readConfigBase(root, keyHotkey, options);
        }

        if (JsonUtils.hasObject(root, keyBoolean))
        {
            readConfigBase(root, keyBoolean, options);
        }
    }

    public static void writeConfigBase(JsonObject root, String category, List<? extends IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (IConfigBase option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
        }
    }

    public static void writeHotkeyToggleOptions(JsonObject root, String keyHotkey, String keyBoolean, List<? extends IHotkeyTogglable> options)
    {
        JsonObject objBoolean = JsonUtils.getNestedObject(root, keyBoolean, true);

        for (IConfigBoolean option : options)
        {
            objBoolean.add(option.getName(), new JsonPrimitive(option.getBooleanValue()));
        }

        writeConfigBase(root, keyHotkey, options);
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
    public static List<? extends IConfigValue> createConfigWrapperForType(ConfigType wrappedType, List<? extends IConfigValue> toWrap)
    {
        ImmutableList.Builder<ConfigTypeWrapper> builder = ImmutableList.builder();

        for (int i = 0; i < toWrap.size(); ++i)
        {
            builder.add(new ConfigTypeWrapper(wrappedType, toWrap.get(i)));
        }

        return builder.build();
    }
}

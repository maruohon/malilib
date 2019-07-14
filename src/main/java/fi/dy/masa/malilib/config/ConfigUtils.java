package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.config.options.IConfigValue;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigUtils
{
    public static void readConfigBase(JsonObject root, String category, List<? extends IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null)
        {
            for (IConfigBase config : options)
            {
                String name = config.getName();

                if (obj.has(name))
                {
                    config.setValueFromJsonElement(obj.get(name), name);
                }
            }
        }
    }

    public static void readHotkeys(JsonObject root, String keyHotkey, List<? extends IHotkey> hotkeys)
    {
        JsonObject objHotkeys = JsonUtils.getNestedObject(root, keyHotkey, false);

        if (objHotkeys != null)
        {
            for (IHotkey hotkey : hotkeys)
            {
                String name = hotkey.getName();

                if (objHotkeys.has(name))
                {
                    hotkey.getKeybind().setValueFromJsonElement(objHotkeys.get(name), name);
                }
            }
        }
    }

    public static void writeConfigBase(JsonObject root, String category, List<? extends IConfigBase> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (IConfigBase option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
            option.cacheSavedValue();
        }
    }

    public static void writeHotkeys(JsonObject root, String keyHotkey, List<? extends IHotkey> hotkeys)
    {
        JsonObject objHotkeys = JsonUtils.getNestedObject(root, keyHotkey, true);

        for (IHotkey hotkey : hotkeys)
        {
            objHotkeys.add(hotkey.getName(), hotkey.getKeybind().getAsJsonElement());
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
    public static List<ConfigTypeWrapper> createConfigWrapperForType(ConfigType wrappedType, List<? extends IConfigValue> toWrap)
    {
        ImmutableList.Builder<ConfigTypeWrapper> builder = ImmutableList.builder();

        for (int i = 0; i < toWrap.size(); ++i)
        {
            builder.add(new ConfigTypeWrapper(wrappedType, toWrap.get(i)));
        }

        return builder.build();
    }
}

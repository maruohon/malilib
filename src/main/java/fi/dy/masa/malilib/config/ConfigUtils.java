package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
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

    public static void readHotkeys(JsonObject root, String keyHotkey, List<? extends IHotkey> hotkeys)
    {
        JsonObject objHotkey = JsonUtils.getNestedObject(root, keyHotkey, false);

        if (objHotkey != null)
        {
            for (IHotkey hotkey : hotkeys)
            {
                String keyVal = null;

                if (JsonUtils.hasObject(objHotkey, hotkey.getName()))
                {
                    JsonObject obj = objHotkey.getAsJsonObject(hotkey.getName());

                    if (JsonUtils.hasString(obj, "keys"))
                    {
                        keyVal = obj.get("keys").getAsString();
                    }

                    if (JsonUtils.hasObject(obj, "settings"))
                    {
                        hotkey.getKeybind().setSettings(KeybindSettings.fromJson(obj.getAsJsonObject("settings")));
                    }
                }
                // Backwards compatibility for reading the old simple keybinds
                else if (JsonUtils.hasString(objHotkey, hotkey.getName()))
                {
                    keyVal = JsonUtils.getString(objHotkey, hotkey.getName());
                }

                if (keyVal != null)
                {
                    hotkey.getKeybind().setValueFromString(keyVal);
                }
            }
        }
    }

    public static void readHotkeyToggleOptions(JsonObject root, String keyHotkey, String keyBoolean, List<? extends IHotkeyTogglable> options)
    {
        JsonObject objBoolean = JsonUtils.getNestedObject(root, keyBoolean, false);
        JsonObject objHotkey = JsonUtils.getNestedObject(root, keyHotkey, false);

        if (objHotkey != null)
        {
            readHotkeys(root, keyHotkey, options);
        }

        if (objBoolean != null)
        {
            for (IConfigBoolean option : options)
            {
                if (JsonUtils.hasBoolean(objBoolean, option.getName()))
                {
                    option.setBooleanValue(JsonUtils.getBoolean(objBoolean, option.getName()));
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
        }
    }

    public static void writeHotkeys(JsonObject root, String keyHotkey, List<? extends IHotkey> hotkeys)
    {
        JsonObject objHotkey = JsonUtils.getNestedObject(root, keyHotkey, true);

        for (IHotkey hotkey : hotkeys)
        {
            JsonObject obj = new JsonObject();
            obj.add("keys", new JsonPrimitive(hotkey.getKeybind().getStringValue()));
            obj.add("settings", hotkey.getKeybind().getSettings().toJson());

            objHotkey.add(hotkey.getName(), obj);
        }
    }

    public static void writeHotkeyToggleOptions(JsonObject root, String keyHotkey, String keyBoolean, List<? extends IHotkeyTogglable> options)
    {
        JsonObject objBoolean = JsonUtils.getNestedObject(root, keyBoolean, true);

        for (IConfigBoolean option : options)
        {
            objBoolean.add(option.getName(), new JsonPrimitive(option.getBooleanValue()));
        }

        writeHotkeys(root, keyHotkey, options);
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

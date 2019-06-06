package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigHotkey extends ConfigBase<ConfigHotkey> implements IHotkey
{
    private final IKeybind keybind;

    public ConfigHotkey(String name, String defaultStorageString, String comment)
    {
        this(name, defaultStorageString, comment, name);
    }

    public ConfigHotkey(String name, String defaultStorageString, KeybindSettings settings, String comment)
    {
        this(name, defaultStorageString, settings, comment, StringUtils.splitCamelCase(name));
    }

    public ConfigHotkey(String name, String defaultStorageString, String comment, String prettyName)
    {
        this(name, defaultStorageString, KeybindSettings.DEFAULT, comment, prettyName);
    }

    public ConfigHotkey(String name, String defaultStorageString, KeybindSettings settings, String comment, String prettyName)
    {
        super(ConfigType.HOTKEY, name, comment, prettyName);

        this.keybind = KeybindMulti.fromStorageString(defaultStorageString, settings);
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public String getStringValue()
    {
        return this.keybind.getStringValue();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.keybind.getDefaultStringValue();
    }

    @Override
    public void setValueFromString(String value)
    {
        this.keybind.setValueFromString(value);
    }

    @Override
    public boolean isModified()
    {
        return this.keybind.isModified();
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.keybind.isModified(newValue);
    }

    @Override
    public void resetToDefault()
    {
        this.keybind.resetToDefault();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasString(obj, "keys"))
                {
                    this.keybind.setValueFromString(obj.get("keys").getAsString());
                }

                if (JsonUtils.hasObject(obj, "settings"))
                {
                    this.keybind.setSettings(KeybindSettings.fromJson(obj.getAsJsonObject("settings")));
                }
            }
            // Backwards compatibility with some old hotkeys
            else if (element.isJsonPrimitive())
            {
                this.keybind.setValueFromString(element.getAsString());
            }
            else
            {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();
        obj.add("keys", new JsonPrimitive(this.getKeybind().getStringValue()));
        obj.add("settings", this.getKeybind().getSettings().toJson());
        return obj;
    }
}

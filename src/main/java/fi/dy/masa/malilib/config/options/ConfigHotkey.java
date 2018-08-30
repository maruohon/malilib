package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigHotkey extends ConfigBase implements IHotkey
{
    private final String prettyName;
    private final IKeybind keybind;

    public ConfigHotkey(String name, String defaultStorageString, String comment)
    {
        this(name, defaultStorageString, comment, name);
    }

    public ConfigHotkey(String name, String defaultStorageString, boolean isStrict, String comment)
    {
        this(name, defaultStorageString, true, comment, StringUtils.splitCamelCase(name));
    }

    public ConfigHotkey(String name, String defaultStorageString, String comment, String prettyName)
    {
        this(name, defaultStorageString, true, comment, prettyName);
    }

    public ConfigHotkey(String name, String defaultStorageString, boolean isStrict, String comment, String prettyName)
    {
        super(ConfigType.HOTKEY, name, comment);

        this.prettyName = prettyName;
        this.keybind = KeybindMulti.fromStorageString(defaultStorageString);
        this.keybind.setIsStrict(isStrict);
    }

    @Override
    public String getPrettyName()
    {
        return this.prettyName;
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
            if (element.isJsonPrimitive())
            {
                this.keybind.setValueFromString(element.getAsString());
            }
            else
            {
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.keybind.getStringValue());
    }
}

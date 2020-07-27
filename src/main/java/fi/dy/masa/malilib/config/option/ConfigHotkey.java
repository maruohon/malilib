package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyBindMulti;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigHotkey extends ConfigBase<ConfigHotkey> implements IHotkey
{
    private final KeyBindMulti keybind;

    public ConfigHotkey(String name, String defaultStorageString, String comment)
    {
        this(name, defaultStorageString, comment, name);
    }

    public ConfigHotkey(String name, String defaultStorageString, KeyBindSettings settings, String comment)
    {
        this(name, defaultStorageString, settings, comment, StringUtils.splitCamelCase(name));
    }

    public ConfigHotkey(String name, String defaultStorageString, String comment, String prettyName)
    {
        this(name, defaultStorageString, KeyBindSettings.DEFAULT, comment, prettyName);
    }

    public ConfigHotkey(String name, String defaultStorageString, KeyBindSettings settings, String comment, String prettyName)
    {
        super(ConfigType.HOTKEY, name, comment, prettyName);

        this.keybind = KeyBindMulti.fromStorageString(name, defaultStorageString, settings);
    }

    @Override
    public void setModName(String modName)
    {
        super.setModName(modName);

        this.keybind.setModName(modName);
    }

    @Override
    public IKeyBind getKeyBind()
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
    public boolean isDirty()
    {
        return this.keybind.isDirty();
    }

    @Override
    public void cacheSavedValue()
    {
        this.keybind.cacheSavedValue();
    }

    @Override
    public void resetToDefault()
    {
        this.keybind.resetToDefault();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        this.keybind.setValueFromJsonElement(element, configName);
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return this.keybind.getAsJsonElement();
    }
}

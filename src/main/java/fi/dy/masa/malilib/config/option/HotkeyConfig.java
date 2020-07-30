package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyBindMulti;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class HotkeyConfig extends BaseConfig<HotkeyConfig> implements IHotkey
{
    protected final KeyBindMulti keyBind;

    public HotkeyConfig(String name, String defaultStorageString, String comment)
    {
        this(name, defaultStorageString, comment, name);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings, String comment)
    {
        this(name, defaultStorageString, settings, comment, StringUtils.splitCamelCase(name));
    }

    public HotkeyConfig(String name, String defaultStorageString, String comment, String prettyName)
    {
        this(name, defaultStorageString, KeyBindSettings.DEFAULT, comment, prettyName);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings, String comment, String prettyName)
    {
        super(ConfigType.HOTKEY, name, comment, prettyName);

        this.keyBind = KeyBindMulti.fromStorageString(name, defaultStorageString, settings);
    }

    @Override
    public void setModName(String modName)
    {
        super.setModName(modName);

        this.keyBind.setModName(modName);
    }

    @Override
    public IKeyBind getKeyBind()
    {
        return this.keyBind;
    }

    @Override
    public String getStringValue()
    {
        return this.keyBind.getStringValue();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.keyBind.getDefaultStringValue();
    }

    @Override
    public void setValueFromString(String value)
    {
        this.keyBind.setValueFromString(value);
    }

    @Override
    public boolean isModified()
    {
        return this.keyBind.isModified();
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.keyBind.isModified(newValue);
    }

    @Override
    public boolean isDirty()
    {
        return this.keyBind.isDirty();
    }

    @Override
    public void cacheSavedValue()
    {
        this.keyBind.cacheSavedValue();
    }

    @Override
    public void resetToDefault()
    {
        this.keyBind.resetToDefault();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        this.keyBind.setValueFromJsonElement(element, configName);
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return this.keyBind.getAsJsonElement();
    }
}

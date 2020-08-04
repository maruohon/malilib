package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyBindMulti;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class HotkeyConfig extends BaseConfig<HotkeyConfig> implements IHotkey
{
    protected final KeyBindMulti keyBind;

    public HotkeyConfig(String name, String defaultStorageString)
    {
        this(name, defaultStorageString, name);
    }

    public HotkeyConfig(String name, String defaultStorageString, String comment)
    {
        this(name, defaultStorageString, name, comment);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings)
    {
        this(name, defaultStorageString, settings, name);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings, String comment)
    {
        this(name, defaultStorageString, settings, StringUtils.splitCamelCase(name), comment);
    }

    public HotkeyConfig(String name, String defaultStorageString, String prettyName, String comment)
    {
        this(name, defaultStorageString, KeyBindSettings.DEFAULT, prettyName, comment);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings, String prettyName, String comment)
    {
        super(name, name, prettyName, comment);

        this.keyBind = KeyBindMulti.fromStorageString(name, defaultStorageString, settings);
    }

    @Override
    public void setModId(String modId)
    {
        super.setModId(modId);

        this.keyBind.setModId(modId);
    }

    @Override
    public IKeyBind getKeyBind()
    {
        return this.keyBind;
    }

    @Override
    public boolean isModified()
    {
        return this.keyBind.isModified();
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

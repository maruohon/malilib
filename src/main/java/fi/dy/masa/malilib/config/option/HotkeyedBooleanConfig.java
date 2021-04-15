package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.callback.ToggleBooleanWithMessageKeyCallback;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class HotkeyedBooleanConfig extends BooleanConfig implements Hotkey
{
    protected final KeyBind keyBind;

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey)
    {
        this(name, defaultValue, defaultHotkey, name);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, KeyBindSettings settings)
    {
        this(name, defaultValue, defaultHotkey, settings, name, name);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, defaultHotkey, StringUtils.splitCamelCase(name), comment);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, String prettyName, String comment)
    {
        this(name, defaultValue, defaultHotkey, KeyBindSettings.INGAME_DEFAULT, prettyName, comment);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, KeyBindSettings settings, String prettyName, String comment)
    {
        super(name, defaultValue, prettyName, comment);

        this.keyBind = KeyBindImpl.fromStorageString(defaultHotkey, settings);
        this.keyBind.setCallback(new ToggleBooleanWithMessageKeyCallback(this));

        this.cacheSavedValue();
    }

    @Override
    public KeyBind getKeyBind()
    {
        return this.keyBind;
    }

    @Override
    public void setModInfo(ModInfo modInfo)
    {
        super.setModInfo(modInfo);
        this.keyBind.setNameTranslationKey(this.nameTranslationKey);
        this.keyBind.setModInfo(modInfo);
    }

    @Override
    public BaseConfig setNameTranslationKey(String key)
    {
        this.keyBind.setNameTranslationKey(key);
        return super.setNameTranslationKey(key);
    }

    @Override
    public boolean isModified()
    {
        return super.isModified() || this.keyBind.isModified();
    }

    @Override
    public boolean isDirty()
    {
        return super.isDirty() || this.keyBind.isDirty();
    }

    @Override
    public void cacheSavedValue()
    {
        super.cacheSavedValue();

        // Tis method unfortunately gets called already from the super constructor,
        // before the field is set in this class's constructor.
        if (this.keyBind != null)
        {
            this.keyBind.cacheSavedValue();
        }
    }

    @Override
    public void resetToDefault()
    {
        super.resetToDefault();
        this.keyBind.resetToDefault();
    }

    public void loadHotkeydBooleanValueFromConfig(boolean booleanValue)
    {
        this.booleanValue = booleanValue;
        this.value = booleanValue;
        this.cacheSavedValue();
        this.onValueLoaded(booleanValue);
    }
}

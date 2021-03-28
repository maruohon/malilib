package fi.dy.masa.malilib.config.option;

import java.util.List;
import java.util.Locale;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class HotkeyConfig extends BaseConfigOption<KeyBind> implements Hotkey
{
    protected final KeyBind keyBind;

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
        this(name, defaultStorageString, KeyBindSettings.INGAME_DEFAULT, prettyName, comment);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings, String prettyName, String comment)
    {
        super(name, name, prettyName, comment);

        this.keyBind = KeyBindImpl.fromStorageString(defaultStorageString, settings);
    }

    @Override
    protected String createNameTranslationKey(String modId)
    {
        String nameLower = this.getName().toLowerCase(Locale.ROOT);
        return modId + ".hotkey.name." + nameLower;
    }

    @Override
    protected String createCommentTranslationKey(String modId)
    {
        String nameLower = this.getName().toLowerCase(Locale.ROOT);
        return modId + ".hotkey.comment." + nameLower;
    }

    @Override
    public void setModInfo(ModInfo modInfo)
    {
        super.setModInfo(modInfo);
        this.keyBind.setNameTranslationKey(this.nameTranslationKey);
        this.keyBind.setModInfo(modInfo);
    }

    @Override
    public KeyBind getKeyBind()
    {
        return this.keyBind;
    }

    @Override
    public KeyBind getValue()
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

    public void loadHotkeyValueFromConfig(List<Integer> keys, KeyBindSettings settings)
    {
        this.keyBind.setKeys(keys);
        this.keyBind.setSettings(settings);
        this.cacheSavedValue();
        this.onValueLoaded(this.keyBind);
    }
}

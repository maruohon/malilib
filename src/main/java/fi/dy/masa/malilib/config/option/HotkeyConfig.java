package fi.dy.masa.malilib.config.option;

import java.util.Locale;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class HotkeyConfig extends BaseConfig<HotkeyConfig> implements Hotkey
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
        this(name, defaultStorageString, KeyBindSettings.DEFAULT, prettyName, comment);
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
    public void setModId(String modId)
    {
        super.setModId(modId);
        this.keyBind.setNameTranslationKey(this.nameTranslationKey);
    }

    @Override
    public void setModName(String modName)
    {
        super.setModName(modName);
        this.keyBind.setModName(modName);
    }

    @Override
    public KeyBind getKeyBind()
    {
        return this.keyBind;
    }

    @Override
    public HotkeyConfig getValue()
    {
        return this;
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

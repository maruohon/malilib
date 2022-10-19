package malilib.config.option;

import java.util.Locale;
import malilib.action.Action;
import malilib.action.NamedAction;
import malilib.input.Hotkey;
import malilib.input.KeyBind;
import malilib.input.KeyBindImpl;
import malilib.input.KeyBindSettings;
import malilib.input.callback.HotkeyCallback;
import malilib.util.data.ModInfo;

public class HotkeyConfig extends BaseConfigOption<KeyBind> implements Hotkey
{
    protected final KeyBind keyBind;

    public HotkeyConfig(String name, String defaultStorageString)
    {
        this(name, defaultStorageString, KeyBindSettings.INGAME_DEFAULT);
    }

    public HotkeyConfig(String name, String defaultStorageString, KeyBindSettings settings)
    {
        super(name);
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
    public void setNameTranslationKey(String key)
    {
        this.keyBind.setNameTranslationKey(key);

        super.setNameTranslationKey(key);
    }

    public void setHotkeyCallback(HotkeyCallback callback)
    {
        this.keyBind.setCallback(callback);
    }

    public void createCallbackForAction(Action action)
    {
        this.keyBind.setCallback(HotkeyCallback.of(action));
    }

    public void createCallbackForAction(NamedAction action)
    {
        this.keyBind.setCallback(HotkeyCallback.of(action));
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
}

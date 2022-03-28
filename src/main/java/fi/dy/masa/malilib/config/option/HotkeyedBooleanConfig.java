package fi.dy.masa.malilib.config.option;

import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.action.BooleanToggleAction;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class HotkeyedBooleanConfig extends BooleanConfig implements Hotkey
{
    protected final KeyBind keyBind;
    protected Action toggleAction;

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

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey,
                                 String prettyName, String comment)
    {
        this(name, defaultValue, defaultHotkey, KeyBindSettings.INGAME_DEFAULT, prettyName, comment);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey,
                                 KeyBindSettings settings, String prettyName, String comment)
    {
        super(name, defaultValue, prettyName, comment);

        this.keyBind = KeyBindImpl.fromStorageString(defaultHotkey, settings);

        this.setSpecialToggleMessageFactory(null);
        this.cacheSavedValue();
    }

    @Override
    public KeyBind getKeyBind()
    {
        return this.keyBind;
    }

    public Action getToggleAction()
    {
        return this.toggleAction;
    }

    /**
     * This will replace the default hotkey callback with the variant that takes in the message factory
     */
    public void setSpecialToggleMessageFactory(@Nullable Function<BooleanConfig, String> messageFactory)
    {
        this.toggleAction = BooleanToggleAction.of(this, messageFactory, this.keyBind.getSettings()::getMessageType);
        this.keyBind.setCallback(HotkeyCallback.of(this.toggleAction));
    }

    public void setHotkeyCallback(HotkeyCallback callback)
    {
        this.keyBind.setCallback(callback);
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

        // FIXME This method unfortunately gets called already from the super constructor,
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

    public void loadHotkeyedBooleanValueFromConfig(boolean booleanValue)
    {
        this.value = booleanValue;
        this.cacheSavedValue();
        this.updateEffectiveValue();
        this.onValueLoaded(this.effectiveValue);
    }
}

package malilib.gui.config;

import java.util.function.Function;
import javax.annotation.Nullable;

import malilib.config.option.ConfigInfo;
import malilib.input.KeyBind;
import malilib.util.data.BooleanStorageWithDefault;

public class ConfigSearchInfo<C extends ConfigInfo>
{
    public final boolean hasHotkey;
    public final boolean hasToggle;
    protected Function<C, BooleanStorageWithDefault> booleanConfigGetter = (c) -> null;
    protected Function<C, KeyBind> keyBindGetter = (c) -> null;

    public ConfigSearchInfo(boolean hasToggle, boolean hasHotkey)
    {
        this.hasToggle = hasToggle;
        this.hasHotkey = hasHotkey;
    }

    @Nullable
    public BooleanStorageWithDefault getBooleanStorage(C config)
    {
        return this.booleanConfigGetter.apply(config);
    }

    @Nullable
    public KeyBind getKeyBind(C config)
    {
        return this.keyBindGetter.apply(config);
    }

    public boolean hasEnabledToggle(C config)
    {
        if (this.hasToggle)
        {
            BooleanStorageWithDefault storage = this.booleanConfigGetter.apply(config);
            return storage != null && storage.getBooleanValue();
        }

        return false;
    }

    public boolean hasDisabledToggle(C config)
    {
        if (this.hasToggle)
        {
            BooleanStorageWithDefault storage = this.booleanConfigGetter.apply(config);
            return storage != null && storage.getBooleanValue() == false;
        }

        return false;
    }

    public boolean hasModifiedToggle(C config)
    {
        if (this.hasToggle)
        {
            BooleanStorageWithDefault storage = this.booleanConfigGetter.apply(config);
            // Can't use isModified() here, because it may be checking other
            // things as well, such as in HotkeyedBooleanConfig
            return storage != null && storage.getBooleanValue() != storage.getDefaultBooleanValue();
        }

        return false;
    }

    public boolean hasModifiedHotkey(C config)
    {
        if (this.hasHotkey)
        {
            KeyBind keyBind = this.keyBindGetter.apply(config);
            return keyBind != null && keyBind.isModified();
        }

        return false;
    }

    public boolean hasBoundHotkey(C config)
    {
        if (this.hasHotkey)
        {
            KeyBind keyBind = this.keyBindGetter.apply(config);
            return keyBind != null && keyBind.hasKeys();
        }

        return false;
    }

    public boolean hasUnboundHotkey(C config)
    {
        if (this.hasHotkey)
        {
            KeyBind keyBind = this.keyBindGetter.apply(config);
            return keyBind != null && keyBind.hasKeys() == false;
        }

        return false;
    }

    public ConfigSearchInfo<C> setBooleanStorageGetter(Function<C, BooleanStorageWithDefault> getter)
    {
        this.booleanConfigGetter = getter;
        return this;
    }

    public ConfigSearchInfo<C> setKeyBindGetter(Function<C, KeyBind> getter)
    {
        this.keyBindGetter = getter;
        return this;
    }
}

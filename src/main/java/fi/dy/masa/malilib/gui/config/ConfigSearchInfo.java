package fi.dy.masa.malilib.gui.config;

import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.input.KeyBind;

public class ConfigSearchInfo<C extends ConfigInfo>
{
    public final boolean hasHotkey;
    public final boolean hasToggle;
    protected Function<C, BooleanConfig> booleanConfigGetter = (c) -> null;
    protected Function<C, KeyBind> keyBindGetter = (c) -> null;

    public ConfigSearchInfo(boolean hasToggle, boolean hasHotkey)
    {
        this.hasToggle = hasToggle;
        this.hasHotkey = hasHotkey;
    }

    @Nullable
    public BooleanConfig getBooleanConfig(C config)
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
            BooleanConfig booleanConfig = this.booleanConfigGetter.apply(config);
            return booleanConfig != null && booleanConfig.getBooleanValue();
        }

        return false;
    }

    public boolean hasDisabledToggle(C config)
    {
        if (this.hasToggle)
        {
            BooleanConfig booleanConfig = this.booleanConfigGetter.apply(config);
            return booleanConfig != null && booleanConfig.getBooleanValue() == false;
        }

        return false;
    }

    public boolean hasModifiedToggle(C config)
    {
        if (this.hasToggle)
        {
            BooleanConfig booleanConfig = this.booleanConfigGetter.apply(config);
            // Can't use isModified() here, because it may be checking other
            // things as well, such as in HotkeyedBooleanConfig
            return booleanConfig != null && booleanConfig.isModified();
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
            return keyBind != null && keyBind.getKeys().isEmpty() == false;
        }

        return false;
    }

    public boolean hasUnboundHotkey(C config)
    {
        if (this.hasHotkey)
        {
            KeyBind keyBind = this.keyBindGetter.apply(config);
            return keyBind != null && keyBind.getKeys().isEmpty();
        }

        return false;
    }

    public ConfigSearchInfo<C> setBooleanConfigGetter(Function<C, BooleanConfig> getter)
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

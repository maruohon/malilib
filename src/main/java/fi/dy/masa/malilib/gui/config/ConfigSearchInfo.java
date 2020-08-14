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

    public boolean getToggleStatus(C config)
    {
        BooleanConfig booleanConfig = this.booleanConfigGetter.apply(config);
        return booleanConfig != null && booleanConfig.getBooleanValue();
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

    public interface ToggleStatusGetter<C>
    {
        boolean getToggleStatus(C config);
    }
}

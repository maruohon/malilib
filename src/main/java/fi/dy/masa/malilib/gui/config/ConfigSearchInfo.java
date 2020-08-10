package fi.dy.masa.malilib.gui.config;

import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.input.KeyBind;

public class ConfigSearchInfo<C extends ConfigInfo>
{
    public final boolean hasHotkey;
    public final boolean hasToggle;
    protected ToggleStatusGetter<C> toggleStatusGetter = (c) -> false;
    protected Function<C, KeyBind> keyBindGetter = (c) -> null;

    public ConfigSearchInfo(boolean hasToggle, boolean hasHotkey)
    {
        this.hasToggle = hasToggle;
        this.hasHotkey = hasHotkey;
    }

    public boolean getToggleStatus(C config)
    {
        return this.toggleStatusGetter.getToggleStatus(config);
    }

    @Nullable
    public KeyBind getKeyBind(C config)
    {
        return this.keyBindGetter.apply(config);
    }

    public ConfigSearchInfo<C> setToggleOptionGetter(ToggleStatusGetter<C> getter)
    {
        this.toggleStatusGetter = getter;
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

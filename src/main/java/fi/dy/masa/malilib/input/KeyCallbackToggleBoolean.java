package fi.dy.masa.malilib.input;

import fi.dy.masa.malilib.config.option.BooleanConfig;

public class KeyCallbackToggleBoolean implements IHotkeyCallback
{
    protected final BooleanConfig config;

    public KeyCallbackToggleBoolean(BooleanConfig config)
    {
        this.config = config;
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeyBind key)
    {
        this.config.toggleBooleanValue();
        return true;
    }
}

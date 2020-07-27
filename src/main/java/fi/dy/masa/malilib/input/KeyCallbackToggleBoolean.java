package fi.dy.masa.malilib.input;

import fi.dy.masa.malilib.config.option.IConfigBoolean;

public class KeyCallbackToggleBoolean implements IHotkeyCallback
{
    protected final IConfigBoolean config;

    public KeyCallbackToggleBoolean(IConfigBoolean config)
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

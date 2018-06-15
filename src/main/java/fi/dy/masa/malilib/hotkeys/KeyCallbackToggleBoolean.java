package fi.dy.masa.malilib.hotkeys;

import fi.dy.masa.malilib.config.IConfigBoolean;

public class KeyCallbackToggleBoolean implements IHotkeyCallback
{
    protected final IConfigBoolean config;
    protected final KeyAction action;

    public KeyCallbackToggleBoolean(IConfigBoolean config)
    {
        this(config, KeyAction.PRESS);
    }

    public KeyCallbackToggleBoolean(IConfigBoolean config, KeyAction toggleOnAction)
    {
        this.config = config;
        this.action = toggleOnAction;
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key)
    {
        if (action == this.action)
        {
            this.config.setBooleanValue(this.config.getBooleanValue() == false);
            return true;
        }

        return false;
    }
}

package fi.dy.masa.malilib.hotkeys;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;

public class KeyCallbackToggleBooleanConfigWithMessage extends KeyCallbackToggleBoolean
{
    public KeyCallbackToggleBooleanConfigWithMessage(IConfigBoolean config)
    {
        this(config, KeyAction.PRESS);
    }

    public KeyCallbackToggleBooleanConfigWithMessage(IConfigBoolean config, KeyAction toggleOnAction)
    {
        super(config, toggleOnAction);
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key)
    {
        if (super.onKeyAction(action, key))
        {
            StringUtils.printBooleanConfigToggleMessage(this.config.getPrettyName(), this.config.getBooleanValue());
            return true;
        }

        return false;
    }
}

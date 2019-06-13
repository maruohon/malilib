package fi.dy.masa.malilib.hotkeys;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.util.InfoUtils;

public class KeyCallbackToggleBooleanConfigWithMessage extends KeyCallbackToggleBoolean
{
    public KeyCallbackToggleBooleanConfigWithMessage(IConfigBoolean config)
    {
        super(config);
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key)
    {
        super.onKeyAction(action, key);

        InfoUtils.printBooleanConfigToggleMessage(this.config.getPrettyName(), this.config.getBooleanValue());

        return true;
    }
}

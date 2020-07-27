package fi.dy.masa.malilib.input;

import fi.dy.masa.malilib.config.option.IConfigBoolean;
import fi.dy.masa.malilib.message.MessageUtils;

public class KeyCallbackToggleBooleanConfigWithMessage extends KeyCallbackToggleBoolean
{
    public KeyCallbackToggleBooleanConfigWithMessage(IConfigBoolean config)
    {
        super(config);
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeyBind key)
    {
        super.onKeyAction(action, key);

        MessageUtils.printBooleanConfigToggleMessage(this.config.getPrettyName(), this.config.getBooleanValue());

        return true;
    }
}

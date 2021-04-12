package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class ToggleBooleanWithMessageKeyCallback extends ToggleBooleanKeyCallback
{
    public ToggleBooleanWithMessageKeyCallback(BooleanConfig config)
    {
        super(config);
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        super.onKeyAction(action, key);
        MessageUtils.printBooleanConfigToggleMessage(this.config);
        return ActionResult.SUCCESS;
    }
}

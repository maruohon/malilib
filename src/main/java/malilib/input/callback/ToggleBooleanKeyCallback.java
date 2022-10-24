package malilib.input.callback;

import malilib.input.ActionResult;
import malilib.input.KeyAction;
import malilib.input.KeyBind;
import malilib.util.data.BooleanStorage;

public class ToggleBooleanKeyCallback implements HotkeyCallback
{
    protected final BooleanStorage config;

    public ToggleBooleanKeyCallback(BooleanStorage config)
    {
        this.config = config;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        this.config.toggleBooleanValue();
        return ActionResult.SUCCESS;
    }
}

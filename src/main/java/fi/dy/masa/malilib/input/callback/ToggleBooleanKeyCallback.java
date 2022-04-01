package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.util.data.BooleanStorage;

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

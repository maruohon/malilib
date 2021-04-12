package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;

public class ToggleBooleanKeyCallback implements HotkeyCallback
{
    protected final BooleanConfig config;

    public ToggleBooleanKeyCallback(BooleanConfig config)
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

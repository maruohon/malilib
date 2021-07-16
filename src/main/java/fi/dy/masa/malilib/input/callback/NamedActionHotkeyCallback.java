package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;

public class NamedActionHotkeyCallback implements HotkeyCallback
{
    protected final NamedAction action;

    public NamedActionHotkeyCallback(NamedAction action)
    {
        this.action = action;
    }

    public NamedAction getAction()
    {
        return this.action;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        return this.action.execute();
    }
}

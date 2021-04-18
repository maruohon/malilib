package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;

public class ActionHotkeyCallback implements HotkeyCallback
{
    protected final Action action;

    public ActionHotkeyCallback(Action action)
    {
        this.action = action;
    }

    public Action getAction()
    {
        return this.action;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        return this.action.execute(new ActionContext());
    }
}

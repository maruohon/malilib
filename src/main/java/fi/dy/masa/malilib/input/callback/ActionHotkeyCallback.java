package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;

public class ActionHotkeyCallback implements HotkeyCallback
{
    protected final Action action;

    public ActionHotkeyCallback(Action action)
    {
        this.action = action;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        return this.action.execute(ActionContext.COMMON);
    }
}

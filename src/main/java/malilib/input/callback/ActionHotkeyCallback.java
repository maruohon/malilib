package malilib.input.callback;

import malilib.action.Action;
import malilib.action.ActionContext;
import malilib.input.ActionResult;
import malilib.input.KeyAction;
import malilib.input.KeyBind;

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

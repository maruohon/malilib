package malilib.input.callback;

import malilib.action.NamedAction;
import malilib.input.ActionResult;
import malilib.input.KeyAction;
import malilib.input.KeyBind;

public class NamedActionHotkeyCallback implements HotkeyCallback
{
    protected final NamedAction action;

    public NamedActionHotkeyCallback(NamedAction action)
    {
        this.action = action;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        return this.action.execute();
    }
}

package malilib.action.builtin;

import malilib.action.Action;
import malilib.action.ActionContext;
import malilib.input.ActionResult;
import malilib.listener.EventListener;

public class EventAction implements Action
{
    protected final EventListener listener;

    public EventAction(EventListener listener)
    {
        this.listener = listener;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        this.listener.onEvent();
        return ActionResult.SUCCESS;
    }

    public static EventAction of(EventListener listener)
    {
        return new EventAction(listener);
    }
}

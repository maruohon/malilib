package fi.dy.masa.malilib.action;

import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;

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

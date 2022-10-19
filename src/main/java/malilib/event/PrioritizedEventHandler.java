package malilib.event;

public interface PrioritizedEventHandler
{
    /**
     * Returns the priority of this event handler.
     * This is a simple numeric sort order against any other registered handlers,
     * so lower values come first in the list of registered handlers.
     * The handlers are called in the sorted order.
     * The default priority is 100.
     * @return the priority of this handler, lower values come first
     */
    default int getPriority()
    {
        return 100;
    }
}

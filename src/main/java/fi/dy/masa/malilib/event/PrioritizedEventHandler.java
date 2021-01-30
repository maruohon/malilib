package fi.dy.masa.malilib.event;

public interface PrioritizedEventHandler
{
    /**
     * Returns the priority of this event handler.
     * This is a simple numeric sort order against any other registered handlers,
     * so lower values come first in the list of registered handlers and are
     * thus also called first when handling input.
     * @return the priority of this handler, lower values come first
     */
    default int getPriority()
    {
        return 100;
    }
}

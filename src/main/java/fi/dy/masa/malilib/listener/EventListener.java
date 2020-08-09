package fi.dy.masa.malilib.listener;

public interface EventListener
{
    void onEvent();

    /**
     * Creates a new chained EventListener, where this listener
     * runs first and the provided other listener runs after.
     * @param other
     * @return
     */
    default EventListener chain(EventListener other)
    {
        return () -> {
            this.onEvent();
            other.onEvent();
        };
    }
}

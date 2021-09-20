package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.event.ClientWorldChangeHandler;

public interface ClientWorldChangeEventDispatcher
{
    /**
     * Registers a handler for listening to client world changes.
     * @param listener
     */
    void registerClientWorldChangeHandler(ClientWorldChangeHandler listener);

    /**
     * Un-registers a previously registered client world change handler.
     * @param listener
     */
    void unregisterClientWorldChangeHandler(ClientWorldChangeHandler listener);
}

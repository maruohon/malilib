package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.event.IClientWorldChangeHandler;

public interface IClientWorldChangeEventDispatcher
{
    /**
     * Registers a handler for listening to client world changes.
     * @param listener
     */
    void registerClientWorldChangeHandler(IClientWorldChangeHandler listener);

    /**
     * Un-registers a previously registered client world change handler.
     * @param listener
     */
    void unregisterClientWorldChangeHandler(IClientWorldChangeHandler listener);
}
